package com.test.ingestion.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.Test.Test.util.TestLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

public class MetadataExporter {

	private TestLogger log;
	private DataSource testDataSource;
	private ClassPathXmlApplicationContext applicationContext;
	private Connection con;
	private String path;

	/**
	 * 
	 */
	public MetadataExporter() {
		log = new TestLogger(MetadataExporter.class);
		path = "\\profiles";
	}

	public static void main(String[] args) {
		MetadataExporter exporter = new MetadataExporter();
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
		
		String datasourceName = "sfdc_ee_1" ; //includes tenant id
		
		String full_resource_path = (new StringBuilder(String.valueOf(path)))
				.append("\\").append(datasourceName).append(".json").toString();
		Resource resource = applicationContext.getResource(full_resource_path);
		File sourceFile = null;
		
		///
		if (resource.exists()) {
			sourceFile = resource.getFile();
			FileUtils.forceDelete(sourceFile);
		}
		//sourceFile = new File(full_resource_path);
		//FileUtils.touch(sourceFile);
		
/*		String[] tableNames = new String[] { "stg_account", "stg_activity",
				"stg_employee", "stg_lead", "stg_opportunity", "stg_product", "stg_opportunity_stage" ,
				"stg_territory", "stg_user_territory",  "stg_lead_status",
				"stg_opportunity_lineitem", "stg_pricebook", "stg_pricebook_entry", "stg_task"};*/
		
		String[] tableNames = new String[] { "stg_account", "stg_employee", "stg_lead", "stg_opportunity", "stg_product", "stg_opportunity_stage" ,
				  "stg_lead_status", "stg_opportunity_lineitem", "stg_pricebook", "stg_pricebook_entry"};
		
		JsonWriter  writer = new JsonWriter(new FileWriter(full_resource_path));
		
			DatabaseMetaData metadata = con.getMetaData();
			
			JsonObject  datasetTemplateJsonObj = new JsonObject();
			datasetTemplateJsonObj.addProperty("version", "1.0");
			datasetTemplateJsonObj.addProperty("appName", datasourceName);
			
			JsonArray tablesJsonArray = new JsonArray();
			
			/**
			 * 
			 */
			for (int i = 0; i < tableNames.length; i++) {
				String tableName = tableNames[i];
				log.info("Gather the Columns Info for the  Table :: "+tableName);
				String tenantDatasourceSpecificTableName = "";
				/*String origTableName = tableName;
				if(tableName.contains("stg_")) {
					tableName= tableName.replace("stg_", "").trim();
				}
			*/
				tenantDatasourceSpecificTableName = "raw_"+datasourceName+"_"+tableName;
				
				JsonObject tableJonObj = new JsonObject();
				//Map<String, Object> tablesMap = new LinkedHashMap<String, Object>();
				
				//tableJonObj.addProperty("internalName", tenantDatasourceSpecificTableName);
				tableJonObj.addProperty("objectName", tableName);
				tableJonObj.addProperty("description", "Salesforce "+tableName);
				tableJonObj.addProperty("version", "1");
								
				JsonArray columnsJsonArray = new JsonArray();				
				//				
				for (ResultSet resultSet = metadata.getColumns(null, null,
						tableName, "%"); resultSet.next();) {
					String columnName = resultSet.getString("COLUMN_NAME");			
					String columnSize = resultSet.getString("COLUMN_SIZE");
					int columnDataType = Integer.parseInt(resultSet.getString("DATA_TYPE"));
					String columnNullable = resultSet.getString("NULLABLE");
					
					
					if (!columnName.equals("pkey")
							&& !columnName.equals("tenant_id")
							&& !columnName.equals("batchid")
							&& !columnName.equals("entity_id")
							&& !columnName.equals("etl_timestamp")
							&& !columnName.equals("operation")
							&& !columnName.endsWith("time_dimension_id")) {

						JsonObject columnJsonObj = new JsonObject();
						columnJsonObj.addProperty("name", columnName);						
						columnJsonObj.addProperty("dataType", columnDataTypeLabel(columnDataType));
						columnJsonObj.addProperty("size", columnSize);
						columnJsonObj.addProperty("defaultIfNull", columnDefaultValue(columnDataType));
						//columnJsonObj.addProperty("nullable", columnNullable);
						columnJsonObj.addProperty("scdType", getValueChangeStrategy(columnDataType, columnName ));
						//columnJsonObj.addProperty("businessKey", isBusinessKey(columnName));
						//columnJsonObj.addProperty("filterable", "false");
						//columnJsonObj.addProperty("measure", isMeasure(columnDataType));
						
						columnsJsonArray.add(columnJsonObj);						
					}
				}
				tableJonObj.add("attributes", columnsJsonArray);
				tablesJsonArray.add(tableJonObj);
			}
			datasetTemplateJsonObj.add("businessObjects", tablesJsonArray);
			
		
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonOutput = gson.toJson(datasetTemplateJsonObj);
			
			JsonReader reader = new JsonReader(new InputStreamReader( new ByteArrayInputStream(jsonOutput.getBytes())));
	            writer.setIndent("    ");
	        reader.setLenient(true);
	        prettyprint(reader, writer);
	        writer.close();
	        reader.close();
			
			//FileUtils.writeStringToFile(sourceFile, datasetTemplateJsonObj.toString());
			
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
	
	static String isBusinessKey(String columnname) {
		if(columnname.equals("entity_id")) {
			return "true";
		}else
			return "false";
	}
	
	/**
	 * 
	 * @param columnType
	 * @return
	 */
	static String columnDefaultValue(int columnType) {
		 switch (columnType) {
         case java.sql.Types.INTEGER:
             return "0";
         case java.sql.Types.DOUBLE:
             return "0.0";
         case java.sql.Types.VARCHAR:
             return "Unknown";
         case java.sql.Types.DATE:
             return "";
         case java.sql.Types.BOOLEAN:
             return "false";
         default :
             return "";
         }
	}
	
	static String isMeasure(int columnType) {
		 switch (columnType) {
        case java.sql.Types.INTEGER:
            return "true";
        case java.sql.Types.DOUBLE:
            return "true";
        default :
            return "false";
        }
	}
	
	/**
	 * 
	 * @param columnType
	 * @param columnName
	 * @return
	 */
	static String getValueChangeStrategy(int columnType, String columnName) {
		
		if(columnName.contains("created_date") || columnName.contains("last_modified_date")) {
			return "NA";
		}
		
		 switch (columnType) {
	        case java.sql.Types.VARCHAR:
	            return "SCD2";
	        default :
	            return "SCD1";
	        }
		}
	
	/**
	 * 
	 * @param columnType
	 * @return
	 */
	static String columnDataTypeLabel(int columnType) {
		 switch (columnType) {
        case java.sql.Types.INTEGER:
            return "INTEGER";
        case java.sql.Types.DOUBLE:
            return "DOUBLE";
        case java.sql.Types.VARCHAR:
            return "STRING";
        case java.sql.Types.DATE:
            return "DATE";
        case java.sql.Types.BOOLEAN:
            return "BOOLEAN";
        default :
            return "Unknown";
        }
	}
	
	
	  static void prettyprint(JsonReader reader, JsonWriter writer) throws IOException {
	        while (true) {
	            JsonToken token = reader.peek();
	            switch (token) {
	            case BEGIN_ARRAY:
	                reader.beginArray();
	                writer.beginArray();
	                break;
	            case END_ARRAY:
	                reader.endArray();
	                writer.endArray();
	                break;
	            case BEGIN_OBJECT:
	                reader.beginObject();
	                writer.beginObject();
	                break;
	            case END_OBJECT:
	                reader.endObject();
	                writer.endObject();
	                break;
	            case NAME:
	                String name = reader.nextName();
	                writer.name(name);
	                break;
	            case STRING:
	                String s = reader.nextString();
	                writer.value(s);
	                break;
	            case NUMBER:
	                String n = reader.nextString();
	                writer.value(new BigDecimal(n));
	                break;
	            case BOOLEAN:
	                boolean b = reader.nextBoolean();
	                writer.value(b);
	                break;
	            case NULL:
	                reader.nextNull();
	                writer.nullValue();
	                break;
	            case END_DOCUMENT:
	                return;
	            }
	        }
	    }
}
