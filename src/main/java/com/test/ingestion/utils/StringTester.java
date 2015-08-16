package com.test.ingestion.utils;


public class StringTester {

	public static void main(String[] args) {
	
		String[] acntHrchyLevels = "6600 � Depreciation & Computer Supply:6650 � Other Amortization Costs".split(":");
		
		for(int k =0; k <acntHrchyLevels.length; k++) {
			
			String colValue = acntHrchyLevels[k]; 
			if(acntHrchyLevels[k].contains("�")) {				
				colValue = acntHrchyLevels[k].substring(acntHrchyLevels[k].indexOf('�')+1).trim();
			}		
			 
			String colName = "level"+k;
			System.out.println(colName+":"+colValue);
		}
		
	}
	
}
