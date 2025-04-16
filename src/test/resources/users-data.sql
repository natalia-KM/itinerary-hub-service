INSERT INTO dev.users (user_id, first_name, last_name, created_at, is_guest, google_id, currency, google_token)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'John', 'Doe', NOW(), TRUE, null, null, null);

INSERT INTO dev.users (user_id, first_name, last_name, created_at, is_guest, google_id, currency, google_token)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'Jane', 'Smith', NOW(), FALSE, 'google-id', 'GBP', 'google-token');

INSERT INTO dev.trips (trip_id, user_id, trip_name, created_at, start_date, end_date, image_ref)
VALUES ('9c5bb970-faef-419b-a447-365b9471a4b0', '550e8400-e29b-41d4-a716-446655440000', 'Paris Trip', TIMESTAMP '2025-03-22 00:00:00', TIMESTAMP '2025-04-25 00:00:00', TIMESTAMP '2025-04-28 00:00:00', 'default');

INSERT INTO dev.trips (trip_id, user_id, trip_name, created_at, start_date, end_date, image_ref)
VALUES ('cca715f0-8092-4208-80d3-afb7ef35d7f7', '550e8400-e29b-41d4-a716-446655440000', 'London Trip',TIMESTAMP '2022-05-12 00:00:00', null, null, 'default');

INSERT INTO dev.sections (section_id, trip_id, section_name, section_order)
VALUES ('a3c84e94-157b-436f-9e77-2b461c7c3bf2', '9c5bb970-faef-419b-a447-365b9471a4b0', 'Section 1',1);

INSERT INTO dev.sections (section_id, trip_id, section_name, section_order)
VALUES ('c13dd7ad-8f7d-4f93-8edd-ee3951097592', '9c5bb970-faef-419b-a447-365b9471a4b0', 'Section 2',2);

--Two options for guest account
INSERT INTO dev.options (option_id, section_id, option_name, option_order)
VALUES ('0d78ebf0-0159-4843-b54b-a696644f26fc', 'a3c84e94-157b-436f-9e77-2b461c7c3bf2', 'Option 1',1);

INSERT INTO dev.options (option_id, section_id, option_name, option_order)
VALUES ('eb7fd861-6dba-4893-a4c8-bac1bd5a47ba', 'a3c84e94-157b-436f-9e77-2b461c7c3bf2', 'Option 2',2);

--Transport element for option 1
INSERT INTO dev.base_elements (base_element_id, option_id, last_updated_at, element_type, element_category, link, price, notes, element_status)
VALUES ('4e52ae05-06dc-423f-b86f-51a00cb8c452', '0d78ebf0-0159-4843-b54b-a696644f26fc', '2022-05-12 00:00:00','TRANSPORT', 'Flight', null, 23.45, 'Notes', 'PENDING');

INSERT INTO dev.transport_elements (element_id, base_element_id, origin_place, origin_datetime, destination_place, destination_datetime, origin_provider, destination_provider, element_order)
VALUES ('674a2a9c-2dc5-4d00-a9ee-e4f051a17194', '4e52ae05-06dc-423f-b86f-51a00cb8c452', 'London Heathrow','2022-05-12 00:00:00', 'Paris', '2022-05-15 12:00:00', null, 'Ryanair',2);

--Accommodation elements for option 1
INSERT INTO dev.base_elements (base_element_id, option_id, last_updated_at, element_type, element_category, link, price, notes, element_status)
VALUES ('e4f56f0d-01ab-4ddb-be38-486ebefc4ede', '0d78ebf0-0159-4843-b54b-a696644f26fc', '2022-05-15 00:00:00','ACCOMMODATION', 'Hotel', 'https://book-hotel.ih/', null, null, null);

INSERT INTO dev.accommodation_elements (element_id, base_element_id, place, location)
VALUES ('4fc652e6-0fdc-4d69-8a3a-61ca6ab6ddb1', 'e4f56f0d-01ab-4ddb-be38-486ebefc4ede', 'Hotel Name','Paris, Some Street');

INSERT INTO dev.accommodation_events (event_id, accommodation_id, type, datetime, element_order)
VALUES ('0347f675-1040-461c-b3ec-af80a0910850', '4fc652e6-0fdc-4d69-8a3a-61ca6ab6ddb1', 'CHECK_IN','2022-05-12 12:30:00', 1);

INSERT INTO dev.accommodation_events (event_id, accommodation_id, type, datetime, element_order)
VALUES ('f8876598-c138-4bd1-8055-5294bda159be', '4fc652e6-0fdc-4d69-8a3a-61ca6ab6ddb1', 'CHECK_OUT','2022-05-13 14:00:00', 3);

--Activity element for option 2
INSERT INTO dev.base_elements (base_element_id, option_id, last_updated_at, element_type, element_category, link, price, notes, element_status)
VALUES ('b647b387-31ad-4ffb-a9d2-91551d4b3138', 'eb7fd861-6dba-4893-a4c8-bac1bd5a47ba', '2022-05-20 22:00:00','ACTIVITY', 'Restaurant',null, 1000.00, null, 'BOOKED');

INSERT INTO dev.activity_elements (element_id, base_element_id, activity_name, location, starts_at, duration, element_order)
VALUES ('82c24ee6-075f-4d8c-913d-1d06f325fd43', 'b647b387-31ad-4ffb-a9d2-91551d4b3138', 'Escape Room','Paris, Street 2', '2022-05-15 13:00:00', '120', 1);

--Passengers
INSERT INTO dev.passengers (passenger_id, first_name, last_name, avatar, user_id)
VALUES ('0e85075f-be86-4b31-96ec-08feea54fb0e', 'John', 'Doe', 'dog', '550e8400-e29b-41d4-a716-446655440000');

INSERT INTO dev.passengers (passenger_id, first_name, last_name, avatar, user_id)
VALUES ('3c2c02d3-8a7f-4a1c-94bb-4cce3e9b90c1', 'Alice', 'Smith', 'cat', '550e8400-e29b-41d4-a716-446655440000');

INSERT INTO dev.passengers (passenger_id, first_name, last_name, avatar, user_id)
VALUES ('e0a5409f-6b9e-4f2c-8418-a9275aa4ae52', 'Bob', 'Johnson', 'parrot', '550e8400-e29b-41d4-a716-446655440000');

INSERT INTO dev.passengers (passenger_id, first_name, last_name, avatar, user_id)
VALUES ('d2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f', 'Clara', 'Nguyen', 'fox', '550e8400-e29b-41d4-a716-446655440000');

INSERT INTO dev.passengers (passenger_id, first_name, last_name, avatar, user_id)
VALUES ('ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57', 'Ethan', 'Brown', 'turtle', '550e8400-e29b-41d4-a716-446655440000');

--Element Passengers

--Transport Element
INSERT INTO dev.element_passengers (id, passenger_id, base_element_id)
VALUES ('414196f2-81f3-4207-9aea-cc858f578d43', '0e85075f-be86-4b31-96ec-08feea54fb0e', '4e52ae05-06dc-423f-b86f-51a00cb8c452');

INSERT INTO dev.element_passengers (id, passenger_id, base_element_id)
VALUES ('d1badc50-2ba4-4f0f-a5bd-024652f45732', '3c2c02d3-8a7f-4a1c-94bb-4cce3e9b90c1', '4e52ae05-06dc-423f-b86f-51a00cb8c452');

INSERT INTO dev.element_passengers (id, passenger_id, base_element_id)
VALUES ('db6f31a4-17f5-4afe-9807-1b6a6943df4e', 'e0a5409f-6b9e-4f2c-8418-a9275aa4ae52', '4e52ae05-06dc-423f-b86f-51a00cb8c452');

INSERT INTO dev.element_passengers (id, passenger_id, base_element_id)
VALUES ('b43d17da-3bf9-4f1e-a973-0d78cde5f977', 'd2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f', '4e52ae05-06dc-423f-b86f-51a00cb8c452');

--Accom Element
INSERT INTO dev.element_passengers (id, passenger_id, base_element_id)
VALUES ('745b5d92-7d0c-475a-9d42-87c0315ba4a7', 'd2f9a4d1-33f6-40cf-b46d-9b81f3c0a15f', 'e4f56f0d-01ab-4ddb-be38-486ebefc4ede');

INSERT INTO dev.element_passengers (id, passenger_id, base_element_id)
VALUES ('ced9c95a-a0f6-481d-9d62-42cd12e4df1b', 'ba9d1df2-99b1-4df4-ae00-c5d9ef6e5f57', 'e4f56f0d-01ab-4ddb-be38-486ebefc4ede');