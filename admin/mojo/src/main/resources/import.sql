-- NOTE: these queries have to be on single lines to function properly.

-- Load initial admin user
INSERT INTO users (id, applicationRole, email, name, userName) values (1, 'ADMIN', 'test@example.com', 'Admin User', 'admin');
-- Load initial guest user
INSERT INTO users (id, applicationRole, email, name, userName) values (2, 'VIEWER', 'test@example.com', 'Guest User', 'guest');
