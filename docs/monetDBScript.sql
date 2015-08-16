-- // 
select ('START');


DROP TABLE employee ;
CREATE  TABLE employee (
  pkey INT NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(30) NOT NULL ,
  tenant_id INT NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  is_active VARCHAR(10) NULL ,
  employee_name VARCHAR(100) NULL ,
  last_modified_date VARCHAR(30) NULL ,
  last_modified_date_time_dimension_id int NOT NULL ,
  department VARCHAR(40) NULL DEFAULT 'Unknown' ,
  created_date VARCHAR(30) NULL ,
  created_date_time_dimension_id int NOT NULL ,
  operation VARCHAR(3) NULL ,
  batchid INT NULL,
  PRIMARY KEY (pkey) );
-- //

DROP TABLE opportunity ;
CREATE  TABLE opportunity (
  pkey INT NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(30) NOT NULL ,
  tenant_id INT NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  owner_employee_id VARCHAR(30) NULL ,
  last_modified_date VARCHAR(30) NULL DEFAULT NULL ,
  last_modified_date_time_dimension_id int NOT NULL ,
  opportunity_stage_name VARCHAR(40) NULL ,
  lead_source VARCHAR(200) NULL ,
  account_id VARCHAR(30) NULL ,
  opportunity_type VARCHAR(40) NULL DEFAULT NULL ,
  amount VARCHAR(30) NULL DEFAULT NULL ,
  name VARCHAR(200) NULL DEFAULT 'Unknown' ,
  currency_iso_code VARCHAR(40) NULL DEFAULT 'USD' ,
  created_date VARCHAR(50) NULL ,
  created_date_time_dimension_id int NOT NULL ,
  close_date VARCHAR(50) NULL ,
  close_date_time_dimension_id int NOT NULL ,
  batchid INT NULL,
  operation VARCHAR(3) NULL ,
  PRIMARY KEY (pkey) );

-- //

-- IF EXISTS(select 1 from sys.objects where object_id = 'opportunity_stage' )
DROP TABLE opportunity_stage ;
CREATE TABLE opportunity_stage (
  pkey int NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(30) NOT NULL ,
  tenant_id INT  NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  name VARCHAR(100) NULL ,
 probability VARCHAR(6) NULL ,
 last_modified_date VARCHAR(30) NULL ,
  last_modified_date_time_dimension_id int NOT NULL ,
  time_dimension_id int NULL ,
  isactive VARCHAR(5) NULL ,
  isclosed VARCHAR(5) NULL ,
  iswon VARCHAR(5) NULL ,
  operation VARCHAR(3) NULL ,
  batchid INT NULL,
  PRIMARY KEY (pkey) );

-- //  
  
DROP TABLE test ;
CREATE  TABLE test (
  pkey INT NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(50) NOT NULL ,
  tenant_id INT NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  owner_employee_id VARCHAR(50) NULL ,
  last_modified_date VARCHAR(50) NULL DEFAULT NULL ,
  opportunity_stage_name VARCHAR(100) NULL ,
  account_id VARCHAR(50) NULL ,
  opportunity_type VARCHAR(100) NULL DEFAULT NULL ,
  amount VARCHAR(50) NULL DEFAULT NULL ,
  name VARCHAR(200) NULL DEFAULT 'Unknown' ,
  currency_iso_code VARCHAR(40) NULL DEFAULT 'USD' ,
  created_date VARCHAR(50) NULL ,
  close_date VARCHAR(50) NULL ,
  batch_id INT NULL ,
  fiscal_quarter VARCHAR(45) NULL ,
  fiscal_year VARCHAR(45) NULL ,
  lead_source VARCHAR(150) NULL ,
  forecast_category VARCHAR(150) NULL ,
  operation VARCHAR(3) not NULL ,
  PRIMARY KEY (pkey) );

-- //

-- -----------------------------------------------------
-- Table `pricebook`
-- -----------------------------------------------------
DROP TABLE pricebook ;
CREATE  TABLE pricebook (
  pkey INT  NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(30) NOT NULL ,
  tenant_id INT NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  name VARCHAR(100) NULL ,
  last_modified_date VARCHAR(45) NULL ,
  last_modified_date_time_dimension_id int NOT NULL ,
  batchid INT NULL,
  operation VARCHAR(3) not NULL ,
  PRIMARY KEY (pkey) );

-- -----------------------------------------------------
-- Table `pricebook_entry`
-- -----------------------------------------------------
DROP TABLE pricebook_entry ;
CREATE  TABLE pricebook_entry (
  pkey INT  NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(30) NOT NULL ,
  tenant_id INT  NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  price_book_id VARCHAR(45) NULL ,
  product_id VARCHAR(45) NULL ,
  last_modified_date VARCHAR(45) NULL ,
  last_modified_date_time_dimension_id int NOT NULL ,
  batchid INT NULL,
  operation VARCHAR(3) not NULL ,  
  PRIMARY KEY (pkey) );


-- -----------------------------------------------------
-- Table `product`
-- -----------------------------------------------------
DROP TABLE product ;
CREATE  TABLE product (
  pkey INT  NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(30) NOT NULL ,
  tenant_id INT  NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  name VARCHAR(100) NULL ,
   created_date VARCHAR(45) NULL ,
   created_date_time_dimension_id int NOT NULL ,
   is_active VARCHAR(45) NULL,
  last_modified_date VARCHAR(45) NULL ,
    last_modified_date_time_dimension_id int NOT NULL ,
  batchid INT NULL,
  operation VARCHAR(3) not NULL ,  
  PRIMARY KEY (pkey) );


-- -----------------------------------------------------
-- Table `opportunity_lineitem`
-- -----------------------------------------------------
DROP TABLE opportunity_lineitem ;
CREATE  TABLE opportunity_lineitem (
  pkey INT  NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(30) NOT NULL ,
  tenant_id INT  NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  last_modified_date VARCHAR(30) NULL ,
   last_modified_date_time_dimension_id int NOT NULL ,
  opportunity_id VARCHAR(35) NULL ,
  pricebook_entry_id VARCHAR(35) NULL ,
  total_price VARCHAR(35) NULL ,
  batchid INT NULL,
  operation VARCHAR(3) not NULL ,  
  PRIMARY KEY (pkey) );


-- -----------------------------------------------------
-- Table `account`
-- -----------------------------------------------------
DROP TABLE account ;
CREATE  TABLE account (
  pkey INT  NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(30) NOT NULL ,
  tenant_id INT  NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  account_name VARCHAR(1000) NULL ,
  last_modified_date VARCHAR(30) NULL ,
  last_modified_date_time_dimension_id int NOT NULL ,
  city_name VARCHAR(100) NULL ,
  state_name VARCHAR(100) NULL ,
  country_name VARCHAR(100) NULL ,
  industry VARCHAR(45) NULL ,
  owner_employee_id VARCHAR(45) NULL ,
  account_type VARCHAR(45) NULL DEFAULT 'Unknown' ,
  account_status VARCHAR(10) NULL DEFAULT 'A' ,
  created_date VARCHAR(30) NULL ,
  created_date_time_dimension_id int NOT NULL ,
  batchid INT NULL,
  operation VARCHAR(3) NULL ,
  PRIMARY KEY (pkey) );


-- -----------------------------------------------------
-- Table `lead`
-- -----------------------------------------------------
DROP TABLE lead ;
CREATE  TABLE lead (
  pkey INT NOT NULL AUTO_INCREMENT ,
  entity_id VARCHAR(40) NOT NULL ,
  tenant_id INT NOT NULL ,
  etl_timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ,
  company_name VARCHAR(100) NULL ,
  industry VARCHAR(50) NULL DEFAULT NULL ,
  owner_employee_id VARCHAR(40) NULL ,
   created_date VARCHAR(45) NULL ,
   created_date_time_dimension_id int NOT NULL ,
  last_modified_date VARCHAR(30) NULL ,
  last_modified_date_time_dimension_id int NOT NULL ,
  lead_status VARCHAR(100) NULL DEFAULT 'Open' ,
  lead_source VARCHAR(200) NULL ,
  country VARCHAR(100) NULL DEFAULT 'US' ,
  state VARCHAR(100) NULL DEFAULT 'CA' ,
  city VARCHAR(100) NULL DEFAULT 'SF' ,
  converted_opportunity_id VARCHAR(45) NULL ,
  converted_account_id VARCHAR(45) NULL ,
  converted_date VARCHAR(30) NULL ,
  converted_date_time_dimension_id int NOT NULL ,
  batchid INT NULL,
  operation VARCHAR(3) NULL ,
  PRIMARY KEY (pkey) );

-- // 
DROP table batch_log;
CREATE  TABLE batch_log (
  pkey INT NOT NULL AUTO_INCREMENT ,
  app_id INT NOT NULL ,
  tenant_id INT NOT NULL ,
  status CHAR(1) NOT NULL ,
  current_run_date_from VARCHAR(50) NOT NULL DEFAULT '1970-01-01 05:05:05' ,
  current_run_date_to VARCHAR(50) NOT NULL ,
  updated_record_count INT NULL DEFAULT 0 ,
  deleted_record_count INT NULL DEFAULT 0 ,
  new_record_count INT NULL DEFAULT 0 ,
  TEST_batch_start_time VARCHAR(50) NULL DEFAULT NULL ,
  TEST_batch_end_time VARCHAR(50) NULL DEFAULT NULL ,
  generic_app_name VARCHAR(25) NOT NULL DEFAULT 'SFDC' ,
  datasource VARCHAR(50) NULL ,
  PRIMARY KEY (pkey) );
  --   INDEX TENANT_ID_IDX (`tenant_id` ASC) );

-- //

DROP TABLE time_dimension;
CREATE TABLE time_dimension (
    time_dimension_id INT NOT NULL AUTO_INCREMENT, 
      tenant_id INT NOT NULL ,
  a_year CHAR(4) NOT NULL ,
  a_quarter CHAR(2) NOT NULL ,
  a_year_quarter CHAR(9) NOT NULL ,
  a_month CHAR(2) NOT NULL ,
  a_month_name VARCHAR(45) NOT NULL ,
  a_year_month CHAR(9) NOT NULL ,
  a_week CHAR(2) NOT NULL ,
  a_year_week CHAR(9) NOT NULL ,
  a_day CHAR(2) NOT NULL ,
  a_day_name VARCHAR(20) NOT NULL ,
  a_day_of_year CHAR(3) NOT NULL ,
  date_value DATE NOT NULL ,
  int_date INT NOT NULL ); 

-- // 
select ('END');


