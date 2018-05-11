package com.gen.util;


/**
 * @Author: LiYuan
 * @Description:
 * @Date: 11:23 2018/5/11
 */
public class Utils {
	public static String sql2JavaName(String str) {
		StringBuffer sb=new StringBuffer(str.toLowerCase());
		
		//sb.replace(0, 1, str.substring(0,1));
		
		int index=-1;
	
		while((index=sb.indexOf("_"))>-1){
			sb.deleteCharAt(index);
			if(sb.length()>index && index > 1){
				sb.replace(index, index+1,String.valueOf(sb.charAt(index)).toUpperCase());
			}
		}
		return sb.toString();
	}
	
	public static String java2SqlName(String str){
		StringBuffer sqlBuffer = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z'&&i>0)
				sqlBuffer.append('_');
			sqlBuffer.append(str.charAt(i));
		}
		return sqlBuffer.toString().toLowerCase();
	}

	/**
	 * 首字母变大写
	 *
	 * @param str
	 * @return
     */
	public static String upperFirstChar(String str) {
		return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
	}

	/**
	 * 首字母变小写
	 *
	 * @param str
	 * @return
	 */
	public static String lowerFirstChar(String str) {
		return String.valueOf(str.charAt(0)).toLowerCase() + str.substring(1);
	}
}
