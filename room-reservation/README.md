# Room Reservation

## 실행 방법 (루트에서)
1. Docker Desktop 실행
2. 리포지토리 루트에서 실행
```bash
docker compose up -d --build
```
3. 확인
- API: http://localhost:8080
- DB: localhost:5432 (DB/app_user/app_password)

## 실행 방법 (room-reservation 디렉터리에서)
```bash
cd room-reservation
docker compose up -d --build
```

## API 토큰
- ADMIN: `admin-token`
- USER: `user-token-<id>` 예) `user-token-1`

## 주요 엔드포인트
- 방 등록(ADMIN)
  - POST `/rooms`
  - Body: `{ "name": "A", "location": "10F", "capacity": 8 }`
  - Header: `Authorization: admin-token`
- 가용성 조회
  - GET `/rooms?date=YYYY-MM-DD`
- 예약 생성(USER)
  - POST `/reservations`
  - Body: `{ "roomId": 1, "startAt": "2025-09-17T09:00:00Z", "endAt": "2025-09-17T10:00:00Z" }`
  - Header: `Authorization: user-token-1`
- 예약 취소
  - DELETE `/reservations/{id}`

## 동시성/무결성
- PostgreSQL `tstzrange` + `EXCLUDE USING gist`로 같은 방(room_id)에서 겹치는 시간대 예약 차단
- 반개구간 `[start_at, end_at)` 사용

## 개발 참고
- 환경변수로 DB 접속정보 오버라이드 가능
- 스키마는 `db/init`의 SQL로 초기화됨(최초 볼륨 생성 시)


