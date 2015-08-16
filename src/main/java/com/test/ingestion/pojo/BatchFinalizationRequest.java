/**
 * 
 */
package com.test.ingestion.pojo;

import java.io.Serializable;

/**
 * @author Kaniska_Mandal
 */
public final class BatchFinalizationRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int batchid;
	private int appid;
	private String status;
	
	private String testbatchtimeend;
	private String testbatchtimestart;
	
	
	
	/**
	 * @return the batchid
	 */
	public int getBatchid() {
		return batchid;
	}
	/**
	 * @param batchid the batchid to set
	 */
	public void setBatchid(int batchid) {
		this.batchid = batchid;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the appid
	 */
	public int getAppid() {
		return appid;
	}
	/**
	 * @param appid the appid to set
	 */
	public void setAppid(int appid) {
		this.appid = appid;
	}

	/**
	 * @return the testbatchtimeend
	 */
	public String gettestbatchtimeend() {
		return testbatchtimeend;
	}
	/**
	 * @param testbatchtimeend the testbatchtimeend to set
	 */
	public void settestbatchtimeend(String testbatchtimeend) {
		this.testbatchtimeend = testbatchtimeend;
	}
	/**
	 * @return the testbatchtimestart
	 */
	public String gettestbatchtimestart() {
		return testbatchtimestart;
	}
	/**
	 * @param testbatchtimestart the testbatchtimestart to set
	 */
	public void settestbatchtimestart(String testbatchtimestart) {
		this.testbatchtimestart = testbatchtimestart;
	}
	

}
