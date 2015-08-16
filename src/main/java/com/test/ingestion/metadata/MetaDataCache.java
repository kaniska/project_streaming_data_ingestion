/**
 * 
 */
package com.test.ingestion.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kaniska_Mandal
 *
 */
public class MetaDataCache {
	
	private static Map<String, List<String>> columnNameMap = new ConcurrentHashMap<String, List<String>>(1);

	public static Map<String, List<String>> getColumnNameMap() {
		return columnNameMap;
	}
	//DATA_TYPE
	private static Map<String, List<String>> columnTypeMap = new ConcurrentHashMap<String, List<String>>(1);

	public static Map<String, List<String>> getColumnTypeMap() {
		return columnTypeMap;
	}

	
	/**
	 * @param tableName
	 * @param con
	 * @param columnCacheKey
	 * @throws Exception 
	 */
	public static void prepareTableColumnCache(String tableName, Connection con,
			String tableCacheKey) throws Exception {
		if(null == MetaDataCache.getColumnNameMap().get(tableCacheKey)) {
			DatabaseMetaData dbMetadata = con.getMetaData();
			ResultSet columnMetaInfo = null;
			try {
				columnMetaInfo = dbMetadata.getColumns("ca_rawdata%", "ca_rawdata%", tableName, null);
				List<String> columnNameInfo = new LinkedList<String>();
				List<String> columnTypeInfo = new LinkedList<String>();
				// 
				while (columnMetaInfo.next()) {
					String columnName = columnMetaInfo.getString("COLUMN_NAME");
					if(!columnName.equalsIgnoreCase("pkey")) {
						columnNameInfo.add(columnName);
						columnTypeInfo.add(columnMetaInfo.getString("DATA_TYPE"));
					}
				}
				MetaDataCache.getColumnNameMap().put(tableCacheKey, columnNameInfo);
				MetaDataCache.getColumnTypeMap().put(tableCacheKey, columnTypeInfo);
				
			} catch (Exception e) {
				throw e;
			}finally{
				columnMetaInfo.close();
			}
		}
	}
		
}
