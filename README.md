# newsFlow-BE-API

> NewsFlow 플랫폼의 메인 백엔드 API 서버 레포지토리입니다.  
> Spring Boot 기반으로 기사 집계·캐싱·트래픽 처리 등 핵심 비즈니스 로직을 담당하며,  
> AI 서버(newsFlow-BE-AI)와 협력하여 맞춤 추천 결과를 프론트엔드에 제공합니다.

---

## 📌 사용 목적

- 프론트엔드(newsFlow-FE)에 필요한 모든 REST API 엔드포인트 제공
- 기사 데이터의 조회 및 산업군(카테고리) 필터링 처리
- **날짜별·월별·연별 기사 집계** 및 달력 뷰용 데이터 응답
- Redis 기반 실시간 조회수 카운팅 및 인기 기사 랭킹 집계
- 대용량 트래픽 처리: HikariCP 커넥션 풀 + 비동기 처리 + 캐싱 레이어
- 사용자 인증(회원가입/로그인) 및 개인화 데이터 관리
- AI 서버(newsFlow-BE-AI)로 추천 요청을 위임하고 결과를 중계

---

## 🛠 기술 스택

| 분류 | 기술 | 용도 |
|------|------|------|
| **Framework** | Spring Boot 3.x | 메인 서버 프레임워크 |
| **Language** | Java 17 | 안정적인 서버 개발 |
| **ORM** | Spring Data JPA + Hibernate | DB 엔티티 관리 및 기본 쿼리 |
| **동적 쿼리** | QueryDSL | 카테고리 필터·날짜 집계 복합 쿼리 |
| **Database** | PostgreSQL | 기사·사용자 데이터 영구 저장 |
| **Cache** | Redis (Spring Data Redis) | 인기 기사 Sorted Set, 조회수 누적 캐싱 |
| **인증** | Spring Security + JWT | 사용자 인증 및 권한 관리 |
| **비동기 처리** | `@Async` + `CompletableFuture` | 조회수 증가, AI 서버 요청 논블로킹 처리 |
| **커넥션 풀** | HikariCP | 대용량 DB 커넥션 안정 관리 |
| **HTTP 클라이언트** | WebClient (Spring WebFlux) | AI 서버 비동기 통신 |
| **API 문서화** | SpringDoc OpenAPI (Swagger) | API 명세 자동화 |
| **유효성 검사** | Bean Validation (`@Valid`) | 요청 데이터 검증 |
| **로깅** | Logback + SLF4J | 구조화 로그 관리 |
| **빌드 도구** | Gradle | 의존성 관리 및 빌드 |
| **테스트** | JUnit 5 + Mockito | 단위·통합 테스트 |
| **컨테이너** | Docker + docker-compose | 환경 통일 및 배포 |

---

## 📁 주요 디렉토리 구조

```
newsFlow-BE-API/
├── src/
│   └── main/
│       ├── java/com/newsflow/
│       │   ├── article/            # 기사 조회·필터링 API
│       │   ├── calendar/           # 날짜별·월별·연별 기사 집계 모듈
│       │   ├── trending/           # 실시간 인기·조회수 기사 (Redis)
│       │   ├── category/           # 산업군 카테고리 관리
│       │   ├── user/               # 사용자 관리 (회원가입, 프로필)
│       │   ├── auth/               # Spring Security, JWT 인증
│       │   ├── recommend/          # AI 서버 요청 위임 및 결과 중계
│       │   └── common/             # 공통 예외처리, 인터셉터, 응답 포맷
│       └── resources/
│           ├── application.yml
│           └── application-prod.yml
├── src/test/                       # JUnit 5 테스트
├── build.gradle
├── docker-compose.yml
└── .env.example
```

---

## ✅ 핵심 기능 목록

- [ ] 기사 목록 조회 API (카테고리 필터, 날짜 필터, Cursor 페이지네이션)
- [ ] **날짜별 기사 집계 API** — 달력 뷰 지원
- [ ] **월별·연별 주요 기사 도출 API**
- [ ] 실시간 인기 기사 API (Redis Sorted Set 기반)
- [ ] 조회수 높은 기사 API (Redis incr → 배치 DB 반영)
- [ ] 사용자 회원가입 / 로그인 / JWT 갱신
- [ ] 사용자 관심 카테고리 설정 API
- [ ] AI 서버(newsFlow-BE-AI) 추천 요청 위임 및 결과 중계 (WebClient 비동기)

---

## ⚡ 대용량 처리 전략

```
# 조회수 처리 흐름
기사 조회 요청
  → @Async로 Redis incr (DB 직접 타격 X)
  → 스케줄러가 주기적으로 Redis → PostgreSQL 배치 반영

# 인기 기사 흐름
Redis Sorted Set (ZADD / ZRANGE)
  → 점수 기반 실시간 랭킹 유지
  → TTL 설정으로 오래된 데이터 자동 만료

# 캐시 레이어
요청 → Redis 캐시 확인
       ├─ Hit  → 즉시 반환
       └─ Miss → DB 조회 → Redis 저장 → 반환
```

---

## 🔗 연관 레포지토리

| 레포지토리 | 역할 |
|------------|------|
| `newsFlow-FE` | API 소비자 (프론트엔드 클라이언트) |
| `newsFlow-BE-AI` | 맞춤 추천·핵심 기사 도출 결과 제공 |
| `newsFlow-BE-DATA` | 수집·정제된 원천 기사 데이터 공급 |
