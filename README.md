Create a user in your database:

    CREATE USER "gateway" IDENTIFIED BY gateway;
    ACCOUNT UNLOCK;

    GRANT "RESOURCE" TO "gateway";
    GRANT "CONNECT" TO "gateway";
