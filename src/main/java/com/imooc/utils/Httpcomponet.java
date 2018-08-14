package com.imooc.utils;

import com.alibaba.fastjson.JSONObject; //阿里fastjson
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Apache HttpComponents项目HttpClient和的HttpCore   目前还在更新维护的
 * Created by caoxingyun on 2018/8/1.
 * 创建CloseableHttpClient对象。
 创建请求方法的实例，并指定请求URL。如果需要发送GET请求，创建HttpGet对象；如果需要发送POST请求，创建HttpPost对象。
 如果需要发送请求参数，可可调用setEntity(HttpEntity entity)方法来设置请求参数。setParams方法已过时（4.4.1版本）。被标记为过时的方法，在当前版本中还可以使用，不过会在将来某个版本中被完全废弃掉(塞完全不能用)。
 所以如果有替代方法，尽量不要去用被标记为过时的方法。
 调用HttpGet、HttpPost对象的setHeader(String name, String value)方法设置header信息，或者调用setHeaders(Header[] headers)设置一组header信息。
 调用CloseableHttpClient对象的execute(HttpUriRequest request)发送请求，该方法返回一个CloseableHttpResponse。
 调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容；调用CloseableHttpResponse的getAllHeaders()、getHeaders(String name)等方法可获取服务器的响应头。
 释放连接。无论执行方法是否成功，都必须释放连接

 请求的时候可以用String或流，解析返回信息时，也可以直接用获得String或流，因为jar中这些转换已经写好了，我们只需要根据我们的需求选择就可以了
 */
public class Httpcomponet {

    public static Logger log = LoggerFactory.getLogger(Httpcomponet.class);

    /***
     * 发送http post 请求
     * @param url
     * @param data   xml/json
     * @param map   请求参数
     * @return
     * @throws IOException
     */
    public static String send(String url,String data,Map<String,Object> map) throws IOException {
        String body = null;
        CloseableHttpResponse response = null;
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        //post方法请求对象
        HttpPost post = new HttpPost(url);

        //填充参数
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //如果存在参数，则将参数放入LIST中。
        if(map != null){
            for(Map.Entry<String,Object> entry : map.entrySet()){
                params.add(new BasicNameValuePair(entry.getKey(),entry.getKey()));
            }
        }
        InputStreamEntity entity = null;
        //  InputStreamEntity  UrlEncodedFormEntity StringEntity   都是httpEntity的实现类
        //发送xml
        if(data != null){
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.getBytes());
            entity  = new InputStreamEntity(byteArrayInputStream);
        }
        //将参数放到请求对象中
        try {
            post.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
            //总结，对于发送xml/jsoN格式的，可以直接将str，用StringEntity封装；也可以用流的形式，将String转换成流，用InputStreamEntity封装
            //HttpEntity实体即可以使流也可以使字符串形式。
            //post.setEntity(entity);//发送xml,以流的方式。 InputStreamEntity
            post.setEntity(new StringEntity(data, Charset.forName("UTF-8")));////发送xml,以String的方式。 直接用StringEntity
            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            post.setHeader("Content-Type","application/xml");//发送xml/json 记得修改type
            //post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            log.info("请求参数：" + params);
            log.info("请求数据: "+ data);
            log.info("请求地址" + url);
//            //内容类型
//            System.out.println(entity.getContentType());
//            //内容的编码格式
//            System.out.println(entity.getContentEncoding());
//            //内容的长度
//            System.out.println(entity.getContentLength());
//            //把内容转成字符串
//            System.out.println(EntityUtils.toString(entity));
//            //内容转成字节数组
//            System.out.println(EntityUtils.toByteArray(entity).length);
//            //还有个直接获得流
//            //entity.getContent();
            //执行
            response = client.execute(post);
            //获得返回实体信息
            HttpEntity httpEntity = response.getEntity();

            if(httpEntity != null){
                //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(httpEntity,"utf-8");
                //内容转成字节数组
//                EntityUtils.toByteArray(entity);
                //还有个直接获得流，以上的转换都是建立先获取输出流的操作之下的
//            //InputStream input = entity.getContent();

            }
            EntityUtils.consume(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //释放链接
            response.close();
        }
        return body;
    }

    /**

     * httpPost

     * @param url  路径

     * @param jsonParam 参数

     * @return

     */

    public static JSONObject httpPost(String url, JSONObject jsonParam){

        return httpPost(url, jsonParam, false);

    }



    /**

     * post请求

     * @param url         url地址

     * @param jsonParam     参数

     * @param noNeedResponse    不需要返回结果

     * @return

     */

    public static JSONObject httpPost(String url,JSONObject jsonParam, boolean noNeedResponse){

        //post请求返回结果

        CloseableHttpClient httpClient = HttpClients.createDefault();;

        JSONObject jsonResult = null;

        HttpPost method = new HttpPost(url);

        try {

            if (null != jsonParam) {

                //解决中文乱码问题

                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");

                entity.setContentEncoding("UTF-8");

                entity.setContentType("application/json");

                method.setEntity(entity);

            }

            HttpResponse result = httpClient.execute(method);

            url = URLDecoder.decode(url, "UTF-8");

            /**请求发送成功，并得到响应**/

            if (result.getStatusLine().getStatusCode() == 200) {

                String str = "";

                try {

                    /**读取服务器返回过来的json字符串数据**/

                    str = EntityUtils.toString(result.getEntity());

                    if (noNeedResponse) {

                        return null;

                    }

                    /**把json字符串转换成json对象**/

                    jsonResult = JSONObject.parseObject(str);

                } catch (Exception e) {

                    log.error("post请求提交失败:" + url, e);

                }

            }

        } catch (IOException e) {

            log.error("post请求提交失败:" + url, e);

        }

        return jsonResult;
    }





    /**

     * 发送get请求

     * @param url    路径

     * @return

     */

    public static JSONObject httpGet(String url){

        //get请求返回结果

        JSONObject jsonResult = null;

        try {

            DefaultHttpClient client = new DefaultHttpClient();

            //发送get请求

            HttpGet request = new HttpGet(url);

            HttpResponse response = client.execute(request);



            /**请求发送成功，并得到响应**/

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                /**读取服务器返回过来的json字符串数据**/

                String strResult = EntityUtils.toString(response.getEntity());

                /**把json字符串转换成json对象**/

//                jsonResult = JSONObject.fromObject(strResult);

                url = URLDecoder.decode(url, "UTF-8");

            } else {

                log.error("get请求提交失败:" + url);

            }

        } catch (IOException e) {

            log.error("get请求提交失败:" + url, e);

        }

        return jsonResult;

    }

}
