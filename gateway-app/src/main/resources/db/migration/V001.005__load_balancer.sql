CREATE TABLE load_balancer (
  id      NUMBER(19, 0) PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  host    VARCHAR(100) NOT NULL,
  installation_path VARCHAR(200) NOT NULL,
  ssh_key VARCHAR(700) NOT NULL
);



