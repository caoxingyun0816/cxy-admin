package com.wondertek.mam.util.HJ;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * Http请求发送工具类，支持https协议
 * 依赖于HttpComponents-Client-4.3.5 组件
 * 对于http请求接收所有的服务端证书，所以重写X509TrustManager类的三个方法
 */
public class HttpClientUtil {
    public static void main(String[] args)throws Exception{
        //sendSSLPostRequest("https://koa.aspire-tech.com");
        sendPostRequest("http://10.12.3.24:8180/admin/");
    }

    /**
     * 向HTTPS地址发送POST请求
     * @param reqURL 请求地址
     * @param params 请求参数
     * @return 响应内容
     */
    public static String sendSSLPostRequest(String reqURL){
        //响应长度
        long responseLength = 0;
        //响应内容
        String responseContent = null;
        //创建默认的httpClient实例
        HttpClient httpClient = new DefaultHttpClient();
        //创建TrustManager ，接收所有的证书
        X509TrustManager xtm = new X509TrustManager(){
            @Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            @Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            @Override
			public X509Certificate[] getAcceptedIssuers() { return null; }
        };
        try {
            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
            SSLContext ctx = SSLContext.getInstance("TLS");

            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[]{xtm}, null);

            //创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);

            //不校验域名
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

            //创建HttpPost
            HttpPost httpPost = new HttpPost(reqURL);

            StringEntity xmlEntity = new StringEntity("测试","GBK");
            httpPost.addHeader("Content-Type", "text/xml");
            httpPost.setEntity(xmlEntity);

            //发送POST请求
            HttpResponse response = httpClient.execute(httpPost);
            //获取响应实体
            HttpEntity entity = response.getEntity();

            if (null != entity) {
                responseLength = entity.getContentLength();
                responseContent = EntityUtils.toString(entity, "GBK");
                //EntityUtils.consume(entity); //Consume response content
            }
            System.out.println("请求地址: " + httpPost.getURI());
            System.out.println("响应状态: " + response.getStatusLine());
            System.out.println("响应长度: " + responseLength);
            System.out.println("响应内容: " + responseContent);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源
        }
        return responseContent;
    }

    public static String sendPostRequest(String reqURL){
        //响应长度
        long responseLength = 0;
        //响应内容
        String responseContent = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post  = new HttpPost(reqURL);
        try {
            StringEntity xmlEntity = new StringEntity("测试", "GBK");
            post.addHeader("Content-Type", "text/xml");
            post.setEntity(xmlEntity);
            HttpResponse response = httpclient.execute(post);
            //获取响应实体
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseLength = entity.getContentLength();
                responseContent = EntityUtils.toString(entity, "GBK");
                //EntityUtils.consume(entity); //Consume response content
            }
            System.out.println("响应状态: " + response.getStatusLine());
            System.out.println("响应长度: " + responseLength);
            System.out.println("响应内容: " + responseContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return responseContent;
    }

    /**
     *
     * @param reqURL
     * @param charEncoding 字符编码：默认为UTF-8
     * @return
     */
    public static String sendPostRequest(String reqURL, String charEncoding){
        //响应长度
        long responseLength = 0;
        //响应内容
        String responseContent = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post  = new HttpPost(reqURL);
        try {
            StringEntity xmlEntity = new StringEntity("测试", charEncoding);
            post.addHeader("Content-Type", "text/xml");
            post.setEntity(xmlEntity);
            HttpResponse response = httpclient.execute(post);
            //获取响应实体
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseLength = entity.getContentLength();
                responseContent = EntityUtils.toString(entity, charEncoding);
                //EntityUtils.consume(entity); //Consume response content
            }
            System.out.println("响应状态: " + response.getStatusLine());
            System.out.println("响应长度: " + responseLength);
            System.out.println("响应内容: " + responseContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return responseContent;
    }
}