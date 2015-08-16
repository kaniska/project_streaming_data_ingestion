package com.test.ingestion.utils;

public class DeDuplicationUtil {

	/**
String password = org.springframework.util.DigestUtils.md5DigestAsHex
("password".getBytes()) System.out.println(password) 

	 * 
	 * 
String password = org.apache.commons.codec.digest.
DigestUtils.md5Hex("password"); System.out.println(password); 
	 * 
	 * 
	 */
	
	
	public static String findMd5Hex(String inputData){
		return org.apache.commons.codec.digest.
				DigestUtils.md5Hex
				(inputData.getBytes());
	}
}
