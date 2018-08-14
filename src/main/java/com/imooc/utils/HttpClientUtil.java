package com.imooc.utils;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.*;

/**
 * Created by caoxingyun on 2018/8/1.
 * HttpClient  org.apache.commons.httpclient 目前不再维护，后续用Apache HttpComponents项目HttpClient
 * 使用HttpClient发送请求、接收响应很简单，一般需要如下几步即可。
    使用方法
 1. 创建HttpClient对象。

 2. 创建请求方法的实例，并指定请求URL。如果需要发送GET请求，创建HttpGet对象；如果需要发送POST请求，创建HttpPost对象。

 3. 如果需要发送请求参数，可调用HttpGet、HttpPost共同的setParams(HetpParams params)方法来添加请求参数；对于HttpPost对象而言，也可调用setEntity(HttpEntity entity)方法来设置请求参数。

 4. 调用HttpClient对象的execute(HttpUriRequest request)发送请求，该方法返回一个HttpResponse。

 5. 调用HttpResponse的getAllHeaders()、getHeaders(String name)等方法可获取服务器的响应头；调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。

 6. 释放连接。无论执行方法是否成功，都必须释放连接
 */
public class HttpClientUtil {

    public static String sendStr(String url,String str) throws IOException {
        HttpClient http = new HttpClient();
        //设置请求参数
        http.getParams().setParameter("http.protocol.content-charset","utg-8");
        http.getParams().setSoTimeout(5000);
        PostMethod  method = null;
        method = new PostMethod(url);
        method.addRequestHeader("Content-Type","text/xml;charset = 'utf-8'");
        ByteArrayInputStream fis = null;
        InputStreamRequestEntity io = null;
        byte[] data = str.getBytes();
        fis = new ByteArrayInputStream(data);
        io = new InputStreamRequestEntity(fis);
        //发送的内容
        method.setRequestEntity(io);
//        method.setRequestBody(str);
        method.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler());
        try {
            int retCode = http.executeMethod(method);
            if(retCode != 301 && retCode !=302){
                String responseStr;
                if(retCode != 200){
                    //获得返回码和返回信息
                    responseStr = "1:errorCode_"+ method.getStatusCode() + "errorCause"+method.getStatusText();
                    return  responseStr;
                }else {
                    responseStr = method.getResponseBodyAsString();
                    return responseStr;
                }
            }else{
                Header locationHeader = method.getResponseHeader("location");
                String location = null;
                String var12;
                if(locationHeader != null) {
                    location = locationHeader.getValue();
                    var12 = "1:errorCode_";
                    return var12;
                } else {
                    var12 = "1:errorCode_";
                    return var12;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //释放连接
            method.releaseConnection();
            if(fis != null) {
                fis.close();
                fis = null;
            }
        }
        return "s";
    }

    /**
     * 发送xml请求到server端
     * @param url xml请求数据地址
     * @param xmlString 发送的xml数据流
     * @return null发送失败，否则返回响应内容
     */
    public static String sendPost(String url,String xmlString){
        //创建httpclient工具对象
        HttpClient client = new HttpClient();
        //创建post请求方法
        PostMethod myPost = new PostMethod(url);
        //设置请求超时时间
        client.setConnectionTimeout(3000*1000);
        String responseString = null;
        try{
            //设置请求头部类型
            myPost.setRequestHeader("Content-Type","text/xml");
            myPost.setRequestHeader("charset","utf-8");
            //设置请求体，即xml文本内容，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
            myPost.setRequestBody(xmlString);
            int statusCode = client.executeMethod(myPost);
            //只有请求成功200了，才做处理
            if(statusCode == HttpStatus.SC_OK){
                InputStream inputStream = myPost.getResponseBodyAsStream();//请求返回的内容，这边肯定是inputstream解析
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                StringBuffer stringBuffer = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    stringBuffer.append(str);
                }
                responseString = stringBuffer.toString();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            myPost.releaseConnection();
        }
        return responseString;
    }

}
