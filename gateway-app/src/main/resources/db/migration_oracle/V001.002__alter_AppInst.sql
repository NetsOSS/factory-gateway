ALTER TABLE application_instance
ADD backup NUMBER(1) DEFAULT 0;

ALTER TABLE application_instance
MODIFY path VARCHAR(100) NULL;