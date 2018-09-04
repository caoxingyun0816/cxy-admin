package com.wondertek.mam.util;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;


import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP/HTTPS 请求工具类
 *
 * @author :
 * @version : 2.0.0
 * @date : 2017/4/27
 */


public class HttpUtil {
	private Logger log = Logger.getLogger(HttpUtil.class);
    private static PoolingHttpClientConnectionManager poolManager;
    private static RequestConfig requestConfig;
    private static SocketConfig socketConfig;
    private static int retryCount = 2;
    private static HttpRequestRetryHandler httpRequestRetryHandler;

/**
     * 最大连接数
     */

    private final static int MAX_TOTAL = 200;
    /**
     * 每个目标服务器最大连接数(缺省)
     */

    private final static int DEFAULT_MAX_PER_ROUTE  = 50;

    private final static int DEFAULT_CONN_TIMEOUT = 30000;
    private final static int DEFAULT_CONN_REQUEST_TIMEOUT = 8000;
    private final static int DEFAULT_CONN_SOCKET_TIMEOUT = 30000;

    private final static int DEFAULT_SO_TIMEOUT = 30000;
    private final static int DEFAULT_SO_LINGER = 10;

    static {
        poolManager = new PoolingHttpClientConnectionManager();
        poolManager.setMaxTotal(MAX_TOTAL);
        poolManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

        requestConfig = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_CONN_TIMEOUT)
                .setConnectionRequestTimeout(DEFAULT_CONN_REQUEST_TIMEOUT)
                .setSocketTimeout(DEFAULT_CONN_SOCKET_TIMEOUT)
                .build();

        socketConfig = SocketConfig.custom()
                .setSoKeepAlive(true)
                .setSoTimeout(DEFAULT_SO_TIMEOUT)
                .setSoLinger(DEFAULT_SO_LINGER)
                .build();

        // 请求重试处理
        //httpRequestRetryHandler = new HttpRequestRetryHandler(exception, executionCount, context) -> {
        /*httpRequestRetryHandler = (exception, executionCount, context) -> {
            // 如果已经重试了2次(不包括第一次)，就放弃
            if (executionCount > retryCount) {
                return false;
            }
            // 如果服务器丢掉了连接，那么就重试
            if (exception instanceof NoHttpResponseException) {
                return true;
            }
            // 不要重试SSL握手异常
            if (exception instanceof SSLHandshakeException) {
                return false;
            }
            // 超时
            if (exception instanceof SocketTimeoutException) {
                return true;
            }
            // 目标服务器不可达
            if (exception instanceof UnknownHostException) {
                return false;
            }
            // 连接被拒绝
            if (exception instanceof ConnectTimeoutException) {
                return true;
            }
            // SSL握手异常
            if (exception instanceof SSLException) {
                return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            return request instanceof HttpEntityEnclosingRequest;
        };*/
    }

    private static @Bean
    HttpClientBuilder httpClientBuilder(boolean retryHandler){
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        httpClientBuilder.setConnectionManager(poolManager);
        if(retryHandler) {
            httpClientBuilder.setRetryHandler(httpRequestRetryHandler);
        }
        return httpClientBuilder;
    }

    private static @Bean HttpClientBuilder httpsClientBuilder(boolean retryHandler) throws Exception{
        HttpClientBuilder httpsClientBuilder = HttpClients.custom();
        httpsClientBuilder.setSSLSocketFactory(createSSLConnSocketFactory());
        httpsClientBuilder.setDefaultRequestConfig(requestConfig);
        httpsClientBuilder.setConnectionManager(poolManager);
        if(retryHandler) {
            httpsClientBuilder.setRetryHandler(httpRequestRetryHandler);
        }
        return httpsClientBuilder;
    }

/**
 * 发送 GET 请求，不带参数
 * @param url
 * @return String String
 */

    public static String doGet(String url) throws Exception{
        return doGet(url, new HashMap<String, Object>());
    }

/**
 * 发送 GET 请求，参数为 K-V形式
 * @param url
 * @param params
 * @return String
 */

    public static String doGet(String url, Map<String, Object> params) throws Exception{
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;

        CloseableHttpClient httpClient = getHttpClient(apiUrl, false);
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(apiUrl);
        try {
            httpGet.addHeader("Content-Type", "application/json");
            response = httpClient.execute(httpGet);
            return httpResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if(null != response) {
                response.close();
            }
            httpGet.releaseConnection();
        }
    }

/**
 * 发送 POST 请求（HTTP），不带输入数据
 * @param apiUrl
 * @return String String
 */

    public static String doPost(String apiUrl) throws Exception{
        return doPost(apiUrl, new HashMap<String,Object>());
    }

    public static String doPostRetryHandler(String apiUrl) throws Exception{
        return doPostRetryHandler(apiUrl, new HashMap<String,Object>());
    }

/**
 * 发送 POST 请求，K-V形式
 * @param apiUrl API接口URL
 * @param params 参数map
 * @return String
 */

    public static String doPost(String apiUrl, Map<String, Object> params) throws Exception{
        return doPostByParams(apiUrl, params, false);
    }

    public static String doPostRetryHandler(String apiUrl, Map<String, Object> params) throws Exception{
        return doPostByParams(apiUrl, params, true);
    }

    private static String doPostByParams(String apiUrl, Map<String, Object> params, boolean retryHandler) throws Exception{
        CloseableHttpClient httpClient = getHttpClient(apiUrl, retryHandler);
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        try {
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            return httpResponse(response);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if(null != response) {
                response.close();
            }
            httpPost.releaseConnection();
        }
    }

/**
 * 发送 POST 请求，JSON形式
 * @param apiUrl
 * @param json json对象
 * @return String
 */

    public static String doPost(String apiUrl, String json) throws Exception{
        return doPostByJson(apiUrl, json, false);
    }

    public static String doPostRetryHandler(String apiUrl, String json) throws Exception{
        return doPostByJson(apiUrl, json, true);
    }

    private static String doPostByJson(String apiUrl, String json, boolean retryHandler) throws Exception{
        CloseableHttpClient httpClient = getHttpClient(apiUrl, retryHandler);
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(apiUrl);
//        httpClient.getParams().setIntParameter("http.socket.timeout", 10000);
        try {
            httpPost.setEntity(new StringEntity(json, "UTF-8"));
            httpPost.addHeader("Content-Type", "application/json");
            response = httpClient.execute(httpPost);
            return httpResponse(response);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if(null != response) {
                response.close();
            }
            httpPost.releaseConnection();
        }
    }

/**
 * 创建SSL安全连接
 */

    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }
                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }
                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

/**
 * 判断HTTP/HTTPS
 * @param url
 * @return String
 * @throws Exception
 */

    private static CloseableHttpClient getHttpClient(String url, boolean retryHandler) throws Exception{
        CloseableHttpClient httpClient;
        if(url.startsWith("https://")){
            httpClient = httpsClientBuilder(retryHandler).build();
        }else{
            httpClient = httpClientBuilder(retryHandler).build();
        }
        return httpClient;
    }

/**
 * 处理 response
 * @param response
 * @return String
 * @throws Exception
 */

    private static String httpResponse(CloseableHttpResponse response) throws Exception{
        StringBuilder sb = new StringBuilder();
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode != HttpStatus.SC_OK){
            throw new Exception("HttpUtil response error, code=["+statusCode+"]");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        return sb.toString();
    }

}
