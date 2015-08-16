/**
 * 
 */
package com.test.ingestion.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the 'Batch Initialization Response Payload' sent by test to external clients like TEST.
 * test will allocate a 'Batch Process Id', 'Current Run Date From' and '�urrent Run Date To'
 * so that TEST can fetch data incrementally through consistent batches.
 * 
 * @author Kaniska_Mandal
 */
@XmlRootElement(name="TestBatchInfo")
public class BatchInitializationResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private String batchid;
	private String Testbatchtimefrom;
	private String Testbatchtimeto;
	private String errorCondition;
	private String errorMessage;
	
	
	public BatchInitializationResponse() {
		//
	}

	/**
	 * @param batchid
	 * @param lastrundatefrom
	 * @param lastrundateto
	 * @param status
	 */
	public BatchInitializationResponse(String batchid, String Testbatchtimefrom,
			String Testbatchtimeto, String errorCondition, String errorMessage) {
		this.batchid = batchid;
		this.Testbatchtimefrom = Testbatchtimefrom;
		this.Testbatchtimeto = Testbatchtimeto;
		this.errorCondition = errorCondition;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the batchid
	 */
	public String getBatchid() {
		return batchid;
	}


	/**
	 * @return the Testbatchtimefrom
	 */
	public String getTestbatchtimefrom() {
		return Testbatchtimefrom;
	}

	/**
	 * @return the Testbatchtimeto
	 */
	public String getTestbatchtimeto() {
		return Testbatchtimeto;
	}

	/**
	 * @return the errorCondition
	 */
	public String getErrorCondition() {
		return errorCondition;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

}
