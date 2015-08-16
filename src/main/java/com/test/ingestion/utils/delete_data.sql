-- call delete_fact_data(${TENANT_ID}, 1, 'fact_profit_and_loss');

drop procedure if exists delete_fact_data;

DELIMITER @@
CREATE PROCEDURE delete_fact_data(p_tenant_id INT, p_number_of_years INT, p_table_name varchar(100))
 BEGIN
     set @dtYear=-1;
     set @startDate=-1;
     set @table_name = p_table_name;
     set @tenant_id = p_tenant_id;
     
     select YEAR(CURDATE())-p_number_of_years into @dtYear;
     SELECT STR_TO_DATE(CONCAT_WS('-',@dtYear,1,1),'%Y-%m-%d') into @startDate;
     
     -- select @startDate;     
     
     SET @sql_text =concat('delete from ',@table_name,' where transaction_utc_date_id in (select dim_utc.dim_utc_date_id from dim_utc_date as dim_utc where (Date(dim_utc.date_value) >= Date(@startDate)) and (Date(dim_utc.date_value) <= CURDATE()) and dim_utc.tenant_id = ',@tenant_id,')');
   
   PREPARE stmt FROM @sql_text;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;  
      
END;
@@


call delete_fact_data(2,1, 'fact_profit_and_loss');
