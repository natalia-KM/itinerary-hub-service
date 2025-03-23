INSERT INTO dev.users (user_id, first_name, last_name, created_at, is_guest, google_id, currency, google_token)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'John', 'Doe', NOW(), TRUE, null, null, null);

INSERT INTO dev.users (user_id, first_name, last_name, created_at, is_guest, google_id, currency, google_token)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Jane', 'Smith', NOW(), FALSE, 'google-id', 'GBP', 'google-token');

INSERT INTO dev.trips (trip_id, user_id, trip_name, created_at, start_date, end_date, image_ref)
VALUES ('9c5bb970-faef-419b-a447-365b9471a4b0', '550e8400-e29b-41d4-a716-446655440000', 'Paris Trip', TIMESTAMP '2025-03-22 00:00:00', TIMESTAMP '2025-04-25 00:00:00', TIMESTAMP '2025-04-28 00:00:00', 'default');

INSERT INTO dev.trips (trip_id, user_id, trip_name, created_at, start_date, end_date, image_ref)
VALUES ('cca715f0-8092-4208-80d3-afb7ef35d7f7', '550e8400-e29b-41d4-a716-446655440000', 'London Trip',TIMESTAMP '2022-05-12 00:00:00', null, null, 'default');