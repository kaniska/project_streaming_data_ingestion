project_streaming_data_ingestion
================================

Contextual Data Transfer Protocol advocates a stateless multi-tenanted system with self-defined independent immutable contextual records ensuring data isolation, tenanted schema creation, data sclalability, sharding, auto-rollback through TIME-MACHINE , auto-restartable , multiple contextual views .

BI Data Analysis involves various types of models --  DYNAMIC LOGICAL MODELS (star schema, denarmalized flat tables, snowflake, ad-hoc schema) , DATA MANAGEMENT MODELS ( sources, data sets, groups) , DATA PROCESSING MODELS (Streaming-stage, Scheduling-stage, Trigering-stage, Query-execution-stage etc) , QUERY LANG MODEL (Dynamic KPI , SQL, Json, DSL) , OLAP MODEL (Stats func(), UDF)

##### Datasource communicates with SaaS Central
##### SaaS Central injects contextual info into streams
##### Ingest streaming contextual data
##### Store incoming data into staging data store
##### Adhoc Analysis on staged data
##### ETL raw data into analytics store
##### Historical query on transformed , denormalized facts

The Goal is to build a Data Integration App (discussed in a seperate git repo) and ingest contextual data in stateless manner and analyze the data.


##### Ingest streaming data

[Source] Synchronous Bulk Batches / CSV Business records / Asynchronous Fire-n-Forget / Continuous Feeds 

###### Data Integrator : lets assume Data Source (CRM channels) sending marketing leads or Data Integrator polling/pulling the feeds periodically 

###### Packet Optimization
<lead>
<lead_id>123</leade_id>
<company_name>TESCO</company_name>
<industry>RETAIL</industry>
<owner_employee_id>567</owner_employee_id>
<created_date></created_date>
<last_modified_date></last_modified_date>
<lead_status>DISCOVERY</lead_status>
<lead_source>EMAIL</lead_source>
<country>UK</country>
<state>OR</state>
<city>LONDON</city>
<converted_opportunity_id></converted_opportunity_id>
<converted_account_id></converted_account_id>
<converted_date></converted_date>
</lead>

###### SaaS Ingestion Layer : it can inject tenant_id , batch id , app id , timestamp, schema info and other business contexts into raw streams
> Data Ingestion can maintain a TIME_MACHINE table where every single batch will maintain current tx start time and last successful tx end time.
> The Time Machine helps to keep the Data Ingestion System stateless.

###### Data Integrator : may combine records into Mega Document and hands over sizable chuks to parallel threads

###### Data Ingestion Layer : push contexual schema-aware batches into immutable staging store

###### Query Engine : once data is stored, query in real-time bu Business User or automatically through ETL system
Ad-hoc example on MySQL : 
select a.lead_id, a.company_name, a.created_date
from lead a
inner join lead b on a.lead_id = b.lead_id 
and a.pkey < b.pkey
where a.created_date <> b.created_date
group by a.lead_id
order by 1;

> Data can stored into HDFS and HBASE can act as large scale ad hoc bigdata analytics engine
> MongoDB can be used to create multi-tenenated collections 
  -- https://github.com/kaniska/project_streaming_data_ingestion/blob/master/SQL-to-MongoMR.pdf


##### ETL process loads the staged data into analytics store
Data Ingestion API can directly call ETL API or push messages to Queue to trigger ETL
 ###### slowly changing dimensions (type I , type II)
 ###### parallel runs 
 ###### cascading logic
 ###### dimension lookup from cache
 
###### pre-computed complex queries (merged with incoming data in real time)
