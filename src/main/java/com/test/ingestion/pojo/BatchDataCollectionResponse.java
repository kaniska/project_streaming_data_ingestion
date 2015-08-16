/**
 * 
 */
package com.test.ingestion.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Http Response Object - embodies necessary data that external clients like TEST can use for error reporting and debugging.
 * @author Kaniska_Mandal
 */
@XmlRootElement(name="ResponseDataForUpdateRequest")
public class BatchDataCollectionResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	// source entity that was updated (for sequential request)
	//TODO - remove this variable
	private String sourceEntityId;
	// status code -> captures the status of the operation 'collect incoming data'
	private int statusCode;
	// actual error occurred at the backend
	private String errorLog;
	//TODO - for batch request, replace this variable with groupRequestId generated in the backend
	private int persistedRecordId;
	
	public BatchDataCollectionResponse() {
		//
	}
	
	/**
	 * @param sourceEntityId
	 * @param statusCode
	 * @param errorLog
	 * @param persistedRecordId TODO
	 */
	public BatchDataCollectionResponse(String sourceEntityId, int statusCode, String errorLog, int persistedRecordId) {
		this.sourceEntityId = sourceEntityId;
		this.persistedRecordId = persistedRecordId;
		this.statusCode = statusCode;
		this.errorLog = errorLog;
	}
	/**
	 * @return the sourceEntityId
	 */
	public String getSourceEntityId() {
		return sourceEntityId;
	}
	/**
	 * @param sourceEntityId the sourceEntityId to set
	 */
	public void setSourceEntityId(String sourceEntityId) {
		this.sourceEntityId = sourceEntityId;
	}
	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * @return the errorLog
	 */
	public String getErrorLog() {
		return errorLog;
	}
	/**
	 * @param errorLog the errorLog to set
	 */
	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	/**
	 * @return the persistedRecordId
	 */
	public int getPersistedRecordId() {
		return persistedRecordId;
	}

	/**
	 * @param persistedRecordId the persistedRecordId to set
	 */
	public void setPersistedRecordId(int persistedRecordId) {
		this.persistedRecordId = persistedRecordId;
	}
	
	
	
}
