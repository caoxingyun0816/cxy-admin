package com.wondertek.mam.util.network;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wondertek.mam.model.DBComment;
import com.wondertek.mam.model.DBInfo;
 
import java.io.IOException;

public class HttpDouBanRequest {
    private static Logger logger = LoggerFactory.getLogger(HttpDouBanRequest.class);    //日志记录
 
    /**
     * 发送get请求
     * @param url    路径
     * @return
     */
    @SuppressWarnings("deprecation")
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
                jsonResult = JSONObject.fromObject(strResult);
//                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get请求提交失败:" + url+"失败的原因:"+response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url+"失败的原因:抛出了异常!!!!!!", e);
        }
        return jsonResult;
    }
    
    public DBInfo getDbInfo(){
    	getDouBanId();
    	new DBInfo();
    	new DBComment();
		return null;
    	
    }
    
    public JSONObject getDouBanId(){
    	JSONObject json = HttpDouBanRequest.httpGet("http://movie.douban.com/migu/subject_ids.json");
    	return json;
    }
    
   // public static void main(String[] args){
    	//JSONObject json = HttpDouBanRequest.httpGet("http://movie.douban.com/migu/subject_ids.json");
    	//JSONArray ids = json.getJSONArray("ids");
    	//String id = ids.toString();
       // System.out.println(id);
    	/*JSONObject json = HttpDouBanRequest.httpGet("http://api.douban.com/v2/movie/subject/"+1291557+"/reviews?apikey=0edfff6473843ebe2c9ffe2d5a8cbc3a&&start=0&&count=816");
    	String reviews_count = json.getString("count");
    	JSONArray reviews = json.getJSONArray("reviews");
    	System.out.println("size:"+reviews.size());
    	for(int i=0;i<reviews.size();i++){
    		JSONObject review = reviews.getJSONObject(i);
    		System.out.println("review summary:"+review.getString("summary").trim().replaceAll("\r\n", "").replaceAll(" ", ""));
    		System.out.println("review alt----:"+review.getString("alt"));   //长评详情1    
    	}
    	System.out.println("　　「1」　	第一次看《花样年华》".replaceAll(" ", ""));
    	String next_start = json.getString("next_start");
    	System.out.println("next_start:"+next_start);
    	System.out.println("-----------------------subject-------------------------");
    	JSONObject subject = json.getJSONObject("subject");
    	JSONObject subject_rating = subject.getJSONObject("rating");
    	String subject_rating_max = subject_rating.getString("max");            //最高分
    	String subject_rating_average = subject_rating.getString("average");   //平均分
    	JSONArray subject_genres = subject.getJSONArray("genres");    //剧情
    	for(int i=0;i<subject_genres.size();i++){
    		System.out.println("genres:"+subject_genres.getString(i));
    	}
    	String image = subject.getJSONObject("images").getString("large");
    	System.out.println("剧照----:"+image );
    	String title = subject.getString("title");
    	System.out.println("title---------:"+title);
    	JSONArray durations = subject.getJSONArray("durations");
    	for(int i=0;i<durations.size();i++){
    		System.out.println("durations-------:"+durations.getString(i));
    	}
    	String collect_count = subject.getString("collect_count");    //观看人数
    	System.out.println("观看人数-------:"+collect_count);
    	
    	String mainland_pubdate = subject.getString("mainland_pubdate");  //上映日期
    	System.out.println("上映日期---:"+mainland_pubdate);
    	JSONArray pubdates = subject.getJSONArray("pubdates");        //发布日期
    	for(int i=0;i<pubdates.size();i++){
    		System.out.println("pubdates--------:"+pubdates.getString(i));
    	}
    	JSONArray directors = subject.getJSONArray("directors");
    	for(int i=0;i<directors.size();i++){
    		JSONObject director_object = directors.getJSONObject(i);
    		JSONObject director_img = director_object.getJSONObject("avatars");
    		System.out.println("director_object--------:"+director_object.getString("name")+"  "+director_img.getString("large"));
    	}
    	
    	JSONArray casts = subject.getJSONArray("casts");
    	for(int i=0;i<casts.size();i++){
    		JSONObject star = casts.getJSONObject(i);
    		JSONObject avatars = star.getJSONObject("avatars");
    		System.out.println("star-------:"+star.getString("name")+"   "+avatars.getString("large"));
    	}*/
    //}
}