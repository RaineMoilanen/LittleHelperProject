# createsqlclauses
Small java helper application to create sql insert or update clauses

Usage:
give as arguments:
1. file to read the column names and inserted/updated values (and/or where clauses)  - MANDATORY
2. whether this is update or insert ("update" or "insert")  - DEFAULT update
3. name of the table to be inserted to or updated to - DEFAULT TABLENAME
4. separator that is used in the file given in 1 (like , or ; or |)  - DEFAULT ,
5. file to write to - DEFAULT printing to command line
6. in case of update how many columns from the beginning are used as updated columns (the rest are used in where part of the clause) INT
7. Column name for some generic where rule (like "country_code")
8. The rule for generic where (like 'fi')

Example command line:
java LittleHelperProject C:\Users\username\Documents\values.csv update USERINFORMATION ; C:\Users\usrname\Documents\update.sql 1 COUNTRY_CODE 'FI'
