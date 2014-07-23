ALTER TABLE application
DROP (
  connect_timeout,
  read_timeout,
  retry_timeout
);

ALTER TABLE load_balancer
ADD (
  check_timeout INTEGER DEFAULT 2000 NOT NULL,
  connect_timeout INTEGER DEFAULT 1000 NOT NULL,
  server_timeout INTEGER DEFAULT 60000 NOT NULL,
  client_timeout INTEGER DEFAULT 60000 NOT NULL,
  retries INTEGER DEFAULT 3 NOT NULL,
  CONSTRAINT chk_server_client CHECK (server_timeout = client_timeout)
);

ALTER TABLE header_rule
DROP UNIQUE (name);
