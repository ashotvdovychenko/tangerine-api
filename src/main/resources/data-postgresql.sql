INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER'), (2, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;

INSERT INTO users (username, password, created_at)
VALUES ('admin', '$2a$12$KJ2OnYf.tUXS8DthukuNlOfJ54OVNzAuB3kfCzLodTsbWDJlHhUDy', to_timestamp(0))
ON CONFLICT (username) DO NOTHING;

INSERT INTO users_roles (user_id, role_id) VALUES (1, 1), (1, 2) ON CONFLICT (user_id, role_id) DO NOTHING;