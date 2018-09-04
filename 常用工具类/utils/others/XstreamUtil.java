package com.wondertek.mam.util.others;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class XstreamUtil {
	
	static public String object2xml(Object obj){
		XStream xstream = new XStream(new StaxDriver());
		return xstream.toXML(obj);
	}
	
	static public Object xml2Object(String xml){
		XStream xstream = new XStream(new StaxDriver());
		return xstream.fromXML(xml);
	}
	
	
	static public void object2xml(Object obj,String file) throws IOException{
		XStream xstream = new XStream(new StaxDriver());
		xstream.toXML(obj, new FileWriter(file));
	}
	
	static public Object xml2Object(File xml){
		XStream xstream = new XStream(new StaxDriver());
		return xstream.fromXML(xml);
	}

}
