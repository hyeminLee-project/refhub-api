# RefHub API

AI/개발 레퍼런스 수집 · 분류 · 검색 플랫폼의 백엔드 API 서버입니다.

arXiv 논문, GitHub 레포지토리, YouTube 영상 등 다양한 소스에서 AI/개발 관련 레퍼런스를 자동으로 수집하고, 태그 기반으로 분류하여 검색할 수 있는 플랫폼입니다.

## Architecture

```
┌─────────────┐     ┌──────────────────────────────────────────────┐
│   Client    │────▶│              Spring Boot API                 │
└─────────────┘     │                                              │
                    │  ┌──────────┐  ┌───────────┐  ┌───────────┐ │
                    │  │   Auth   │  │ Reference │  │Collection │ │
                    │  │Controller│  │Controller │  │Controller │ │
                    │  └────┬─────┘  └─────┬─────┘  └─────┬─────┘ │
                    │       │              │              │        │
                    │  ┌────┴─────┐  ┌─────┴─────┐  ┌────┴──────┐│
                    │  │  JWT /   │  │ Reference │  │Collection ││
                    │  │ Security │  │  Service  │  │  Service  ││
                    │  └──────────┘  └─────┬─────┘  └───────────┘│
                    │                      │                      │
                    └──────────────────────┼──────────────────────┘
                                           │
            ┌──────────────────────────────┼──────────────────┐
            │                              │                  │
     ┌──────┴──────┐              ┌────────┴────────┐  ┌──────┴──────┐
     │ PostgreSQL  │              │      Redis      │  │    Kafka    │
     │   (JPA)     │              │    (Cache)      │  │  (Events)   │
     └─────────────┘              └─────────────────┘  └──────┬──────┘
                                                              │
                    ┌─────────────────────────────────────────┤
                    │           Crawl Pipeline                 │
                    │                                         │
                    │  ┌─────────┐ ┌────────┐ ┌───────────┐  │
                    │  │  arXiv  │ │ GitHub │ │  YouTube  │  │
                    │  │ Crawler │ │Crawler │ │  Crawler  │  │
                    │  └─────────┘ └────────┘ └───────────┘  │
                    └─────────────────────────────────────────┘
```

## Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin 1.9, Java 21 |
| Framework | Spring Boot 3.3 |
| Database | PostgreSQL 16, Spring Data JPA |
| Cache | Redis 7, Spring Cache |
| Messaging | Apache Kafka, Spring Kafka |
| Auth | Spring Security, JWT (jjwt) |
| API Docs | SpringDoc OpenAPI (Swagger) |
| HTTP Client | WebFlux WebClient |
| Infra | Docker Compose |
| Test | JUnit 5, Mockito-Kotlin |

## Data Sources

| Source | What | How |
|---|---|---|
| **arXiv** | AI/ML/NLP 논문 메타데이터 | arXiv API (Atom XML) |
| **GitHub** | 트렌딩 AI 레포지토리 | GitHub REST API v3 |
| **YouTube** | AI 관련 영상 | YouTube Data API v3 |
| **Manual** | 블로그, 문서 등 | 사용자 직접 등록 |

## API Endpoints

### Auth
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/signup` | 회원가입 | - |
| POST | `/api/v1/auth/login` | 로그인 (JWT 발급) | - |

### References
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/references` | 레퍼런스 검색 (키워드/소스/태그) | - |
| GET | `/api/v1/references/{id}` | 레퍼런스 상세 | - |
| POST | `/api/v1/references` | 레퍼런스 수동 등록 | Bearer |

### Collections
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/collections` | 내 컬렉션 목록 | Bearer |
| GET | `/api/v1/collections/{id}` | 컬렉션 상세 | Bearer |
| POST | `/api/v1/collections` | 컬렉션 생성 | Bearer |
| POST | `/api/v1/collections/{id}/references/{refId}` | 컬렉션에 레퍼런스 추가 | Bearer |

### Crawl (Admin)
| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/crawl/trigger` | 수동 크롤 트리거 | ADMIN |

## Getting Started

### Prerequisites

- JDK 21
- Docker & Docker Compose

### Run

```bash
# 1. Start infrastructure
docker compose up -d

# 2. Build & run
./gradlew bootRun
```

API 문서: http://localhost:8080/swagger-ui.html

### Test

```bash
./gradlew test
```

## Crawl Pipeline

레퍼런스 수집은 Kafka 기반 비동기 파이프라인으로 동작합니다:

1. **Scheduler** (`@Scheduled`, 매일 06:00) 또는 Admin API로 크롤 트리거
2. **Crawler**가 외부 API에서 데이터 수집
3. **Kafka** (`refhub.crawl.result` 토픽)로 이벤트 발행
4. **Consumer**가 이벤트를 받아 중복 검사 후 DB에 저장
5. **Redis** 캐시 무효화

```
Trigger → Crawler → Kafka(crawl.result) → Consumer → DB + Cache Evict
```

## Project Structure

```
src/main/kotlin/com/refhub/api/
├── RefhubApplication.kt
├── config/                  # Redis, Kafka, Security, Swagger 설정
├── common/
│   ├── exception/           # 전역 예외 처리
│   └── response/            # API 응답 래퍼
├── domain/
│   ├── reference/           # 레퍼런스 (Entity, Repo, Service, Controller, DTO)
│   ├── collection/          # 컬렉션/북마크
│   ├── user/                # 사용자
│   └── tag/                 # 태그
├── security/                # JWT 인증/인가
├── event/                   # Kafka Producer/Consumer
└── crawler/                 # arXiv, GitHub, YouTube 크롤러 + 스케줄러
```
