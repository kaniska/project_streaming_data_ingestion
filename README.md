project_streaming_data_ingestion
================================
### Datasource communicates with SaaS Central
### SaaS Central injects contextual info into streams
### Ingest streaming contextual data
### Store incoming data into staging data store
### Adhoc Analysis on staged data
### ETL raw data into analytics store
### Historical query on transformed , denormalized facts

The Goal is to build a Data Integration App (discussed in a seperate git repo) and ingest contextual data in stateless manner and analyze the data

### Ingest streaming data

#### lets assume we are receiving marketing leads .
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

#### next we inject tenant_id , batch id and other business contexts into raw streams

#### once data is stored, query in real-time bu Business User or automatically through ETL system
Ad-hoc example on MySQL : 
select a.lead_id, a.company_name, a.created_date
from lead a
inner join lead b on a.lead_id = b.lead_id 
and a.pkey < b.pkey
where a.created_date <> b.created_date
group by a.lead_id
order by 1;
