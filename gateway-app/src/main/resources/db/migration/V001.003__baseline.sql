DROP TABLE load_balancer_application;
DROP TABLE application_instance;
DROP TABLE application;
DROP TABLE load_balancer;
DROP TABLE application_group;

CREATE TABLE application_group (
  id   NUMBER(19, 0) PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE load_balancer (
  id                NUMBER(19, 0) PRIMARY KEY,
  name              VARCHAR(30)  NOT NULL,
  host              VARCHAR(100) NOT NULL,
  installation_path VARCHAR(200) NOT NULL,
  ssh_key           VARCHAR(700) NOT NULL,
  public_port        INTEGER      NOT NULL
);

CREATE TABLE application (
  id                NUMBER(19, 0) PRIMARY KEY,
  name              VARCHAR(100)  NOT NULL,
  publicURL         VARCHAR(1000) NOT NULL,
  application_group NUMBER(19, 0) NOT NULL,
  FOREIGN KEY (application_group) REFERENCES application_group (id)
);

CREATE TABLE application_instance (
  id          NUMBER(19, 0) PRIMARY KEY,
  name        VARCHAR(30)   NOT NULL,
  host        VARCHAR(100)  NOT NULL,
  port        INTEGER       NOT NULL,
  path        VARCHAR(100)  NOT NULL,
  application NUMBER(19, 0) NOT NULL,
  FOREIGN KEY (application) REFERENCES application (id)
);

CREATE TABLE load_balancer_application (
  application_id   NUMBER(19, 0),
  load_balancer_id NUMBER(19, 0),
  PRIMARY KEY (application_id, load_balancer_id),
  FOREIGN KEY (application_id) REFERENCES application (id),
  FOREIGN KEY (load_balancer_id) REFERENCES load_balancer (id)
);
