INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER'), (2, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password, created_at)
VALUES (1, 'admin', '$2a$12$nA.MmuSVwJFmR5B04uwoX.HTPpk4Wpv7kYC/hsrVQkZcNL/4kGGeK', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1), (1, 2) ON CONFLICT (user_id, role_id) DO NOTHING;