package com.wondertek.mam.util.others;

import java.util.List;
import java.util.Random;

public class IntegerUtil {

	/**
	 * 16进制转化
	 * @param value
	 * @return
	 */
	public static String toHexValue(String value){
		StringBuffer sb = new StringBuffer();
		byte[] bytes = value.getBytes();
		
		for(byte b : bytes){
			String hex = Integer.toHexString(b & 0xFF);
			
			if (hex.length() < 2) {
		        hex = '0' + hex;
		    }
			sb.append(hex);
		}
		
		return sb.toString();
	}	
	
	/**
	 * 二进制转化
	 * @param value
	 * @return
	 */
	public static String toBinaryValue(String value){
		StringBuffer sb = new StringBuffer();
		byte[] bytes = value.getBytes();
		
		for(byte b : bytes){
			String binary = Integer.toBinaryString(b & 0xFF);
			
			while (binary.length() < 8) {
				binary = '0' + binary;
			}
			sb.append(binary);
		}
		
		return sb.toString();
	}

	/**
	 * 返回指定长度的字符串
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length){
		StringBuilder buffer = new StringBuilder("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		int range = buffer.length();
		for (int i = 0; i < length; i ++){
			sb.append(buffer.charAt(r.nextInt(range)));
		}
		return sb.toString();
	}

	/**
	 * 随机生成从0到num-1之间的一个随机数
	 * @param num
	 * @return
	 */
	public static int randomNum(int num){
		int randomNum = (int)(Math.random() * num);
		return randomNum;
	}

	/**
	 * 得到下拉选项html
	 * @param list 选项列表
	 * @param selectedcode 默认选中的代码
	 * @param name 下拉选项名
	 * @return
	 */
	public static String getSelectHtml(List<String> list, String selectedcode, String name){
		StringBuffer sb = new StringBuffer();
		sb.append("<select name='"+name+"'>");
		String temp = "";
		String code = "";
		String desc = "";

		for(String str : list){
			String isSelected ="";

			code = str.substring(0,str.indexOf("&"));
			desc = str.substring(str.indexOf("&")+1);
			if(selectedcode.equals(code))
				isSelected= "selected='selected'";

			temp = "<option value='"+code+"' "+isSelected+">"+code+"-"+desc+"</option>";
			sb.append(temp);
		}

		sb.append("</select>");
		return sb.toString();
	}

}
