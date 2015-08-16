/**
 * 
 */
package com.test.ingestion.dao.monetdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import com.test.ingestion.dao.common.BulkRequestDAO;
import com.test.ingestion.metadata.MetaDataCache;
import com.test.ingestion.pojo.BatchFinalizationRequest;
import com.test.ingestion.pojo.BatchInitializationResponse;
import com.test.ingestion.pojo.BatchRequestContext;
import com.test.ingestion.rules.DataTypeRules;
import com.test.ingestion.rules.SQLRules;
import com.test.ingestion.rules.TimeDimensionRules;
import com.test.ingestion.utils.testDateUtil;
import com.test.ingestion.utils.testXMLParser;
import com.test.ingestion.utils.testConstants;
import com.test.ingestion.core.testApplicationContext;
import com.test.ingestion.resource.exception.DatasourceNotFoundException;
import com.test.ingestion.resource.service.DataSourceService;
import com.test.ingestion.security.SecurityHelper;
import com.test.ingestion.util.TestLogger;

/**
 * @author Kaniska_Mandal
 * 
 *         This is the DAO class that performs the following operations : (i)
 *         initializes (ii) finalizes batch process
 *         inserts/deletes/updates/reads records (iii) inserts/updates the
 *         request metadata
 * 
 *         Note : This class directly handles Jdbc Connection (Rollback, Release
 *         resource). Somehow monetdriver does not go well with
 *         SimpleJdbcCommand. 
 *         Note : There is another version of the this DAO -
 *         BulkRequestDAOImpl which delegates the job of handling jdbc
 *         connections to SimpleJdbcCommand. It is used for storing data into MySQL
 */
@Repository("bulkRequestDAO")
public class MonetdbDAOImpl implements BulkRequestDAO {

	private static final TestLogger log = new TestLogger(MonetdbDAOImpl.class);

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
		// int id = appService.getApplicationIdForGivenName("SFDC EE");
		BatchInitializationResponse testBatchInfoResponse = null;
		// ////
		boolean foundLastSuccessfulBatch = false;
		DataSource datasource = null;
		java.sql.Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			// Get spring jdbc command
			datasource = getDatasource(DataSourceService.ComponentDBType.stagingdb);
			con = (java.sql.Connection) datasource.getConnection();
			// get a statement to execute on
			pstmt = con.prepareStatement(testConstants.BATCH_INIT_SQL);
			pstmt.setInt(1, appid);
			pstmt.setInt(2, tenantid);
			rs = pstmt.executeQuery();

			Map<String, Object> resultMap = new HashMap<String, Object>();
			while (rs.next()) {
				foundLastSuccessfulBatch = true;
				resultMap.put(testConstants.BATCH_LOG_RUN_DATE_TO,
						rs.getObject(testConstants.BATCH_LOG_RUN_DATE_TO));
				break;
			}

			if (foundLastSuccessfulBatch) { // SUBSEQUENT LOADS ...
				testBatchInfoResponse = calculateNextFetchDateForIncrementalLoadByLastSuccessfulRunDate(
						appid, appName, tenantid, appDateFormat, appTimeZone,
						tblName, con, resultMap);
			} else {// Initial Load - calculate next query dates

				testBatchInfoResponse = calculateNextFetchTimeForInitialLoadByLastSuccessfulRunDate(
						appid, appName, tenantid, appDateFormat, appTimeZone,
						initialLoadStartDate, con);
			}

		} catch (Exception e) {
			con.rollback();
			throw e;

		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			DataSourceUtils.releaseConnection(con, datasource);
		}

		Map<String, String> dbPropMap = platformService
				.getDataSourceProperties(tenantid,
						DataSourceService.ComponentDBType.stagingdb);
		log.debug(
				"||| Batch Initialized in Warehouse DB -> %s, running on Host -> %s ",
				dbPropMap.get(DataSourceService.DATABASE_NAME),
				dbPropMap.get(DataSourceService.HOST_NAME));

		return testBatchInfoResponse;
	}

	/**
	 * 
	 * @param componentDBType
	 * @return
	 * @throws DatasourceNotFoundException
	 */
	private DataSource getPlatformDatasource(
			DataSourceService.ComponentDBType componentDBType)
			throws DatasourceNotFoundException {
		if (platformService == null) {
			platformService = (DataSourceService) testApplicationContext
					.getBean("DatasourceService");
		}

		int tenantid = SecurityHelper.getTenantId();
		DataSource datasourceResolvedByPlatform = platformService
				.getTenantDataSource(tenantid, componentDBType); //

		return datasourceResolvedByPlatform;
	}

	/**
	 * 
	 * @param componentDBType
	 * @param tblName
	 * @return
	 * @throws DatasourceNotFoundException
	 */
	@SuppressWarnings("unused")
	private DataSource getDatasource(
			DataSourceService.ComponentDBType componentDBType)
			throws DatasourceNotFoundException {

		if (platformService == null) {
			platformService = (DataSourceService) testApplicationContext
					.getBean("DatasourceService");
		}

		int tenantid = SecurityHelper.getTenantId();
		DataSource datasourceResolvedByPlatform = platformService
				.getTenantDataSource(tenantid, componentDBType); //

		return datasourceResolvedByPlatform;
	}

	// //////// LOG ERROR ////////////////

	public void logInProcessError(BatchRequestContext requestContext,
			String errorCode, String errorLog) throws DataAccessException,
			DatasourceNotFoundException, SQLException {

		log.error("test Error::  \n" + errorLog);

		// update the batch process status to 'E'

		DataSource datasource = getDatasource(DataSourceService.ComponentDBType.stagingdb);
		Connection con = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		try {
			con = datasource.getConnection();
			// get a statement to execute on
			pstmt1 = con.prepareStatement(testConstants.BATCH_ERROR_LOG_SQL);
			pstmt1.setInt(1, requestContext.getBatchid());
			pstmt1.setInt(2, requestContext.getAppid());
			pstmt1.setInt(3, requestContext.getTenantid());

			int rows1 = pstmt1.executeUpdate();
			// /////
			// createdtime,tenantid,batchid,appid,tablename,operation,errorcode,errorlog
			pstmt2 = con.prepareStatement(testConstants.ERROR_LOG_SQL);
			pstmt2.setString(1, new Date().toString());
			pstmt2.setInt(2, requestContext.getTenantid());
			pstmt2.setInt(3, requestContext.getBatchid());
			pstmt2.setInt(4, requestContext.getAppid());
			pstmt2.setString(5, requestContext.getTablename());
			pstmt2.setString(6, "NEW");
			pstmt2.setString(7, errorCode);
			pstmt2.setString(8, errorLog);

			int rows2 = pstmt2.executeUpdate();
		} catch (Exception ex) {
			if (con != null)
				con.rollback();
			ex.printStackTrace();
		} finally {
			if (pstmt1 != null)
				pstmt1.close();
			if (pstmt2 != null)
				pstmt2.close();
			DataSourceUtils.releaseConnection(con, datasource);
		}

	}

	/**
	 * 
	 * @param requestContext
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void insertBatch(BatchRequestContext requestContext)
			throws Exception {
		testXMLParser testXMLParser = requestContext.gettestXMLParser();
		String tableName = testXMLParser.getParentRecord();
		/*
		 * testJdbcCommand jdbcCommand = (testJdbcCommand) (getNewJdbcCommand(
		 * DataSourceService.ComponentDBType.stagingdb, tableName));
		 */

		List<Node> columnInfo = testXMLParser.selectNodes(tableName);
		persistBatch(columnInfo, tableName);
		testXMLParser.cleanup();
	}

	/**
	 * @param jdbcCommand
	 * @param list
	 * @param tableName
	 * @throws Exception
	 */
	private void persistBatch(List<Node> list, String tableName)
			throws Exception {
		Iterator<?> iter = list.iterator();
		String tableCacheKey = SecurityHelper.getTenantId() + "_" + tableName;
		
		// lookup the internalTableName from Business Object definition
		String internalTableName = tableName;

		DataSource datasource = getDatasource(DataSourceService.ComponentDBType.stagingdb);
		Connection con = null;
		PreparedStatement pstmt = null;
		List<String> paramNameList = null;

		try {
			con = (java.sql.Connection) datasource.getConnection();
			MetaDataCache
					.prepareTableColumnCache(internalTableName, con, tableCacheKey);
			List<String> columnNameSet = MetaDataCache.getColumnNameMap().get(
					tableCacheKey);
			// ///////////////////////
			//con.setAutoCommit(false);
			String[] sqlStrings = SQLRules
					.gennerateInsertStatement(tableCacheKey);
			String sql = "insert into " + internalTableName + "(" + sqlStrings[0] + ")"
					+ " VALUES ( " + sqlStrings[1] + " )";
			paramNameList = Arrays.asList(sqlStrings[0].split(","));
			pstmt = con.prepareStatement(sql);
			// //////////////////////
			Set<String> recordAttributeSet = new HashSet<String>(1);

			while (iter.hasNext()) {
				Element elm = (Element) iter.next();
				List<Element> childelm = elm.elements(); // children
				Iterator<?> subiter = childelm.iterator();
				// ////////////////////
				int parameterIndex = 1;
				while (subiter.hasNext()) {
					Element subelm = (Element) subiter.next(); // source of
																// values
					String columnName = subelm.getName(); // column names
					recordAttributeSet.add(columnName);
					if (columnNameSet.contains(columnName)) {
						parameterIndex = columnNameSet.indexOf(columnName) + 1;
						Object dataVal = DataTypeRules.convertDataValue(subelm);
						int dataType = DataTypeRules.convertDataType(subelm);
						pstmt.setObject(parameterIndex,dataVal, dataType );

						if (DataTypeRules.isOfDateType(subelm)) {
							columnName = subelm.getName()
									+ "_time_dimension_id";
							parameterIndex = columnNameSet.indexOf(columnName) + 1;
							pstmt.setInt(parameterIndex, TimeDimensionRules
									.getTimeDimensionValue(subelm.getName(),
											"", con));
						}
					}
				}

				// add the values for the left overs
				for (String actualColumn : columnNameSet) {
					if (!recordAttributeSet.contains(actualColumn)) {
						parameterIndex = columnNameSet.indexOf(actualColumn) + 1;

						if (actualColumn.equals("tenant_id")) {
							pstmt.setInt(
									columnNameSet.indexOf("tenant_id") + 1,
									SecurityHelper.getTenantId());
							continue;
						}
						if (actualColumn.equals("etl_timestamp")) {
							/*
							 * pstmt.setObject(
							 * columnNameSet.indexOf("etl_timestamp") + 1, new
							 * Date(), java.sql.Types.TIMESTAMP);
							 */
							pstmt.setString(
									columnNameSet.indexOf("etl_timestamp") + 1,
									new Date().toString());
							continue;
						}
						if (actualColumn.endsWith("_time_dimension_id")) {
							continue;
						}
						if (DataTypeRules.isOfDateType(actualColumn)) {
							pstmt.setString(parameterIndex, ""); // for date
																	// column
							String timeDimColumn = actualColumn
									+ "_time_dimension_id";
							int timeDimColumnIndex = columnNameSet
									.indexOf(timeDimColumn) + 1;
							// TODO - lookup date id
							pstmt.setInt(timeDimColumnIndex, TimeDimensionRules
									.getTimeDimensionValue(actualColumn, "",
											con));
							continue;
						}
						pstmt.setString(parameterIndex, "Unknown");
					}
				}
				recordAttributeSet.clear();
				pstmt.addBatch();
			}

			int[] rows = pstmt.executeBatch();
			//con.commit();
		} catch (Exception ex) {
			// con.rollback();
			throw ex;
		} finally {
			if (pstmt != null)
				pstmt.close();
			DataSourceUtils.releaseConnection(con, datasource);
		}
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
		String statusValue = "S";
		/*
		 * TODO if(shouldRollback(tenantid, appName)) { statusValue = 'X'; }
		 */
		Map<String, String> dbPropMap = platformService
				.getDataSourceProperties(tenantid,
						DataSourceService.ComponentDBType.stagingdb);

		// ////////// also update the batch info
		// TODO -> add datasource IP in the batch_log

		DataSource datasource = getDatasource(DataSourceService.ComponentDBType.stagingdb);
		Connection con = null;
		PreparedStatement pstmt = null;
		try {

			con = datasource.getConnection();
			// get a statement to execute on
			pstmt = con.prepareStatement(testConstants.BATCH_FINAL_SQL);
			pstmt.setString(1, statusValue);
			pstmt.setString(2,
					batchFinalizationRequest.getTESTbatchtimestart());
			pstmt.setString(3, batchFinalizationRequest.getTESTbatchtimeend());
			pstmt.setInt(4, Integer.parseInt(String
					.valueOf(batchFinalizationRequest.getBatchid())));
			pstmt.setInt(5, Integer.parseInt(String
					.valueOf(batchFinalizationRequest.getAppid())));
			pstmt.setInt(6, Integer.parseInt(String.valueOf(tenantid)));

			int rows = pstmt.executeUpdate();

		} catch (Exception ex) {
			con.rollback();
			throw ex;
		} finally {
			if (pstmt != null)
				pstmt.close();
			DataSourceUtils.releaseConnection(con, datasource);
		}

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

	/**
	 * 
	 * @param appid
	 * @param appName
	 * @param tenantid
	 * @param appDateFormat
	 * @param appTimeZone
	 * @param initialLoadStartDate
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static BatchInitializationResponse calculateNextFetchTimeForInitialLoadByLastSuccessfulRunDate(
			int appid, String appName, int tenantid, String appDateFormat,
			String appTimeZone, String initialLoadStartDate, Connection conn)
			throws Exception {

		BatchInitializationResponse testBatchInfoResponse;
		int batchid;
		int num = -1;
		String current_run_date_from = null;
		String current_run_date_to = null;

		// INITIAL LOAD // The Query did not return any ROW
		// Create the first batch Time Slot

		// EPOCH => "1970-01-01T05:05:05.000Z"
		current_run_date_from = testDateUtil
				.convertDefaultDateToSpecificFormat(initialLoadStartDate,
						appDateFormat, appTimeZone);
		Date currentTime = new Date();
		current_run_date_to = testDateUtil.convertDateToStr(currentTime,
				appDateFormat, appTimeZone);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "insert into batch_log " + "("
				+ testConstants.BATCH_LOG_RUN_DATE_FROM + ","
				+ testConstants.BATCH_LOG_RUN_DATE_TO + ", "
				+ testConstants.BATCH_LOG_APP_ID + ","
				+ testConstants.BATCH_LOG_TENANT_ID + ", "
				+ testConstants.BATCH_LOG_STATUS + ","
				+ testConstants.BATCH_LOG_GENERIC_APP_NAME + ") "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, current_run_date_from);
			pstmt.setString(2, current_run_date_to);
			pstmt.setInt(3, appid);
			pstmt.setInt(4, tenantid);
			pstmt.setString(5, "R");
			pstmt.setString(6, appName);

			pstmt.execute();

			rs = pstmt.getGeneratedKeys();

			// Verify if New Data was created fine
			if (rs.next()) {
				num = rs.getInt(1);
			}
			if (num == -1) {
				throw new Exception(
						"Batch Initialization Info not generated properly!");
			}

			// Else prepare the response object
			testBatchInfoResponse = new BatchInitializationResponse(
					String.valueOf(num), current_run_date_from,
					current_run_date_to, "N", "");

			batchid = num;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		}

		log.debug("Batch Params >> Batch Id - " + batchid
				+ " || Run date From - " + current_run_date_from
				+ " || Run date to - " + current_run_date_to + " || App Id - "
				+ appid + " || Tenant Id - " + tenantid + " || App Name - "
				+ appName);
		/*
		 * System.out.println("Batch Params >> Batch Id - " + batchid +
		 * " || Run date From - " + current_run_date_from + " || Run date to - "
		 * + current_run_date_to + " || App Id - " + appid + " || Tenant Id - "
		 * + tenantid + " || App Name - " + appName);
		 */

		return testBatchInfoResponse;

	}

	/**
	 * 
	 * @param appid
	 * @param appName
	 * @param tenantid
	 * @param appDateFormat
	 * @param appTimeZone
	 * @param tblName
	 * @param conn
	 * @param resultMap
	 * @return
	 * @throws Exception
	 */
	public static BatchInitializationResponse calculateNextFetchDateForIncrementalLoadByLastSuccessfulRunDate(
			int appid, String appName, int tenantid, String appDateFormat,
			String appTimeZone, String tblName, Connection conn,
			Map<String, Object> resultMap) throws Exception {

		BatchInitializationResponse testBatchInfoResponse;
		int batchid;
		int num = -1;
		String current_run_date_from = null;
		String current_run_date_to = null;
		// long buffer = 1000; // 1 second
		// Expires on one minute from the date object date

		// the last successful run date now becomes new current run date
		// from
		current_run_date_from = resultMap.get(
				testConstants.BATCH_LOG_RUN_DATE_TO).toString();

		current_run_date_to = testDateUtil.convertDateToStr(new Date(),
				appDateFormat, appTimeZone);

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String batchLogSql = "insert into batch_log " + "("
				+ testConstants.BATCH_LOG_RUN_DATE_FROM + ","
				+ testConstants.BATCH_LOG_RUN_DATE_TO + ", "
				+ testConstants.BATCH_LOG_APP_ID + ","
				+ testConstants.BATCH_LOG_TENANT_ID + ","
				+ testConstants.BATCH_LOG_STATUS + ","
				+ testConstants.BATCH_LOG_GENERIC_APP_NAME + ") "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		try {
			pstmt = conn.prepareStatement(batchLogSql);
			pstmt.setString(1, current_run_date_from);
			pstmt.setString(2, current_run_date_to);
			pstmt.setInt(3, appid);
			pstmt.setInt(4, tenantid);
			pstmt.setString(5, "R");
			pstmt.setString(6, appName);

			pstmt.execute();
			rs = pstmt.getGeneratedKeys();

			// Verify if New Data was created fine
			if (rs.next()) {
				num = rs.getInt(1);
			}
			if (num == -1) {
				throw new Exception(
						"Batch Initialization Info not generated properly!");
			}

			// Else prepare the response object
			testBatchInfoResponse = new BatchInitializationResponse(
					String.valueOf(num), current_run_date_from,
					current_run_date_to, "N", "");

			batchid = num;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		}

		log.debug("Batch Params >> Batch Id - " + batchid
				+ " || Run date From - " + current_run_date_from
				+ " || Run date to - " + current_run_date_to + " || App Id - "
				+ appid + " || Tenant Id - " + tenantid + " || App Name - "
				+ appName);
		/*
		 * System.out.println("Batch Params >> Batch Id - " + batchid +
		 * " || Run date From - " + current_run_date_from + " || Run date to - "
		 * + current_run_date_to + " || App Id - " + appid + " || Tenant Id - "
		 * + tenantid + " || App Name - " + appName);
		 */

		return testBatchInfoResponse;
	}
}
