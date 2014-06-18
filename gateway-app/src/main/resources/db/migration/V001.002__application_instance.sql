CREATE TABLE application_instance (
  id   NUMBER(19, 0) PRIMARY KEY,
  name VARCHAR(30)  NOT NULL,
  host VARCHAR(100) NOT NULL,
  port INTEGER      NOT NULL,
  path VARCHAR(100) NOT NULL
);


