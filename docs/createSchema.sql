monetdbd stop /var/lib/monetdb
monetdbd create  /var/lib/monetdb
monetdbd start  /var/lib/monetdb
monetdb create test
monetdb release test
CREATE USER "test" WITH PASSWORD 'test' NAME 'test' SCHEMA "sys";
mclient -u test -d ca_rawdata_s1
CREATE SCHEMA "test" AUTHORIZATION "test";
ALTER USER "test" SET SCHEMA "test";
