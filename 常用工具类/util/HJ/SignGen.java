package com.wondertek.mam.util.HJ;

public class SignGen {
	public static void main(String[] args) {
		//消息接口sign生成举例
		//如果：ActivityCode=0,ReqSys=1001,ReqDateTime=20140101010101001,双方系统约定的密钥=m333333333333333333333333333333m，则生成的sign为
		String messageSign = MD5.sign("0" + "1001" + "20140101010101001" , "m3333333333333333333333333333333m","GBK");
		System.out.println("messageSign==" + messageSign);
	}
}

