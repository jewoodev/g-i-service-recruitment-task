# g-i-service-recruitment-task

서울 버스 관제 시스템 MVP 구현 과제입니다.

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
