package com.test.ingestion.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public final class TestStringUtil {
	
	/**
	 * 
	 * @param inputValue
	 * @param delimiter TODO
	 * @return
	 */
	public static final Map<String,String> tokenize(String inputValue, String delimiter) {
		StringTokenizer tokenizer =
                new StringTokenizer(inputValue, delimiter);
		
		Map<String,String> map = new HashMap<String, String>();
        while (tokenizer.hasMoreElements()) {
            String token1 = tokenizer.nextToken();
            String token2 = tokenizer.nextToken();
            map.put(token1, token2);
        }
				
		return map;
	}
}