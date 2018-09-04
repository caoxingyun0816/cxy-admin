package com.wondertek.mam.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.wondertek.mobilevideo.core.base.Constants;
import com.wondertek.mam.service.SystemConfigService;
import com.wondertek.mobilevideo.core.util.StringUtil;

/** 
 * elasticsearch 相关操作工具类 
 *  
 * @author tangyingjun 
 * @date 2016年11月12日 
 */ 
public class ESUtils {
	/** 
     * es服务器的host 
     */  
    private static final String host = "127.0.0.1";  
  
    /** 
     * es服务器暴露给client的port 
     */  
    private static final int port = 9300;  
  
	/** 
     * es服务器的host 
     */  
    private static final String clusterName = "elasticsearch"; 
    
    /** 
     * jackson用于序列化操作的mapper 
     */  
    static Map<String, String> m = new HashMap<String, String>();       
 
    // 创建私有对象
    private static TransportClient client;
 
    static {
        try {
        	SystemConfigService systemConfigService = (SystemConfigService) Constants.ctx.getBean("systemConfigService");
			String esConfig = StringUtil.null2Str(systemConfigService.getConfigValue("ES_CONFIGURATION"));
			String[] esArray = esConfig.split("\\$");

			String host_name = host;
			int index_port = 9300;
			String cluster_name = clusterName;
			if (esArray.length >=3 ) {//如果已配置ES
				host_name = esArray[0];
				index_port = Integer.parseInt(esArray[1]);
				cluster_name = esArray[2];
			}
			// 设置client.transport.sniff为true来使客户端去嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中，
		    Settings settings = Settings.builder().put(m).put("cluster.name",cluster_name).put("client.transport.sniff", true).build();
            client = TransportClient.builder().settings(settings).build();
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host_name), index_port));
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
 
    // 取得实例
    public static synchronized TransportClient getTransportClient() {
        return client;
    }
    
    //为集群添加新的节点  
    public static synchronized void addNode(String name){  
        try {  
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(name),9300));  
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        }  
    }  
  
    //删除集群中的某个节点  
    public static synchronized void removeNode(String name){  
        try {  
            client.removeTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(name),9300));  
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
        }  
    }     
}
