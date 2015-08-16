package com.test.ingestion.bkup;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.Test.Test.util.TestLogger;

public class Db2JsonExporterOld {

	private TestLogger log;
	private boolean ALLOW_DUPLICATE;
	private boolean ONLY_UNIQUE;
	private DataSource testDataSource;
	private ClassPathXmlApplicationContext applicationContext;
	private Connection con;
	private String path2;
	private String path;

	/**
	 * 
	 */
	public Db2JsonExporterOld() {
		log = new TestLogger(Db2JsonExporterOld.class);
		ALLOW_DUPLICATE = Boolean.TRUE.booleanValue();
		ONLY_UNIQUE = Boolean.FALSE.booleanValue();
		path = "\\profiles";
	}

	public static void main(String[] args) {
		Db2JsonExporterOld exporter = new Db2JsonExporterOld();
		exporter.setUp();
		try {
			exporter.exportMetadata();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
		exporter.tearDown();
		}
	}

	/**
	 * 
	 */
	public void setUp() {
		log.info("===> setup");
		try {
			applicationContext = new ClassPathXmlApplicationContext(
					new String[] { "monetdb-schema-export-config.xml" });
			testDataSource = (DataSource) applicationContext
					.getBean("testDataSource");
			con = testDataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param tableName
	 * @param shouldTrackChanges
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void exportMetadata() throws Exception {
		
		String datasourceName = "SFDC_EE_1" ; //includes tenant id
		
		String full_resource_path = (new StringBuilder(String.valueOf(path)))
				.append("\\").append(datasourceName).append(".json").toString();
		Resource resource = applicationContext.getResource(full_resource_path);
		File sourceFile = null;
		
		///
		if (resource.exists()) {
			sourceFile = resource.getFile();
			FileUtils.forceDelete(sourceFile);
		}
		sourceFile = new File(full_resource_path);
		FileUtils.touch(sourceFile);
		
		String[] tableNames = new String[] { "stg_account", "stg_activity",
				"stg_employee", "stg_lead", "stg_opportunity", "stg_product", "stg_opportunity_stage" ,
				"stg_territory", "stg_user_territory",  "stg_lead_status",
				"stg_opportunity_lineitem", "stg_pricebook", "stg_pricebook_entry", "stg_task"};
		
		try {
			DatabaseMetaData metadata = con.getMetaData();
			
			Map<String, Object> datasetDefMap = new LinkedHashMap<String, Object>();
			
			datasetDefMap.put("version", "1.0");
			datasetDefMap.put("appName", datasourceName);
			
			JSONArray tablesJsonArray = new JSONArray();
			
			for (int i = 0; i < tableNames.length; i++) {
				String tableName = tableNames[i];
				log.info("Gather the Columns Info for the  Table :: "+tableName);
			
				String tenantDatasourceSpecificTableName = tableName +"_"+datasourceName;
				
				//JSONObject tableJonObj = new JSONObject();
				Map<String, Object> tablesMap = new LinkedHashMap<String, Object>();
				
				tablesMap.put("objectName", tenantDatasourceSpecificTableName);
								
				JSONArray columnsJsonArray = new JSONArray();				
				//				
				for (ResultSet resultSet = metadata.getColumns(null, null,
						tableName, "%"); resultSet.next();) {
					String columnName = resultSet.getString("COLUMN_NAME");			
					if (!columnName.equals("pkey")
							&& !columnName.equals("tenant_id")
							&& !columnName.equals("etl_timestamp")) {

						//JSONObject columnJsonObj = new JSONObject();
						Map<String, Object> columnsMap = new LinkedHashMap<String, Object>();
						columnsMap.put("name", columnName);						
						columnsMap.put("dataType", "");
						JSONObject columnJsonObj = (JSONObject)columnsMap;
						columnsJsonArray.add(columnJsonObj);						
					}
				}
				tablesMap.put("businessAttributes", columnsJsonArray);
				JSONObject tableJonObj = (JSONObject)tablesMap;
				tablesJsonArray.add(tableJonObj);
			}
			datasetDefMap.put("businessObjects", tablesJsonArray);
			JSONObject datasetTemplateJsonObj = (JSONObject) datasetDefMap;
			FileUtils.writeStringToFile(sourceFile, datasetTemplateJsonObj.toString(4));
			
		} catch (Exception e1) {
			throw e1;
		}
	}


	public void tearDown() {
		log.info("===> tearDown");
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
