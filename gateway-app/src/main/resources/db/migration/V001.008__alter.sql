ALTER TABLE application
ADD (
connect_timeout INTEGER DEFAULT 5000 NOT NULL,
read_timeout INTEGER DEFAULT 5000 NOT NULL,
retry_timeout INTEGER DEFAULT 5000 NOT NULL
);

ALTER TABLE application_instance
ADD (
weight INTEGER DEFAULT 0 NOT NULL
);