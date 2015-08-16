/**
 * 
 */
package com.test.ingestion.dao.common;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import com.test.ingestion.pojo.BatchFinalizationRequest;
import com.test.ingestion.pojo.BatchInitializationResponse;
import com.test.ingestion.pojo.BatchRequestContext;
import com.test.ingestion.resource.exception.DatasourceNotFoundException;

/**
 * Batch Process Life Cycle :
 * (i) Initialize Batch Process
 * (ii) Create/Update/Delete Groups of records for each Entity for a correspoding Batch
 * (iii) Finalize the Batch Process
 */
public interface BulkRequestDAO {

	/**
	 * Find the Last Completed Batch to calculate the new start time. Otherwise
	 * initiate a new batch with the current time as the end of period
	 * 
	 * @param appid
	 * @param appName
	 * @param tenantid
	 * @param appDateFormat
	 * @param appTimeZone
	 * @param tblName
	 * @param string 
	 * @return
	 * @throws Exception
	 */
	public BatchInitializationResponse initializeBatch(int appid, String appName,
			int tenantid, String appDateFormat, String appTimeZone,
			String initialLoadStartDate, String tblName) throws Exception;

	/**
	 * TEST sends the XML (BatchFinalizationRequest) which contains the batch_id and
	 * app_id for updating the status accordingly.
	 * 
	 * @param tenantid
	 * @param batchFinalizationRequest
	 * @param appDateFormat
	 * @param appTimeZone
	 * @throws Exception
	 */
	public void finalizeBatch(BatchFinalizationRequest batchFinalizationRequest,
			int tenantid, String tblName) throws Exception;

	// ////////REQUEST LOG ////////////////

	/**
	 * 
	 * @param requestContext
	 * @param errorCode
	 * @param errorLog
	 * @throws DataAccessException
	 * @throws DatasourceNotFoundException
	 * @throws SQLException 
	 */
	public void logInProcessError(BatchRequestContext requestContext,
			String errorCode, String errorLog) throws DataAccessException,
			DatasourceNotFoundException, SQLException;

	/**
	 * 
	 * @param requestContext
	 * @throws DatasourceNotFoundException
	 * @throws SQLException 
	 * @throws CannotGetJdbcConnectionException 
	 * @throws Exception 
	 */
	public void insertBatch(BatchRequestContext requestContext)
			throws DatasourceNotFoundException, CannotGetJdbcConnectionException, SQLException, Exception;

}
