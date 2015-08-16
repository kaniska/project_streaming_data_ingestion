/**
 * 
 */
package com.test.ingestion.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.Test.ca.data.DataType;
import com.Test.ca.data.DataTypeBase;
import com.Test.ca.metadata.model.BusinessObjectDefinition;
import com.Test.ca.metadata.model.FieldDefinition;
import com.Test.Test.util.TestLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.sql.ddl.CreateTableClause;
import com.mysema.query.sql.ddl.DropTableClause;

/**
 * @author Kaniska_Mandal
 * 
 */
public class MetadataImporter {

	private static final int APP_DATASOURCE_ID = 5;
	private TestLogger log;
	private DataSource testDataSource;
	private ClassPathXmlApplicationContext applicationContext;
	private Connection conn;

	public MetadataImporter() {
		log = new TestLogger(MetadataImporter.class);
	}

	public static void main(String[] args) {
		MetadataImporter metadataImporter = new MetadataImporter();
		metadataImporter.setUp();
		try {
			metadataImporter.importMetadata();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			metadataImporter.tearDown();
		}
	}

	private void setUp() {
		log.info("===> setup");
		try {
			applicationContext = new ClassPathXmlApplicationContext(
					new String[] { "monetdb-schema-export-config.xml" });
			testDataSource = (DataSource) applicationContext
					.getBean("testDataSource");
			conn = testDataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private String receiveJsonTemplate() {
		return "{ 'version': '1.0',    'appName': 'SFDC_EE',    'businessObjects': [        {            'objectName': 'raw_sfdc_ee_1_stg_account',            'attributes': [                {                    'name': 'account_name',                    'dataType': 'STRING',                    'size': '1000',                    'defaultIfNull': 'Unknown',                    'scdType': 'SCD2'                },                {                    'name': 'last_modified_date',                    'dataType': 'STRING',                    'size': '30',                    'defaultIfNull': 'Unknown',                    'scdType': 'NA'                }]        }           ]}";
	}

	private void importMetadata() throws Exception {
		try {

			String jsonTemplate = receiveJsonTemplate();

			List<BusinessObjectDefinition> bizDefList = createBusinessObjectDef(jsonTemplate);

			createTables(bizDefList);

		} catch (Exception ex) {
			throw ex;
		} finally {
		}
	}

	/**
	 * @param bizDefList
	 * @throws SQLException
	 */
	private void createTables(List<BusinessObjectDefinition> bizDefList)
			throws SQLException {
		SQLTemplates dialect = new MySQLTemplates();
		for (BusinessObjectDefinition bizObject : bizDefList) {
			if (tableExists(bizObject.getDbTableName())) {
				DropTableClause dropTableClause = new DropTableClause(conn,
						dialect, bizObject.getDbTableName());
				dropTableClause.execute();
			}
			CreateTableClause createTableClause = new CreateTableClause(conn,
					dialect, bizObject.getDbTableName());
			// http://www.querydsl.com/static/querydsl/2.1.0/reference/html/ch02s04.html#d0e832
			for (FieldDefinition fieldDef : bizObject.getFields()) {
				createTableClause.column(fieldDef.getDbColumnName(),
						fieldDef.getDataType().getDataTypeClass()).size(1020);
			}
			createTableClause.execute();
		}
	}

	/**
	 * @param bizObject
	 * @throws SQLException
	 */
	private boolean tableExists(String tablename) throws SQLException {
		DatabaseMetaData dbMetadata = conn.getMetaData();
		ResultSet rset = dbMetadata.getTables(null, null, tablename, null);
		if (rset.next())
			return true;
		else
			return false;
	}

	/**
	 * @param jsonTemplate
	 * @return
	 * @throws JsonSyntaxException
	 */
	private List<BusinessObjectDefinition> createBusinessObjectDef(
			String jsonTemplate) throws JsonSyntaxException {
		Gson gson = new GsonBuilder().create();
		JsonObject templateDef = gson.fromJson(jsonTemplate, JsonObject.class);
		JsonElement templateVersion = templateDef.get("version");
		List<BusinessObjectDefinition> bizDefList = new ArrayList<BusinessObjectDefinition>(
				1);

		for (Map.Entry<String, JsonElement> entry : templateDef.entrySet()) {
			JsonElement elem = ((JsonElement) entry.getValue());

			if (entry.getKey().equals("businessObjects")) {
				BusinessObjectDefinition bizDef = new BusinessObjectDefinition();
				JsonArray bizObjArray = elem.getAsJsonArray();
				for (JsonElement bizObjectJson : bizObjArray) {
//					bizDef.setAppDataSourceId(APP_DATASOURCE_ID);
//					String tableName = bizObjectJson.getAsJsonObject()
//							.get("objectName").getAsString();
//					bizDef.setDbTableName(tableName);
					/*
					 * bizDef.setBizObjVersion(bizObjVersion);
					 * bizDef.setDesc(desc); bizDef.setLabel(label);
					 * bizDef.setTableName(tableName);
					 * bizDef.setInternalName(internalName);
					 */
					JsonArray fieldsArray = bizObjectJson.getAsJsonObject()
							.get("attributes").getAsJsonArray();

					List<FieldDefinition> fields = new ArrayList<FieldDefinition>(
							1);
					for (JsonElement fieldDefJson : fieldsArray) {

						FieldDefinition fieldDef = new FieldDefinition("");
						fieldDef.setDataType(resolveDataType(fieldDefJson
								.getAsJsonObject().get("dataType")
								.getAsString()));
						fieldDef.setDbColumnName(fieldDefJson.getAsJsonObject()
								.get("name").getAsString());
//						fieldDef.setDbTableName(tableName);
						
//						fieldDef.setDefaultValueIfNull(fieldDefJson
//								.getAsJsonObject().get("defaultIfNull")
//								.getAsString());
//						fieldDef.setDesc("");
//						fieldDef.setErrorValue("Error while trying to read or update column.");
						fields.add(fieldDef);
					}
					bizDef.setFields(fields);
					//
				}
				//
				bizDefList.add(bizDef);
			}
		}
		return bizDefList;
	}

	/**
	 * 
	 * @param inputDataType
	 * @return
	 */
	private DataType resolveDataType(String inputDataType) {
		DataType outputDataType = DataTypeBase.STRING_TYPE;
		if (inputDataType.equals("DOUBLE")) {
			outputDataType = DataTypeBase.INT_TYPE;
		}
		return outputDataType;
	}

	public void tearDown() {
		log.info("===> tearDown");
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
