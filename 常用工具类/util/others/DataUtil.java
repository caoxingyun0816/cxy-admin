package com.wondertek.mam.util.others;

import java.util.regex.Pattern;

public class DataUtil {
	
	public static boolean isInteger(String str) {
		  if(str==null )
			   return false;
			  Pattern pattern = Pattern.compile("[0-9]+");
			  if(pattern.matcher(str).matches()){
				  return true;
			 }else{
				  pattern = Pattern.compile("[0-9]*(\\.?)0*");
				  return pattern.matcher(str).matches();
			  }
		}
		
		public  static boolean isDecimal(String str) {
			  if(str==null )
				   return false;
			  Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
			  return pattern.matcher(str).matches();
				
		}

		
		
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
