ALTER TABLE application
ADD CONSTRAINT uq_app_grp_index UNIQUE (
  application_group,
  index_order
);
