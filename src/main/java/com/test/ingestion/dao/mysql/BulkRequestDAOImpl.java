/**
 * 
 */
package com.test.ingestion.dao.mysql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.test.ingestion.dao.common.BulkRequestDAO;
import com.test.ingestion.dao.common.TestJdbcCommand;
import com.test.ingestion.pojo.BatchFinalizationRequest;
import com.test.ingestion.pojo.BatchInitializationResponse;
import com.test.ingestion.pojo.BatchRequestContext;
import com.test.ingestion.utils.BatchScheduler;
import com.test.ingestion.utils.TestXMLParser;
import com.test.ingestion.utils.testConstants;
import com.test.ingestion.core.TestApplicationContext;
import com.test.ingestion.resource.exception.DatasourceNotFoundException;
import com.test.ingestion.resource.service.DataSourceService;
import com.test.ingestion.security.SecurityHelper;
import com.test.ingestion.util.TestLogger;

/**
 * @author Kaniska_Mandal
 * 
 *         This is the DAO class that performs the following operations : (i)
 *         initializes / finalizes batch process (ii)
 *         inserts/deletes/updates/reads records (iii) inserts/updates the
 *         request metadata
 */
@Repository("batchRequestDAO")
public class BulkRequestDAOImpl implements BulkRequestDAO {

	private static final TestLogger log = new TestLogger(
			BulkRequestDAOImpl.class);

	@Autowired
	DataSourceService platformService;

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
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public BatchInitializationResponse initializeBatch(int appid,
			String appName, int tenantid, String appDateFormat,
			String appTimeZone, String initialLoadStartDate, String tblName)
			throws Exception {

		if (tblName == null || appName == null || appDateFormat == null
				|| appTimeZone == null) {
			throw new RuntimeException();
		}

		/*
		 * TODO if(shouldRollback(tenantid, appName)) { return new
		 * BatchInitializationResponse("-1","","", "N",
		 * "ETL Rollback is in progress. Do not run the TEST Process till the Data Issue is resolved !"
		 * ); }
		 */

		// Get spring jdbc command
		TestJdbcCommand jdbcCommand = (TestJdbcCommand) getNewJdbcCommand(
				DataSourceService.ComponentDBType.warehousedb, tblName);

		// int id = appService.getApplicationIdForGivenName("SFDC EE");

		BatchInitializationResponse testBatchInfoResponse = null;

		List<String> parameterList1 = new ArrayList<String>();
		parameterList1.add(String.valueOf(appid));
		parameterList1.add(String.valueOf(tenantid));

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			resultMap = ((TestJdbcCommand) jdbcCommand).getJdbcTemplate()
					.queryForMap(testConstants.BATCH_INIT_SQL,
							parameterList1.toArray());

		} catch (EmptyResultDataAccessException e) {
			// Initial Load - calculate next query dates
			testBatchInfoResponse = BatchScheduler
					.calculateNextFetchTimeForInitialLoadByLastSuccessfulRunDate(
							appid, appName, tenantid, appDateFormat,
							appTimeZone, initialLoadStartDate, jdbcCommand);
		}

		// Incremental Load - calculate next query dates
		if (!resultMap.isEmpty()) {

			testBatchInfoResponse = BatchScheduler
					.calculateNextFetchDateForIncrementalLoadByLastSuccessfulRunDate(
							appid, appName, tenantid, appDateFormat,
							appTimeZone, tblName, jdbcCommand, resultMap);

		}

		Map<String, String> dbPropMap = platformService
				.getDataSourceProperties(tenantid,
						DataSourceService.ComponentDBType.warehousedb);
		log.debug(
				"||| Batch Initialized in Warehouse DB -> %s, running on Host -> %s ",
				dbPropMap.get(DataSourceService.DATABASE_NAME),
				dbPropMap.get(DataSourceService.HOST_NAME));

		return testBatchInfoResponse;
	}

	/**
	 * 
	 * @param componentDBType
	 * @param tblName
	 * @return
	 * @throws DatasourceNotFoundException
	 */
	private SimpleJdbcInsert getNewJdbcCommand(
			DataSourceService.ComponentDBType componentDBType, String tblName)
			throws DatasourceNotFoundException {

		if (platformService == null) {
			platformService = (DataSourceService) TestApplicationContext
					.getBean("DatasourceService");
		}

		int tenantid = SecurityHelper.getTenantId();
		DataSource datasourceResolvedByPlatform = platformService
				.getTenantDataSource(tenantid, componentDBType); //

		// spring jdbc insert
		SimpleJdbcInsert command = (new TestJdbcCommand(
				datasourceResolvedByPlatform).withTableName(tblName)
				.usingGeneratedKeyColumns(testConstants.UNIQ_RECORD_ID_COLUMN_LABEL));

		return command;
	}

	// //////// LOG ERROR ////////////////

	public void logInProcessError(BatchRequestContext requestContext,
			String errorCode, String errorLog) throws DataAccessException,
			DatasourceNotFoundException {

		log.error("test Error::  \n" + errorLog);

		// update the batch process status to 'E'

		// //////////also update the batch info

		List<String> parameterList2 = new ArrayList<String>(1);
		parameterList2.add(String.valueOf(requestContext.getBatchid()));
		parameterList2.add(String.valueOf(requestContext.getAppid()));
		parameterList2.add(String.valueOf(requestContext.getTenantid()));

		((TestJdbcCommand) getNewJdbcCommand(
				DataSourceService.ComponentDBType.warehousedb,
				testConstants.WAREHOUSE_BATCH_LOG_TABLE_NAME)).getJdbcTemplate()
				.update(testConstants.BATCH_ERROR_LOG_SQL,
						parameterList2.toArray());

		// //TODO - truncate the error-log length
		// createdtime=?, tenantid=? ,batchid=?, appid=?,
		// tablename=?, operation=?, errorcode=?, errorlog=?

		List<String> parameterList1 = new ArrayList<String>(1);
		parameterList1.add(null);
		parameterList1.add(String.valueOf(requestContext.getTenantid()));
		parameterList1.add(String.valueOf(requestContext.getBatchid()));
		parameterList1.add(String.valueOf(requestContext.getAppid()));
		parameterList1.add(requestContext.getTablename());
		parameterList1.add("NEW");
		parameterList1.add(errorCode);
		// String errorString =
		// TestMySqlUtil.escapeMySqlSpecialCharacters(errorLog).substring(0,
		// 2999);
		parameterList1.add(errorLog);

		// insert the error details into error_log table
		((TestJdbcCommand) getNewJdbcCommand(
				DataSourceService.ComponentDBType.stagingdb,
				testConstants.STG_ERROR_LOG_TABLE)).getJdbcTemplate().update(
				testConstants.ERROR_LOG_SQL, parameterList1.toArray());

	}

	/**
	 * Non-Blocking, Stateless, Fast RollBack Policy
	 * 
	 * @param tenantId
	 * @param genericAppName
	 * @return
	 * @throws DatasourceNotFoundException
	 */
	@SuppressWarnings("unused")
	/*private boolean shouldRollback(int tenantId, String genericAppName)
			throws DatasourceNotFoundException {
		// Get spring jdbc command
		TestJdbcCommand jdbcCommand = (TestJdbcCommand) getNewJdbcCommand(
				DataSourceService.ComponentDBType.warehousedb,
				testConstants.WAREHOUSE_ETL_PROCESSING_TABLE_NAME);

		// Query data_processing_params table to find the rollback flag

		List<String> parameters = new ArrayList<String>();
		parameters.add(testConstants.ETL_ROLLBACK_FLAG);
		parameters.add(genericAppName);
		parameters.add(String.valueOf(tenantId));

		try {
			Object obj = ((TestJdbcCommand) jdbcCommand).getJdbcTemplate()
					.queryForMap(testConstants.ETL_ROLLBACK_CHECK_SQL);

			return true;

		} catch (EmptyResultDataAccessException e) {
			// The Query did not return any ROW
			return false;
		}
	}*/

	/**
	 * 
	 * @param requestContext
	 * @throws DatasourceNotFoundException
	 */
	public void insertBatch(BatchRequestContext requestContext)
			throws DatasourceNotFoundException {
		TestXMLParser testXMLParser = requestContext.gettestXMLParser();
		String tableName = testXMLParser.getParentRecord();
		TestJdbcCommand jdbcCommand = (TestJdbcCommand) (getNewJdbcCommand(
				DataSourceService.ComponentDBType.stagingdb, tableName));

		List<Node> list = testXMLParser.selectNodes(tableName);
		persistBatch(jdbcCommand, list);
		testXMLParser.cleanup();
	}

	/**
	 * @param jdbcCommand
	 * @param list
	 */
	private void persistBatch(TestJdbcCommand jdbcCommand, List<Node> list) {
		Iterator<?> iter = list.iterator();

		List<SqlParameterSource> paramsBatch = new ArrayList<SqlParameterSource>();

		while (iter.hasNext()) {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			Element elm = (Element) iter.next();

			List<Element> childelm = elm.elements(); // children
			Iterator<?> subiter = childelm.iterator();

			while (subiter.hasNext()) {
				Element subelm = (Element) subiter.next();
				parameters.addValue(subelm.getName(), subelm.getData());
			}
			parameters.addValue(testConstants.TENANT_ID_COLUMN_LABEL,
					SecurityHelper.getTenantId());

			paramsBatch.add(parameters);
		}

		SqlParameterSource[] batch = paramsBatch
				.toArray(new MapSqlParameterSource[0]);

		// TODO to see if setAutoCommit to false - improves performance
		// DataSourceUtils.getConnection(datasourceResolvedByPlatform).setAutoCommit(false);
		int[] num = jdbcCommand.executeBatch(batch);
		// DataSourceUtils.getConnection(datasourceResolvedByPlatform).setAutoCommit(true);

	}

	/**
	 * TEST sends the XML (BatchFinalizationRequest) which contains the
	 * batch_id and app_id for updating the status accordingly.
	 * 
	 * @param tenantid
	 * @param batchFinalizationRequest
	 * @param appDateFormat
	 * @param appTimeZone
	 * @throws Exception
	 */
	public void finalizeBatch(
			BatchFinalizationRequest batchFinalizationRequest, int tenantid,
			String tblName) throws Exception {
		char statusValue = 'S';
		/*
		 * TODO if(shouldRollback(tenantid, appName)) { statusValue = 'X'; }
		 */
		Map<String, String> dbPropMap = platformService
				.getDataSourceProperties(tenantid,
						DataSourceService.ComponentDBType.warehousedb);

		// ////////// also update the batch info

		List<String> parameterList2 = new ArrayList<String>(1);
		parameterList2.add(String.valueOf(statusValue));
		parameterList2.add(batchFinalizationRequest.getTESTbatchtimestart());
		parameterList2.add(batchFinalizationRequest.getTESTbatchtimeend());
		parameterList2
				.add(String.valueOf(batchFinalizationRequest.getBatchid()));
		parameterList2.add(String.valueOf(batchFinalizationRequest.getAppid()));
		parameterList2.add(String.valueOf(tenantid));

		// TODO -> add datasource IP in the batch_log

		// Get spring jdbc command
		TestJdbcCommand jdbcCommand = (TestJdbcCommand) getNewJdbcCommand(
				DataSourceService.ComponentDBType.warehousedb,
				testConstants.WAREHOUSE_BATCH_LOG_TABLE_NAME);
		((TestJdbcCommand) jdbcCommand).getJdbcTemplate().update(
				testConstants.BATCH_FINAL_SQL, parameterList2.toArray());

		log.debug(
				"||| Batch Finalization Info :: "
						+ "App Id= %s, Tenant Id= %s, Table Name=%s, Batch Id=%s, TEST Batch Start Date=%s, TEST Batch End Date=%s",
				String.valueOf(batchFinalizationRequest.getAppid()),
				String.valueOf(tenantid), tblName,
				String.valueOf(batchFinalizationRequest.getBatchid()),
				batchFinalizationRequest.getTESTbatchtimestart(),
				batchFinalizationRequest.getTESTbatchtimeend());

		log.debug(
				"||| Batch Finalized in Warehouse DB -> %s, running on Host -> %s ",
				dbPropMap.get(DataSourceService.DATABASE_NAME),
				dbPropMap.get(DataSourceService.HOST_NAME));
	}

}
