package com.test.ingestion.rules;

import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Node;

import com.test.ingestion.metadata.MetaDataCache;

public class SQLRules {

	/**
	 * This code treats the XML Metadata document as the source for insert sql
	 * parameters
	 * 
	 * @param xmlNodeList
	 * @param columnCacheKey
	 * @return
	 */
	public static String[] gennerateInsertStatementFromXML(List<Node> xmlNodeList,
			String columnCacheKey) {
		Iterator<?> iter = xmlNodeList.iterator();
		StringBuilder columnNames = new StringBuilder();
		StringBuilder paramPlaceHolders = new StringBuilder();

		List<String> columnsSet = MetaDataCache.getColumnNameMap().get(
				columnCacheKey);

		String[] sqlStrings = new String[2];
		// ////////////////////////////////////////////////
		if (iter.hasNext()) {
			Element elm = (Element) iter.next();

			List<Element> childelm = elm.elements(); // children
			Iterator<?> subiter = childelm.iterator();

			int numberOfCols = childelm.size();

			int i = 0;
			while (subiter.hasNext()) {
				Element subelm1 = (Element) subiter.next();
				String columnName = subelm1.getName();
				if (!columnsSet.contains(columnName)) {
					// NOOP - column does not exist in table like AppId,
					// KeepHistory
					i++;
					continue;
				}
				columnNames.append(columnName);
				paramPlaceHolders.append("?");

				if (i++ <= numberOfCols) {
					columnNames.append(",");
					paramPlaceHolders.append(",");
				}

				if (DataTypeRules.isOfDateType(subelm1)) {
					numberOfCols++;
					String timeDimColumnName = columnName
							+ "_time_dimension_id";
					;
					columnNames.append(timeDimColumnName);
					paramPlaceHolders.append("?");
					if (i++ <= numberOfCols) {
						columnNames.append(",");
						paramPlaceHolders.append(",");
					}
				}

			}
		}

		if (!columnNames.toString().trim().endsWith(",")) {
			columnNames.append(",");
			paramPlaceHolders.append(",");
		}

		columnNames.append("tenant_id");
		paramPlaceHolders.append("?");
		columnNames.append(", etl_timestamp");
		paramPlaceHolders.append(", ?");

		sqlStrings[0] = columnNames.toString();
		sqlStrings[1] = paramPlaceHolders.toString();

		return sqlStrings;
	}

	/**
	 * For a given cache key lookup the column names
	 * @param tableCacheKey
	 * @return
	 */
	public static String[] gennerateInsertStatement(String tableCacheKey) {
		StringBuilder columnNames = new StringBuilder();
		StringBuilder paramPlaceHolders = new StringBuilder();

		List<String> columnsNameSet = MetaDataCache.getColumnNameMap().get(
				tableCacheKey);

		String[] sqlStrings = new String[2];
		int numberOfCols = columnsNameSet.size();
		int i = 0;
		for (String columnName : columnsNameSet) {
			columnNames.append(columnName);
			paramPlaceHolders.append("?");

			if (i++ < numberOfCols-1) {
				columnNames.append(",");
				paramPlaceHolders.append(",");
			}
		}		
		sqlStrings[0] = columnNames.toString();
		sqlStrings[1] = paramPlaceHolders.toString();

		return sqlStrings;
	}
}