
CREATE TABLE header_rule (
  id          NUMBER(19, 0) PRIMARY KEY,
  name        VARCHAR(100)   NOT NULL UNIQUE,
  prefix_match        VARCHAR(100)  NOT NULL,
  application NUMBER(19, 0) NOT NULL,
  FOREIGN KEY (application) REFERENCES application (id)
);
