ALTER TABLE application
ADD (
sticky_session INTEGER DEFAULT 0 NOT NULL,
failover_load_balancer_setup INTEGER DEFAULT 0 NOT NULL
);

ALTER TABLE application_instance
ADD (
ha_proxy_state INTEGER DEFAULT 0 NOT NULL
);