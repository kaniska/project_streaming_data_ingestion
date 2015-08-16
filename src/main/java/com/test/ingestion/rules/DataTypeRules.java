package com.test.ingestion.rules;
import java.util.Iterator;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * @author Kaniska_Mandal
 *
 */
public final class DataTypeRules {
	
	/**
	 * 
	 * @param inputData
	 * @return
	 */
	public static int convertDataType(Element inputData) {
		if(inputData.getName().equals("batchid")) {
			return  java.sql.Types.INTEGER;
		}
		
		if(inputData.attributeCount() > 0) {
	    	Attribute attr = inputData.attribute("type");
	    	String dataType = attr.getValue(); 
	    	if(null != dataType) {
	    		if (dataType.equals("int")) {
	    		   return java.sql.Types.INTEGER;	
	    		}else if (dataType.equals("date")) {
		    	   return java.sql.Types.VARCHAR;	
		    	}else if (dataType.equals("double")) {	    			
	    				return java.sql.Types.DOUBLE;
	    		}  	    		    	
	    	}
	    }
		return java.sql.Types.VARCHAR;
	}

	/**
	 * 
	 * @param inputData
	 * @return
	 */
	public static Object convertDataValue(Element inputData) {
		Object data= inputData.getData();
		if(inputData.getName().equals("batchid")) {
			return  Integer.parseInt((String) data);
		}		
		
		if(inputData.attributeCount() > 0) {
	    	Attribute attr = inputData.attribute("type");
	    	String dataType = attr.getValue(); 
	    	if(null != dataType) {
	    		if (dataType.equals("int")) {	    			
	    			if(null != data && !data.toString().trim().equals(""))
	    				return Integer.parseInt(data.toString());
	    			else
	    				return 0;
	    		}else if (dataType.equals("double") || dataType.equals("decimal")) {
	    			if(null != data && !data.toString().trim().equals(""))
	    				return Double.parseDouble(data.toString());
	    			else
	    				return 0.0;
	    		}       		    	
	    	}
	    }
		if (data == null) data = "Unknown";
		return data;
	}
	
	
	/**
	 * 
	 * @param inputData
	 * @return
	 */
	
	public static boolean isOfDateType(Element inputData) {
		Object data= inputData.getData();
		if(inputData.attributeCount() > 0) {
	    	Attribute attr = inputData.attribute("type");
	    	String dataType = attr.getValue(); 
	    	if(null != dataType) {
	    		if (dataType.equals("date")) {	    			
	    			return true;
	    		}    		    	
	    	}
	    }
		return false;
	}
	
	/**
	 * 
	 * @param dataType
	 * @return
	 */
	public static boolean isOfDateType(String dataType) {
	    	if(null != dataType) {
	    		if (dataType.equals("date") || dataType.endsWith("date")) {	    			
	    			return true;
	    		}    		    	
	    	}
		return false;
	}
}
