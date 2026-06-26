# g-i-service-recruitment-task

서울 버스 관제 시스템 MVP 구현 과제입니다.

## 프로젝트 개요

관제 담당자가 웹 화면에서 운행 중인 버스의 상태를 확인할 수 있는 MVP입니다.

구현 범위는 다음과 같습니다.

- 버스 목록 조회
- 버스 상세 조회
- 지도 기반 버스 위치 표시
- 최근 이벤트 조회
- 마지막 통신시간 기준 `ONLINE` / `OFFLINE` 상태 계산
- Mock Data 기반 시연 데이터 제공
- Polling 기반 화면 갱신
- Docker Compose 기반 전체 실행 환경

## 기술 스택

### Backend

- Java 21
- Spring Boot 3.5.15
- Spring Web
- Spring Data JPA
- MySQL 8.4
- Flyway
- Maven

### Frontend

- React
- TypeScript
- Vite
- Leaflet
- React Leaflet
- Nginx

### Infra

- Docker
- Docker Compose

## 기술 선택 이유

- `Java`: Spring 기반 백엔드 개발에서 가장 널리 쓰이는 언어라 협업과 유지보수 관점에서 유리합니다.
- `Spring Boot 3.5`: 현재 안정적으로 사용할 수 있는 Spring Boot 3.x 라인으로, Java 21과 최신 Spring 생태계를 활용할 수 있습니다.
- `MySQL`: 버스, 노선, 정류장, 위치, 이벤트처럼 관계가 명확한 데이터를 다루기 적합하고 운영 경험이 많은 RDBMS입니다.
- `Flyway`: DB 스키마와 Mock Data를 버전 관리해 실행 환경마다 같은 초기 상태를 재현할 수 있습니다.
- `React + TypeScript`: 관제 대시보드처럼 상태 변화가 있는 화면을 컴포넌트 단위로 구성하기 쉽고 API 응답 타입을 명확히 다룰 수 있습니다.
- `Vite`: 개발 서버와 빌드 구성이 단순해 MVP 구현에 적합합니다.
- `Leaflet`: 별도 유료 지도 API 키 없이 버스 위치를 지도에 표시할 수 있습니다.
- `Polling`: MVP에서는 WebSocket 인프라 없이도 주기적인 상태 갱신 요구사항을 충족할 수 있습니다.
- `Docker Compose`: 프론트엔드, 백엔드, MySQL을 한 번에 실행해 리뷰어가 같은 환경에서 확인할 수 있습니다.

## Docker 실행

전체 애플리케이션은 루트 `compose.yaml`로 실행합니다.

```bash
docker compose up --build
```

기본 접속 주소는 다음과 같습니다.

- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080/api/v1/buses`
- MySQL: `localhost:3306`

프론트엔드 컨테이너는 Nginx로 정적 파일을 서빙하고, `/api` 요청을 백엔드 컨테이너로 프록시합니다.

중지하려면 다음 명령을 사용합니다.

```bash
docker compose down
```

DB 볼륨까지 제거하려면 다음 명령을 사용합니다.

```bash
docker compose down -v
```

## 로컬 개발 실행

백엔드는 MySQL이 실행 중인 상태에서 다음 명령으로 실행할 수 있습니다.

```bash
cd backend
mvn spring-boot:run
```

프론트엔드는 개발 서버를 실행하면 `/api` 요청을 백엔드 `8080` 포트로 프록시합니다.

```bash
cd frontend
npm install
npm run dev
```

프론트엔드 개발 서버 주소는 `http://127.0.0.1:5173`입니다.

## 검증 방법

백엔드 테스트:

```bash
cd backend
mvn test
```

프론트엔드 정적 검사와 빌드:

```bash
cd frontend
npm run lint
npm run build
```

전체 Docker 실행 확인:

```bash
docker compose up --build
```

## 주요 API

| Method | Path | 용도 |
| --- | --- | --- |
| `GET` | `/api/v1/buses` | 버스 목록 조회 |
| `GET` | `/api/v1/buses/{busId}` | 버스 상세 조회 |
| `GET` | `/api/v1/buses/positions/latest` | 전체 버스 최신 위치 조회 |
| `GET` | `/api/v1/buses/{busId}/positions/latest` | 특정 버스 최신 위치 조회 |
| `GET` | `/api/v1/events/recent` | 최근 이벤트 조회 |

## 문서

- [백엔드 요구사항](backend/docs/requirements.md)
- [도메인 모델](backend/docs/domain-model.md)
- [API 문서](backend/docs/api.md)
- [용어 사전](backend/docs/glossary.md)
- [제출용 설계 문서](docs/design.md)
- [설계 질문 답변서](docs/design-answers.md)
