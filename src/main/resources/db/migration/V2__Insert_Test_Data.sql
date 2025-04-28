-- Insert test users presence
INSERT INTO messaging.user_presence (user_id, online, last_seen) VALUES
('manager123', true, CURRENT_TIMESTAMP),
('driver123', true, CURRENT_TIMESTAMP),
('driver456', false, CURRENT_TIMESTAMP - INTERVAL '10 minutes'),
('driver789', true, CURRENT_TIMESTAMP);

-- Insert chat rooms
INSERT INTO messaging.chat_rooms (type, participant1_id, participant2_id, active, created_at) VALUES
('direct', 'manager123', 'driver123', true, CURRENT_TIMESTAMP),
('direct', 'manager123', 'driver456', true, CURRENT_TIMESTAMP),
('direct', 'manager123', 'driver789', true, CURRENT_TIMESTAMP);

-- Insert group chat room
INSERT INTO messaging.chat_rooms (type, participants, active, created_at) VALUES
('group', 'manager123,driver123,driver456,driver789', true, CURRENT_TIMESTAMP);

-- Insert test messages for direct chat between manager and driver123
INSERT INTO messaging.chat_messages (room_id, sender_id, sender_role, content, created_at, delivered, read) VALUES
('driver-manager-ORD101', 'manager123', 'manager', 'New delivery assigned: Order #12345', CURRENT_TIMESTAMP - INTERVAL '1 hour', true, true),
('driver-manager-ORD101', 'driver123', 'driver', 'Received. Heading to pickup location now.', CURRENT_TIMESTAMP - INTERVAL '55 minutes', true, true),
('driver-manager-ORD101', 'manager123', 'manager', 'Customer requested delivery before 5 PM', CURRENT_TIMESTAMP - INTERVAL '50 minutes', true, true),
('driver-manager-ORD101', 'driver123', 'driver', 'Will make it on time. Currently 15 minutes away.', CURRENT_TIMESTAMP - INTERVAL '45 minutes', true, true),
('driver-manager-ORD101', 'manager123', 'manager', 'Great! Keep me updated.', CURRENT_TIMESTAMP - INTERVAL '40 minutes', true, false);

-- Insert test messages for group chat
INSERT INTO messaging.chat_messages (room_id, sender_id, sender_role, content, created_at, delivered, read) VALUES
('manager-group', 'manager123', 'manager', 'Good morning team! Please update your delivery status.', CURRENT_TIMESTAMP - INTERVAL '2 hours', true, true),
('manager-group', 'driver123', 'driver', 'Order #12345 picked up, en route to customer.', CURRENT_TIMESTAMP - INTERVAL '1 hour 45 minutes', true, true),
('manager-group', 'driver456', 'driver', 'Completed delivery for Order #12346.', CURRENT_TIMESTAMP - INTERVAL '1 hour 30 minutes', true, true),
('manager-group', 'driver789', 'driver', 'Starting my route now.', CURRENT_TIMESTAMP - INTERVAL '1 hour 15 minutes', true, true),
('manager-group', 'manager123', 'manager', 'Thanks for the updates. Remember to mark deliveries as completed in the system.', CURRENT_TIMESTAMP - INTERVAL '1 hour', true, false); 