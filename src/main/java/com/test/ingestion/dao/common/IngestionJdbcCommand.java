/**
 * 
 */
package com.test.ingestion.dao.common;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Created this class to access JdbcTemplate 
 */
public final class IngestionJdbcCommand extends SimpleJdbcInsert {

	public TestJdbcCommand(DataSource dataSource) {
		super(dataSource);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return super.getJdbcTemplate();
	}


}
