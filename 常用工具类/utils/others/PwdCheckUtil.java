package com.wondertek.mam.util.others;

/**
 * 功能说明：用户密码合法性验证
 * 校验规则：
 * 1. 长度不小于8位
 * 2. 密码内容需要包括数字、小写字母、大写字母、特殊符号(范围为ASCII中的第33到126号，共94个) 至少两种
 * @author Administrator
 *
 */
public class PwdCheckUtil {

	public static final int PWD_OK = 0x01;
	public static final int PWD_LENGTH_ERROR = 0x02;
	public static final int PWD_NO_LOWERCASE_ERROR=0x03;
	public static final int PWD_NO_UPPERCASE_ERROR = 0x04;
	public static final int PWD_NO_NUMBER_ERROR = 0x05;
	public static final int PWD_NO_SPECIALCHAR_ERROR = 0x06;
	public static final int PWD_NO = 0x07;
	
	private static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	private static Boolean isSpecialCharacter(char c) {
		if (c >= '!' && c <= '/' || c >= ':' && c <= '@' || c >= '[' && c <= '`' || c >= '{' && c <= '~')
			return Boolean.valueOf(true);
		else
			return Boolean.valueOf(false);
	}

	private static Boolean isLowercase(char c) {
		if (c >= 'a' && c <= 'z')
			return Boolean.valueOf(true);
		else
			return Boolean.valueOf(false);
	}

	private static Boolean isUppercase(char c) {
		if (c >= 'A' && c <= 'Z')
			return Boolean.valueOf(true);
		else
			return Boolean.valueOf(false);
	}

	private static Boolean isNumeral(char c) {
		if (c >= '0' && c <= '9')
			return Boolean.valueOf(true);
		else
			return Boolean.valueOf(false);
	}
	
	/**
	 * @param pwd (密码)
	 * @return int
	 * 返回值说明
	 * 
	 */
	public static final int isKeySecurePwd(String pwd){
		boolean haveLowerCase = false;
		boolean haveUpperCase = false;
		boolean haveNumberCase = false;
		boolean haveSpecialCase = false;
		int len = 0;
		if(isEmpty(pwd) || (len = pwd.trim().length())<8)
		  return PWD_LENGTH_ERROR;
		for(int i=len-1; i>=0;i--){
			char c = pwd.charAt(i);
			if(!haveLowerCase && isLowercase(c)){
				haveLowerCase = true;
			}
			if(!haveUpperCase && isUppercase(c)){
				haveUpperCase = true;
			}
			if(!haveNumberCase && isNumeral(c)){
				haveNumberCase = true;
			}
			if(!haveSpecialCase && isSpecialCharacter(c)){
				haveSpecialCase = true;
			}
		}
		if(haveLowerCase && haveNumberCase){
			return PWD_OK;
		}
		if(haveLowerCase && haveUpperCase){
			return PWD_OK;
		}
		if(haveLowerCase && haveSpecialCase){
			return PWD_OK;
		}
		if(haveUpperCase && haveNumberCase){
			return PWD_OK;
		}
		if(haveUpperCase && haveSpecialCase){
			return PWD_OK;
		}
		if(haveNumberCase && haveSpecialCase){
			return PWD_OK;
		}
		if(haveLowerCase && haveNumberCase && haveSpecialCase)
			return PWD_OK;
		if(haveLowerCase && haveNumberCase && haveUpperCase)
			return PWD_OK;
		if(haveLowerCase && haveUpperCase && haveSpecialCase)
			return PWD_OK;
		if(haveSpecialCase && haveUpperCase && haveLowerCase && haveNumberCase)
			return PWD_OK;
		return PWD_NO;
	}
}
