package com.wondertek.mam.util.others;

import java.io.UnsupportedEncodingException;

public class ByteUtil {
	
	//1字节整数
	public static short byte2sShort(byte b[]) {
		if (b.length < 1)
			return 0;
		
		return (short) (b[0] & 0xff);
	}
	
	public static short byte2sShort(byte b[], int offset) {
		if (offset > b.length - 1)
			return 0;
		else 
			return (short) (b[offset] & 0xff);
	}

	
	//2字节整数
	public static short byte2short(byte b[]) {
		if (b.length < 2)
			return 0;
		
		return (short) ((b[1] & 0xff) | (b[0] & 0xff) << 8);
	}
	
	public static short byte2short(byte b[], int offset) {
		if (offset > b.length - 2)
			return 0;
		else if (offset == (b.length - 1))
			return (short) (b[offset] & 0xff);
		else
			return (short) ((b[offset + 1] & 0xff) | (b[offset + 0] & 0xff) << 8);
	}
	
	
	//4字节整数
	public static int byte2int(byte b[]) {
		if (b.length < 4)
			return 0;
		
		return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16
				| (b[0] & 0xff) << 24;
	}	
	
	public static int byte2int(byte b[], int offset) {
		if (offset > b.length - 4) return 0;
		return b[offset + 3] & 0xff | (b[offset + 2] & 0xff) << 8
				| (b[offset + 1] & 0xff) << 16 | (b[offset] & 0xff) << 24;
	}

	//wchar,只有2个字节长度
	public static String byte2wchar(byte[] b) {
		if (b == null || b.length < 2)
			return "";
		try {
			return new String(b, 0, 2, "iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(b, 0, 2);
		}
	}
	
	public static String byte2wchar(byte[] b, int offset) {
		if (b == null || b.length < offset + 2)
			return "";
		try {
			return new String(b, offset, 2, "iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(b, offset, 2);
		}
	}
	
	
	public static float byte2float(byte b[], int offset) {
		if (offset > b.length - 8) return 0;
		int h = byte2int(b,offset); 
		int l = byte2int(b,offset+4);
		float f = Float.parseFloat(h+"."+l);
		return f;
	}
	
	
	//String,长度放在第一个字节内
	public static String byte2string(byte[] b) {
		if (b == null || b.length == 0 || (b[0] == 0))
			return "";
		byte len = b[0];
		if (len <= 0)
			return "";
		try {
			return new String(b, 1, (int) len, "iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(b, 1, (int) len);
		}
	}
	
	public static String byte2string(byte[] b, int offset) {
		if (b == null || b.length == 0 || b.length == offset
				|| (b[offset] == 0))
			return "";

		byte len = b[offset];
		if (len <= 0)
			return "";
		try {
			return new String(b, offset + 1, (int) len, "iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(b, offset + 1, (int) len);
		}
	}
	

	
	//LString,长度放在前两个字节内
	public static String byte2lstring(byte[] b) {
		if (b == null || b.length < 2)
			return "";
		short len = byte2short(b);
		if (len <= 0)
			return "";
		try {
			return new String(b, 2, (int) len, "iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(b, 2, (int) len);
		}
	}

	public static String byte2lstring(byte[] b, int offset) {
		if (b == null || b.length < offset + 2)
			return "";
		short len = byte2short(b, offset);
		if (len <= 0)
			return "";
		try {
			return new String(b, offset + 2, (int) len, "iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new String(b, offset + 2, (int) len);
		}
	}
	
	
	
	
	//=================================================
	
	public static byte[] sShort2byte(int n) {
		byte b[] = new byte[1];
		b[0] = (byte) n;
		return b;
	}
	
	
	public static byte[] short2byte(int n) {
		byte b[] = new byte[2];
		b[0] = (byte) (n >> 8);
		b[1] = (byte) n;
		return b;
	}

	public static void short2byte(int n, byte buf[], int offset) {
		if(offset>buf.length-1)return;
		else if(offset==buf.length-1)buf[offset]=(byte)n;
		else{
			buf[offset] = (byte) (n >> 8);
			buf[offset + 1] = (byte) n;
		}
	}
	
	
	public static byte[] int2byte(int n) {
		byte b[] = new byte[4];
		b[0] = (byte) (n >> 24);
		b[1] = (byte) (n >> 16);
		b[2] = (byte) (n >> 8);
		b[3] = (byte) n;
		return b;
	}

	public static void int2byte(int n, byte buf[], int offset) {
		buf[offset] = (byte) (n >> 24);
		buf[offset + 1] = (byte) (n >> 16);
		buf[offset + 2] = (byte) (n >> 8);
		buf[offset + 3] = (byte) n;
	}
	
	public static byte[] float2byte(float f) {
		byte b[] = new byte[8];
		String[] sf = (f+"").split("\\.");
		int2byte(Integer.parseInt(sf[0]),b,0);
		int2byte(Integer.parseInt(sf[1]),b,4);
		return b;
	}

		
	public static byte[] wchar2byte(char value) {
		byte[] bytes = new byte[2];
		try {
			bytes = new String(new char[] { value })
					.getBytes("iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			bytes = new byte[] { 0, 0 };
		}
		return bytes;
	}	
	
	
	public static byte[] string2byte(String value){
		if(value==null || value.trim().length()==0)return new byte[]{0};
		byte[] data=null; 
		try {
		    data=value.getBytes("iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			data=value.getBytes();
		}
		byte len=(byte)data.length;
		byte[] bytes=new byte[len+1];
		bytes[0]=len;
		System.arraycopy(data, 0, bytes, 1, len);
		return bytes;
	}	
	
	public static int string2byte(String value,byte[] bytes,int offset){
		if(value==null || value.trim().length()==0){
			bytes[offset]=0;
			return 1;
		}
		byte[] data=null; 
		try {
		    data=value.getBytes("iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			data=value.getBytes();
		}
		byte len=(byte)data.length;
		
		bytes[offset]=len;
		System.arraycopy(data, 0, bytes, offset+1, len);
		return len+1;
	}	
	
	public static byte[] lstring2byte(String value){
		if(value==null || value.trim().length()==0){
			return new byte[]{0,0};
		}
		byte[] data=null; 
		try {
		    data=value.getBytes("iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			data=value.getBytes();
		}
		short len=(short)data.length;
		byte[] lenBytes=short2byte(len);
		byte[] bytes=new byte[len+2];
		System.arraycopy(lenBytes, 0, bytes, 0, 2);
		System.arraycopy(data, 0, bytes, 2, len);
		return bytes;
	}
	
	
	public static int  lstring2byte(String value,byte[] bytes,int offset){
		if(value==null || value.trim().length()==0){
			bytes[offset]=0;
			bytes[offset+1]=0;
			return 2;
		}
		byte[] data=null; 
		try {
		    data=value.getBytes("iso-10646-ucs-2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			data=value.getBytes();
		}
		short len=(short)data.length;
		byte[] lenBytes=short2byte(len);
		
		System.arraycopy(lenBytes, 0, bytes,offset, 2);
		System.arraycopy(data, 0, bytes, offset+2, len);
		return len+2;
	}
	public static byte putBit(byte b,int pos,boolean bit){
		byte a=0;
		if(bit)a = 1;
		a = (byte) (a<<pos-1);
		return (byte) (b|a);
		
	}
	public static byte getBit(byte b,int pos){
		
		return (byte) ((b >>(pos-1)) & 00000001) ;
	}
	//=====================
	public static byte[] markSheader(int msgType, int msgSize, int bodySize){
		byte[] sHeader = new byte[10];//SZ_SHEADER
		ByteUtil.short2byte(msgType, sHeader, 0);
		ByteUtil.int2byte(msgSize, sHeader, 2);//SZ_SHEADER
		ByteUtil.int2byte(bodySize, sHeader, 6);
		
		return sHeader;
	}
	
	public static String printBytes(byte[] bytes){
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for(byte bt:bytes){
			sb.append(bt+", ");
		}
		sb.append("}");
		return sb.toString();
		
	}

}
