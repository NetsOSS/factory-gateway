CREATE SEQUENCE id_sequence
MINVALUE 0
START WITH 10000
INCREMENT BY 1000
CACHE 20;

CREATE TABLE application_group (
  id   NUMBER(19, 0)  PRIMARY KEY,
  name VARCHAR(100)   NOT NULL UNIQUE,
  port                INTEGER UNIQUE
);

CREATE TABLE load_balancer (
  id                NUMBER(19, 0) PRIMARY KEY,
  name              VARCHAR(30)   NOT NULL UNIQUE,
  host              VARCHAR(100)  NOT NULL,
  installation_path VARCHAR(200)  NOT NULL,
  ssh_key           VARCHAR(2500) NOT NULL,
  public_port       INTEGER       NOT NULL,
  user_name         VARCHAR(100)  NOT NULL,
  check_timeout     INTEGER DEFAULT 2000  NOT NULL,
  connect_timeout   INTEGER DEFAULT 1000  NOT NULL,
  server_timeout    INTEGER DEFAULT 60000 NOT NULL,
  client_timeout    INTEGER DEFAULT 60000 NOT NULL,
  retries           INTEGER DEFAULT 3 NOT NULL,
  CONSTRAINT uq_host_installation_path  UNIQUE (host, installation_path),
  CONSTRAINT uq_host_public_port        UNIQUE (host, public_port),
  CONSTRAINT chk_server_client          CHECK (server_timeout = client_timeout)
);

CREATE TABLE application (
  id                            NUMBER(19, 0) PRIMARY KEY,
  name                          VARCHAR(100)  NOT NULL UNIQUE,
  public_path                     VARCHAR(1000) NOT NULL,
  application_group             NUMBER(19, 0) NOT NULL,
  emails                        VARCHAR(1000),
  checkpath                     VARCHAR(1000) NOT NULL,
  sticky_session                INTEGER DEFAULT 0 NOT NULL,
  failover_load_balancer_setup  INTEGER DEFAULT 0 NOT NULL,
  index_order                   INTEGER NOT NULL,
  private_path                  VARCHAR(255),
  FOREIGN KEY (application_group) REFERENCES application_group (id),
  CONSTRAINT uq_app_grp_index UNIQUE (application_group, index_order)
);

CREATE TABLE application_instance (
  id              NUMBER(19, 0) PRIMARY KEY,
  name            VARCHAR(30)   NOT NULL UNIQUE,
  host            VARCHAR(100)  NOT NULL,
  port            INTEGER       NOT NULL,
  path            VARCHAR(100)  NOT NULL,
  application     NUMBER(19, 0) NOT NULL,
  ha_proxy_state  INTEGER DEFAULT 0 NOT NULL,
  weight          INTEGER DEFAULT 0 NOT NULL,
  FOREIGN KEY (application) REFERENCES application (id)
);

CREATE TABLE load_balancer_application (
  application_id    NUMBER(19, 0),
  load_balancer_id  NUMBER(19, 0),
  PRIMARY KEY (application_id, load_balancer_id),
  FOREIGN KEY (application_id) REFERENCES application (id),
  FOREIGN KEY (load_balancer_id) REFERENCES load_balancer (id)
);

CREATE TABLE header_rule (
  id            NUMBER(19, 0) PRIMARY KEY,
  name          VARCHAR(100)  NOT NULL,
  prefix_match  VARCHAR(100)  NOT NULL,
  application   NUMBER(19, 0) NOT NULL,
  FOREIGN KEY (application) REFERENCES application (id)
);