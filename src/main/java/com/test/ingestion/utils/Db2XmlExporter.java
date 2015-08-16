package com.test.ingestion.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.Test.Test.util.TestLogger;

public class Db2XmlExporter {

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
	public Db2XmlExporter() {
		log = new TestLogger(Db2XmlExporter.class);
		ALLOW_DUPLICATE = Boolean.TRUE.booleanValue();
		ONLY_UNIQUE = Boolean.FALSE.booleanValue();
		path = "\\profiles";
	}

	public static void main(String[] args) {
		Db2XmlExporter exporter = new Db2XmlExporter();
		exporter.setUp();
		exporter.exportTables(true);
		exporter.tearDown();
	}

	/**
	 * 
	 */
	public void setUp() {
		log.info("===> setup");
		try {
			applicationContext = new ClassPathXmlApplicationContext(
					new String[] { "mysql-schema-export-config.xml" });
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
	 */
	@SuppressWarnings("unchecked")
	private void exportTables(boolean shouldTrackChanges) {

		/*String[] tableNames = new String[] { "stg_qb_customer", "stg_qb_product",
				"stg_qb_account", "stg_qb_bill", "stg_qb_invoice", "stg_qb_invoice_lineitem",
				"stg_qb_salesrep", "stg_qb_payment"};*/
		
		String[] tableNames = new String[] { "stg_account", "stg_activity",
				"stg_employee", "stg_lead", "stg_opportunity", "stg_product", "stg_opportunity_stage" ,
				"stg_territory", "stg_user_territory",  
				"stg_opportunity_lineitem", "stg_pricebook", "stg_pricebook_entry", "stg_task"};
		
		//String[] tableNames = new String[] { "stg_group", "stg_account_share" };
		
		try {
			for (int i = 0; i < tableNames.length; i++) {
				String tableName = tableNames[i];
				log.info("Gather the Columns Info for the  Table :: "+tableName);
				
			String full_resource_path = (new StringBuilder(String.valueOf(path)))
					.append("\\").append(tableName).append(".xml").toString();
			Resource resource = applicationContext.getResource(full_resource_path);
			File sourceFile = null;
		///
			if (resource.exists()) {
				sourceFile = resource.getFile();
				FileUtils.forceDelete(sourceFile);
			}
			sourceFile = new File(full_resource_path);
			FileUtils.touch(sourceFile);
			
			///
				Collection lines = new ArrayList();
				((ArrayList) lines).ensureCapacity(20);
				lines.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				lines.add((new StringBuilder("<")).append(tableName)
						.append(">").toString());
				DatabaseMetaData metadata = con.getMetaData();
				for (ResultSet resultSet = metadata.getColumns(null, null,
						tableName, "%"); resultSet.next();) {
					String columnName = resultSet.getString("COLUMN_NAME");
					if (!columnName.equals("pkey")
							&& !columnName.equals("tenant_id")
							&& !columnName.equals("etl_timestamp")) {
						lines.add((new StringBuilder("<")).append(columnName)
								.append(">").append("</").append(columnName)
								.append(">").toString());
					}
				}

				lines.add((new StringBuilder("<KeepHistory>"))
						.append(shouldTrackChanges).append("</KeepHistory>")
						.toString());
				lines.add("<AppId>4</AppId>"); // 1 for SalesForce
				lines.add("<batchid>1</batchid>"); // 1 for SalesForce
				lines.add((new StringBuilder("</")).append(tableName)
						.append(">").toString());
				FileUtils.writeLines(sourceFile, lines);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
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
