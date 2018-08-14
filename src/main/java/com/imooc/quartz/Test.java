package com.imooc.quartz;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.imooc.entity.AssetList;
import com.imooc.entity.XmlParse;
import com.imooc.utils.FileUtil;
import com.imooc.utils.XmlUtil;
import org.apache.commons.digester3.Digester;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;


/**
 * servlet 解析 httpclient post/xml 数据的请求
 * @author cxy
 *
 */
public class Test extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(Test.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
    {
//    	//解析json 格式
//		BufferedReader reader = request.getReader();
//		StringBuffer sb = new StringBuffer();
//		String line = null;
//		while ((line = reader.readLine()) != null){
//			sb.append(line);
//		}
//		log.info("json:" + sb.toString());
//		JSONObject jsonObject = JSONObject.parseObject(sb.toString());
//		String accessId = jsonObject.get("accessId").toString();

		// 返回响应给客户端
		PrintWriter out = null;
		String xmlStr = FileUtil.parseInputStreamToString(request.getInputStream());
		log.info(xmlStr);
		//xml 转换成对应的 bean  Digester 解析
		XmlParse xml = (XmlParse) XmlUtil.parseAsset(xmlStr);

		//dom4j 解析xml
//		Document doc = null;
//		try {
//			doc = DocumentHelper.parseText(xmlStr);
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		Element root = doc.getRootElement();
//		Element header = root.element("header");
//		String timeStampTemp = "";
//		if (header != null) {
//			timeStampTemp = (header.attributeValue("timeStamp"));
//		}
//		Element body = root.element("body");
//		Element result = body.element("result");
//		String code = "";
//		String description = "";
//		if (result != null) {
//			code = result.attributeValue("code");// 错误码，1为成功
//			description = result.attributeValue("description");
//		}

		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		String returnValue = "ok|success";
		try{
			out = response.getWriter();
			out.print(returnValue);
			out.flush();
		}catch (Exception e){
			log.error("getWriter exception :" + e);
		}finally {
			if(out != null) {
				out.close();
			}
		}
    }

}
