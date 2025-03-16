INSERT INTO dev.users (user_id, first_name, last_name, created_at, is_guest, google_id, currency, google_token)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'John', 'Doe', NOW(), TRUE, null, null, null);

INSERT INTO dev.users (user_id, first_name, last_name, created_at, is_guest, google_id, currency, google_token)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Jane', 'Smith', NOW(), FALSE, 'google-id', 'GBP', 'google-token');
