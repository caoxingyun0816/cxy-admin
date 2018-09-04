package com.wondertek.mam.util.video;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AssetDotSaxHandler extends DefaultHandler {
	HashMap<String, String> asset_dot = null;
	public ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	String preTag = null;

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String content = new String(ch, start, length).trim();
		if (content.length() > 0) {
			asset_dot.put(preTag, content);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attr) throws SAXException {
		preTag = name;
		if ("AssetId".equals(name) || "AssetDot".equals(name)) {
			asset_dot = new HashMap<String, String>();
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if ("AssetDot".equals(name)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map = asset_dot;
			list.add(map);
			asset_dot = null;
		}
		preTag = null;
	}
}
