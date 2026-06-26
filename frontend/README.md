# 서울 버스 관제 프론트엔드

서울 버스 관제 시스템 MVP의 프론트엔드 애플리케이션이다.

## 기술 스택

- React
- TypeScript
- Vite
- Leaflet
- React Leaflet

## 개발 환경

- Node.js 24.18.0
- npm 11.16.0

`nvm`을 사용한다면 다음 명령으로 버전을 맞춘다.

```bash
nvm use
```

## 실행 방법

```bash
npm ci
npm run dev
```

기본 개발 서버 주소는 `http://127.0.0.1:5173`이다.

## 환경 변수

`.env.example`을 참고해 필요 시 로컬 환경 변수를 설정한다.

```text
VITE_API_BASE_URL=/api/v1
VITE_API_PROXY_TARGET=http://localhost:8080
```

개발 환경에서는 Vite dev server가 `/api` 요청을 백엔드 `8080` 포트로 프록시한다.

## 검증 명령

```bash
npm run lint
npm run build
```
