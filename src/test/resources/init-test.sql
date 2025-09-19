CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE IF NOT EXISTS rooms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL CHECK (capacity >= 1)
);

CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    room_id INT NOT NULL REFERENCES rooms(id),
    user_id INT NOT NULL,
    start_at timestamptz NOT NULL,
    end_at timestamptz NOT NULL,
    period tstzrange GENERATED ALWAYS AS (tstzrange(start_at, end_at, '[)')) STORED,CHECK (start_at < end_at),
    
    EXCLUDE USING gist (
        room_id WITH =,
        period WITH &&
    )
);


-- 회의실 생성 
INSERT INTO rooms (id, name, location, capacity)
VALUES (1, '회의실1', '서울시 강남구', 10)
ON CONFLICT (id) DO NOTHING;
