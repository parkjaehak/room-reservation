# 회의실 예약 API

## 실행 방법
1. Docker Desktop 설치 및 실행 혹은 Docker Engine 실행
2. Docker Compose 명령어 실행
```bash
docker compose up --build
```
3. API 문서 확인 및 테스트
- http://localhost:8080/swagger-ui/index.html



## 동시성 테스트 재현

### 목표
같은 시간대에 10개의 병렬 예약 요청을 보낼 때 정확히 1건만 성공한다.


### 테스트 실행 방법
1. Docker Desktop 실행 (TestContainers 사용)
2. 테스트 실행 (PowerShell)
```powershell
.\gradlew test --tests "ReservationConcurrencyTest" 
```

### 테스트 시나리오
1. 테스트용 회의실1 생성
2. 동일한 시간대에 10개의 병렬 예약 요청 생성
3. 각 요청은 서로 다른 사용자 (`user-token-1` ~ `user-token-10`)로 시뮬레이션
4. 모든 요청이 완료될 때까지 대기
5. 성공/실패 개수 및 데이터베이스 상태 검증


### 테스트 검증 내용
- **성공 기준**: 10개의 병렬 요청 중 정확히 1건만 성공 (HTTP 201)
- **실패 기준**: 나머지 9건은 실패 (HTTP 400, "해당 시간대에 이미 예약이 있습니다.")
- **데이터 검증**: 데이터베이스에 저장된 예약이 정확히 1건인지 확인



## LLM 사용구간