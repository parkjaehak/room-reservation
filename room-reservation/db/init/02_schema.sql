-- 회의실 테이블
CREATE TABLE IF NOT EXISTS rooms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL CHECK (capacity >= 1)
);

-- 예약 테이블
CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    room_id INT NOT NULL REFERENCES rooms(id),
    user_id INT NOT NULL,
    start_at timestamptz NOT NULL,
    end_at timestamptz NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    period tstzrange GENERATED ALWAYS AS (tstzrange(start_at, end_at, '[)')) STORED,
    CHECK (start_at < end_at),
    EXCLUDE USING gist (
        room_id WITH =,
        period WITH &&
    )
);

-- 조회 성능을 위한 보조 인덱스
CREATE INDEX IF NOT EXISTS idx_reservations_room_start ON reservations(room_id, start_at);


