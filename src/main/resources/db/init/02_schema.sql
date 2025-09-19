-- 회의실 테이블 - 이름/위치/수용인원 등록
CREATE TABLE IF NOT EXISTS rooms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL CHECK (capacity >= 1)
);

-- 예약 테이블 - 동시 예약 방지 및 시간 겹침 방지
CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    room_id INT NOT NULL REFERENCES rooms(id),
    user_id INT NOT NULL,
    start_at timestamptz NOT NULL,
    end_at timestamptz NOT NULL,
    -- 시간 경계는 [start, end)
    period tstzrange GENERATED ALWAYS AS (tstzrange(start_at, end_at, '[)')) STORED,
    -- 유효성 검사 - startAt < endAt
    CHECK (start_at < end_at),
    -- 동시 예약 방지
    EXCLUDE USING gist (
        room_id WITH =,
        period WITH &&
    )
);

