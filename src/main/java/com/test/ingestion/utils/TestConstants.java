package com.test.ingestion.utils;

public interface TestConstants {
	
	// Tables
	public static final String STG_ERROR_LOG_TABLE= "stg_errorlog";
	//public static final String STG_REQ_TABLE_NAME = "stgrequestlog";
	public static final String WAREHOUSE_BATCH_LOG_TABLE_NAME = "batch_log";
	//public static final String WAREHOUSE_ETL_PROCESSING_TABLE_NAME = "data_processing_params";
	// Columns - Request Entity data
	public static final String GENERIC_ENTITY_ID_COLUMN_LABEL = "entity_id";
	public static final String TENANT_ID_COLUMN_LABEL = "tenant_id";
	
	// Columns - Request Metadata	
	public static final String BATCH_ID_COLUMN_LABEL = "batchid";		
	public static final String STG_REQ_ENTITY_ID_COLUMN_LABEL = "entityid";	
	public static final String UNIQ_RECORD_ID_COLUMN_LABEL = "pkey";		
	public static final String ETL_TIMESTAMP_COLUMN_LABEL = "etl_timestamp";	
	
	// Data (XML) Element Labels
	public static final String KEEP_HISTORY_XML_ELEMENT_LABEL = "KeepHistory";	
	public static final String APP_ID_XML_ELEMENT_LABEL= "AppId";
	
	// INPUT PARAMS - TEST BATCH INIT REQUEST PARAMS
	public static final String BATCH_REQ_GENERIC_APP_NAME= "GENERIC_APP_NAME";
	public static final String BATCH_REQ_APP_ID= "APP_ID";
	public static final String BATCH_REQ_BATCH_ID= "BATCH_ID";
	public static final String BATCH_REQ_APP_TIMEZONE= "APP_TIMEZONE";
	public static final String BATCH_REQ_DATE_FORMAT= "DATE_FORMAT";
	public static final String BATCH_REQ_TEST_BATCH_TIME_START= "TEST_BATCH_TIME_START";
	public static final String BATCH_REQ_TEST_BATCH_TIME_END= "TEST_BATCH_TIME_END";
	public static final Object INITIAL_LOAD_START_DATE = "INITIAL_LOAD_START_DATE";
	
	public static final String TENANT_APP_TIMEZONE = "APP_TIMEZONE";
	public static final String QB_TXN_DATE_FORMAT = "QB_TXN_DATE_FORMAT";
	
	public static final String START_DATE = "START_DATE";
	public static final String END_DATE = "END_DATE";
	public static final String TABLE_NAME = "TABLE_NAME";

	public static final String BATCH_STATUS_SUCCESS = "S";
	public static final String BATCH_STATUS_ERROR = "E";
	public static final String BATCH_STATUS_RUNNING = "R";
	
	// ETL - constants
	public static final String ETL_ROLLBACK_FLAG= "rollback";
	
	//  BATCH LOG table columns 
	
	public static final String BATCH_LOG_GENERIC_APP_NAME= "generic_app_name";
	public static final String BATCH_LOG_APP_ID= "app_id";
	public static final String BATCH_LOG_BATCH_ID= "batch_id";
	public static final String BATCH_LOG_TENANT_ID= "tenant_id";
	public static final String BATCH_LOG_STATUS= "status";
	public static final String BATCH_LOG_RUN_DATE_TO= "current_run_date_to";
	public static final String BATCH_LOG_RUN_DATE_FROM= "current_run_date_from";
	
	
	// SQL Statements
	public static final String BATCH_INIT_SQL 
	  = "SELECT * FROM "+testConstants.WAREHOUSE_BATCH_LOG_TABLE_NAME+" WHERE status like 'S' and app_id = ? and tenant_id = ? order by pkey desc limit 1";
	

	public static final String BATCH_FINAL_SQL
	  = "update "+testConstants.WAREHOUSE_BATCH_LOG_TABLE_NAME+" set status=?, " +
				"TEST_batch_start_time = ?, TEST_batch_end_time = ? where pkey =? and app_id =? and tenant_id =?";

	
	public static final String DELETE_RECORDS_FROM_ENTITY_TABLE_SQL = "delete from ? where "+testConstants.GENERIC_ENTITY_ID_COLUMN_LABEL+" =?";	
	
	//public static final String DELETE_RECORDS_FROM_STGREQLOG_TABLE_SQL = "delete from " + testConstants.STG_REQ_TABLE_NAME+ " where "+testConstants.GENERIC_ENTITY_ID_COLUMN_LABEL+" =?";
	
	//public static final String STGREQ_ERROR_LOG_SQL= "update "+testConstants.STG_REQ_TABLE_NAME+" set status=?, errorcode=?, errorlog=? where "+testConstants.UNIQ_RECORD_ID_COLUMN_LABEL+" =?";
	
		
	public static String BATCH_ERROR_LOG_SQL = "update "+testConstants.WAREHOUSE_BATCH_LOG_TABLE_NAME+" set status='E' where pkey =? and app_id =? and tenant_id =?";
	
	//public static String STGREQ_POST_PROCESS_LOG_SQL = "update "+testConstants.STG_REQ_TABLE_NAME+" set entitypkey=?, childtable=?, status=? where "+testConstants.UNIQ_RECORD_ID_COLUMN_LABEL+" =?";
	
	//public static String ETL_ROLLBACK_CHECK_SQL = "SELECT * FROM "+testConstants.WAREHOUSE_ETL_PROCESSING_TABLE_NAME+" WHERE param_name like ? and param_value like ? and genericAppName like ? and tenant_id like ? limit 1";
	
	String ERROR_LOG_SQL = "insert into "+testConstants.STG_ERROR_LOG_TABLE+"(createdtime,tenantid,batchid,appid,tablename,operation,errorcode,errorlog) VALUES (?,?,?,?,?,?,?,?)";
	
	
}
