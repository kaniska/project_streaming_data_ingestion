/**
 * 
 */
package com.test.ingestion.pojo;


import com.test.ingestion.utils.TestXMLParser;
import com.Test.Test.security.SecurityHelper;

/**
 * Context Object that stores the batch specific attributes.
 * All the fields should be immutable except 'status'. 
 * @author Kaniska_Mandal
 */
public final class BatchRequestContext {
	
	// stores the 'entire data stream' received from the Biz Apps 
	private String requestXML;
	// stores the type of entity which is same as the parent element name
	private final String entityType;
	// stores the app id for SFDC app_id = , for QBDT app_id = 
	private final int appid;
	// stores the current Batch Number which binds all the requests into a logical group
	private final int batchid;
	// stores the id of the tenant from which the request is received
	private final int tenantid;
	// stores the table name 
	private final String tablename;
	// stores the status of the current batch
	// if any error occurs during processing the request, the status field can be changed to 
	private int status =  org.apache.commons.httpclient.HttpStatus.SC_ACCEPTED;
	
	private TestXMLParser testXMLParser;

	public BatchRequestContext(String requestXML) {
		this.requestXML = requestXML;
		this.testXMLParser = new TestXMLParser(requestXML);
		this.entityType = testXMLParser.getParentRecord();
		this.appid = testXMLParser.getAppId();
		this.batchid = Integer.parseInt(testXMLParser.getBatchId());
		this.tenantid = SecurityHelper.getTenantId();
		this.tablename = testXMLParser.getTableName();
	}

	/**
	 * @return the requestXML
	 */
	public String getRequestXML() {
		return requestXML;
	}


	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the testXMLParser
	 */
	public TestXMLParser gettestXMLParser() {
		return testXMLParser;
	}


	/**
	 * @return the batchid
	 */
	public int getBatchid() {
		return batchid;
	}

	/**
	 * @return the appid
	 */
	public int getAppid() {
		return appid;
	}

	/**
	 * @return the tenantid
	 */
	public int getTenantid() {
		return tenantid;
	}

	/**
	 * @return the tablename
	 */
	public String getTablename() {
		return tablename;
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType() {
		return entityType;
	}
	
	public void cleanup(){
		this.requestXML = null;		
		this.testXMLParser.cleanup();
	}

}
