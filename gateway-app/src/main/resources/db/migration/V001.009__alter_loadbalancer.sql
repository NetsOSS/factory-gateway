ALTER TABLE load_balancer
ADD user_name VARCHAR(100) DEFAULT 'factory' NOT NULL
MODIFY ssh_key VARCHAR(2500);