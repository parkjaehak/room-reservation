# 회의실 예약 API


## 1. 실행 방법
1. Docker Desktop 설치 및 실행 혹은 Docker Engine 실행
2. Docker Compose 명령어 실행
```bash
docker compose up --build
```
3. API 문서 확인 및 테스트
   http://localhost:8080/swagger-ui/index.html



## 2. 동시성 테스트 재현

### 1) 목표
같은 시간대에 10개의 병렬 예약 요청을 보낼 때 정확히 1건만 성공한다.


### 2) 테스트 실행 방법
1. Docker Desktop 실행 (TestContainers 사용)
2. 테스트 실행 (PowerShell)
```powershell
.\gradlew test --tests "ReservationConcurrencyTest" 
```

### 3) 테스트 시나리오
1. 테스트용 회의실1 생성
2. 동일한 시간대에 10개의 병렬 예약 요청 생성
3. 각 요청은 서로 다른 사용자 (`user-token-1` ~ `user-token-10`)로 시뮬레이션
4. 모든 요청이 완료될 때까지 대기
5. 성공/실패 개수 및 데이터베이스 상태 검증


### 4) 테스트 검증 내용
- **성공 케이스**: 10개의 병렬 요청 중 정확히 1건만 성공 (HTTP 201)
- **실패 케이스**: 나머지 9건은 실패 (HTTP 400, "해당 시간대에 이미 예약이 있습니다.")
- **데이터 검증**: 데이터베이스에 저장된 예약이 정확히 1건인지 확인




### 주요 질문 및 프롬프트

1. **TestContainers 설정**
   - PostgreSQL 컨테이너 기반 테스트 환경 구성
   - 동적 프로퍼티 설정 및 데이터베이스 연결

2. **간소화된 RBAC 구현**
   - ThreadLocal 방식으로 사용자 컨텍스트 관리하는 방법
   - 토큰 기반 인증과 권한 검증 로직

3. **데이터베이스 스키마 설계**
   - 시간 경계 구현을 위한 DB 스키마 작성 방법
   - EXCLUDE 제약조건과 tstzrange 활용

4. **OpenAPI Swagger UI 설정**
   - Security Scheme의 역할과 설정 방법
   - API 문서화 및 인증 정보 표시

5. **동시성 테스트 구현**
   - 멀티스레드 환경에서 동시 요청 테스트 방법
   - AtomicInteger와 ExecutorService 활용