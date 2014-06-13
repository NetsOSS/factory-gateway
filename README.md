Create a user in your database:

    -- USER SQL
    ALTER USER "test_bench"  IDENTIFIED BY test_bench
    ACCOUNT UNLOCK ;

    -- ROLES
    GRANT "RESOURCE" TO "test_bench" ;
    GRANT "CONNECT" TO "test_bench" ;

    -- SYSTEM PRIVILEGES

    -- QUOTAS
