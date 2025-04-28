-- Create messaging schema
CREATE SCHEMA IF NOT EXISTS messaging;

-- Create chat_rooms table
CREATE TABLE IF NOT EXISTS messaging.chat_rooms (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    participant1_id VARCHAR(255),
    participant2_id VARCHAR(255),
    participants TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    CONSTRAINT unique_participants UNIQUE (participant1_id, participant2_id)
);

-- Create chat_messages table
CREATE TABLE IF NOT EXISTS messaging.chat_messages (
    id SERIAL PRIMARY KEY,
    room_id VARCHAR(255) NOT NULL,
    sender_id VARCHAR(255) NOT NULL,
    sender_role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered BOOLEAN DEFAULT false,
    read BOOLEAN DEFAULT false,
    attachment_url TEXT,
    attachment_type VARCHAR(50)
);

-- Create user_presence table
CREATE TABLE IF NOT EXISTS messaging.user_presence (
    user_id VARCHAR(255) PRIMARY KEY,
    online BOOLEAN DEFAULT false,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_messages_room_id ON messaging.chat_messages(room_id);
CREATE INDEX IF NOT EXISTS idx_messages_sender_id ON messaging.chat_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messaging.chat_messages(created_at);
CREATE INDEX IF NOT EXISTS idx_rooms_participants ON messaging.chat_rooms(participant1_id, participant2_id);
CREATE INDEX IF NOT EXISTS idx_rooms_active ON messaging.chat_rooms(active);

-- Add comments for documentation
COMMENT ON TABLE messaging.chat_rooms IS 'Stores chat rooms for direct and group messages';
COMMENT ON TABLE messaging.chat_messages IS 'Stores individual chat messages';
COMMENT ON TABLE messaging.user_presence IS 'Tracks user online/offline status';

-- Grant necessary permissions
GRANT USAGE ON SCHEMA messaging TO public;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA messaging TO public;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA messaging TO public; 