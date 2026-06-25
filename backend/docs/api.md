# 프론트엔드 API 문서

이 문서는 프론트엔드가 서울 버스 관제 대시보드를 구현할 때 사용할 백엔드 REST API 계약을 정리한다.

## 기본 규칙

- Base URL: `http://localhost:8080`
- API prefix: `/api/v1`
- Content-Type: `application/json`
- 인증: MVP 범위에서는 없음
- 날짜/시간: ISO-8601 문자열
  - 예: `2026-06-25T19:31:11.123456`
  - 현재 백엔드는 `LocalDateTime`을 사용하므로 timezone offset은 포함하지 않는다.
- 좌표/속도: JSON number
- 통신 상태는 서버가 마지막 통신시간 기준으로 계산한다.
  - 5분 이하: `ONLINE`
  - 5분 초과: `OFFLINE`

## Enum

### BusCommunicationStatus

```text
ONLINE
OFFLINE
```

### RouteDirectionType

```text
OUTBOUND
INBOUND
```

### CameraType

```text
FRONT
REAR
INTERIOR_1
INTERIOR_2
```

### EventType

```text
SUDDEN_STOP
SUDDEN_ACCELERATION
SUDDEN_DECELERATION
IMPACT
```

### EventSeverity

```text
LOW
MEDIUM
HIGH
```

## API 요약

| Method | Path | 용도 |
| --- | --- | --- |
| `GET` | `/api/v1/buses` | 버스 목록 조회 |
| `GET` | `/api/v1/buses/{busId}` | 버스 상세 조회 |
| `GET` | `/api/v1/buses/positions/latest` | 지도 표시용 전체 버스 최신 위치 조회 |
| `GET` | `/api/v1/buses/{busId}/positions/latest` | 특정 버스 최신 위치 조회 |
| `GET` | `/api/v1/events/recent?limit=20` | 최근 이벤트 조회 |

## GET /api/v1/buses

버스 목록 화면에 필요한 요약 정보를 조회한다.

### Response 200

```json
[
  {
    "id": 3,
    "busNumber": "143-01",
    "routeNumber": "143",
    "routeName": "정릉 ↔ 개포동",
    "directionType": "OUTBOUND",
    "directionName": "정릉 -> 개포동",
    "currentSpeed": 42.30,
    "communicationStatus": "ONLINE",
    "lastCommunicationAt": "2026-06-25T19:29:11.123456",
    "latitude": 37.5703770,
    "longitude": 126.9918950,
    "positionRecordedAt": "2026-06-25T19:29:11.123456"
  }
]
```

### Field

| Field | Type | Nullable | 설명 |
| --- | --- | --- | --- |
| `id` | number | no | 버스 ID |
| `busNumber` | string | no | 관제용 버스번호 |
| `routeNumber` | string | no | 노선번호 |
| `routeName` | string | no | 노선명 |
| `directionType` | `RouteDirectionType` | no | 운행 방향 구분 |
| `directionName` | string | no | 화면 표시용 방향명 |
| `currentSpeed` | number | no | 최신 위치 기준 현재속도. 위치가 없으면 `0` |
| `communicationStatus` | `BusCommunicationStatus` | no | 마지막 통신시간 기준 계산 상태 |
| `lastCommunicationAt` | string | no | 마지막 통신시간 |
| `latitude` | number | yes | 최신 위도 |
| `longitude` | number | yes | 최신 경도 |
| `positionRecordedAt` | string | yes | 최신 위치 수집 시각 |

## GET /api/v1/buses/{busId}

버스 상세 패널에 필요한 정보를 조회한다.

### Path Variable

| Name | Type | 설명 |
| --- | --- | --- |
| `busId` | number | 버스 ID |

### Response 200

```json
{
  "id": 1,
  "busNumber": "7016-01",
  "plateNumber": "서울70사1234",
  "routeNumber": "7016",
  "routeName": "은평공영차고지 ↔ 상명대",
  "directionType": "OUTBOUND",
  "directionName": "은평공영차고지 -> 상명대",
  "originName": "은평공영차고지",
  "destinationName": "상명대",
  "currentSpeed": 31.50,
  "communicationStatus": "ONLINE",
  "lastCommunicationAt": "2026-06-25T19:29:11.123456",
  "currentStopName": "서울역버스환승센터",
  "nextStopName": "시청앞",
  "latitude": 37.5559460,
  "longitude": 126.9723170,
  "positionRecordedAt": "2026-06-25T19:29:11.123456",
  "cameraStatuses": [
    {
      "cameraType": "FRONT",
      "receiving": true,
      "streamUrl": "mock://bus/7016-01/front",
      "lastReceivedAt": "2026-06-25T19:29:11.123456"
    }
  ]
}
```

### Field

| Field | Type | Nullable | 설명 |
| --- | --- | --- | --- |
| `id` | number | no | 버스 ID |
| `busNumber` | string | no | 관제용 버스번호 |
| `plateNumber` | string | no | 차량 등록번호 |
| `routeNumber` | string | no | 노선번호 |
| `routeName` | string | no | 노선명 |
| `directionType` | `RouteDirectionType` | no | 운행 방향 구분 |
| `directionName` | string | no | 화면 표시용 방향명 |
| `originName` | string | no | 해당 방향의 기점 |
| `destinationName` | string | no | 해당 방향의 종점 |
| `currentSpeed` | number | no | 최신 위치 기준 현재속도. 위치가 없으면 `0` |
| `communicationStatus` | `BusCommunicationStatus` | no | 마지막 통신시간 기준 계산 상태 |
| `lastCommunicationAt` | string | no | 마지막 통신시간 |
| `currentStopName` | string | yes | 현재 정류장명 |
| `nextStopName` | string | yes | 다음 정류장명 |
| `latitude` | number | yes | 최신 위도 |
| `longitude` | number | yes | 최신 경도 |
| `positionRecordedAt` | string | yes | 최신 위치 수집 시각 |
| `cameraStatuses` | `CameraStatusResponse[]` | no | 카메라 수신 상태 목록 |

### CameraStatusResponse

| Field | Type | Nullable | 설명 |
| --- | --- | --- | --- |
| `cameraType` | `CameraType` | no | 카메라 구분 |
| `receiving` | boolean | no | 영상 수신 여부 |
| `streamUrl` | string | yes | 영상 스트림 URL. MVP에서는 mock 값 |
| `lastReceivedAt` | string | yes | 마지막 영상 수신 시각 |

### Response 404

존재하지 않는 버스 ID를 조회하면 `404 Not Found`를 반환한다.

```json
{
  "message": "Bus not found: id=999",
  "timestamp": "2026-06-25T19:31:11.123456"
}
```

## GET /api/v1/buses/positions/latest

지도에 전체 운행 버스 마커를 표시하기 위한 최신 위치 목록을 조회한다.

### Response 200

```json
[
  {
    "busId": 1,
    "busNumber": "7016-01",
    "routeNumber": "7016",
    "routeName": "은평공영차고지 ↔ 상명대",
    "directionType": "OUTBOUND",
    "directionName": "은평공영차고지 -> 상명대",
    "latitude": 37.5559460,
    "longitude": 126.9723170,
    "speed": 31.50,
    "communicationStatus": "ONLINE",
    "recordedAt": "2026-06-25T19:29:11.123456"
  }
]
```

### Field

| Field | Type | Nullable | 설명 |
| --- | --- | --- | --- |
| `busId` | number | no | 버스 ID |
| `busNumber` | string | no | 관제용 버스번호 |
| `routeNumber` | string | no | 노선번호 |
| `routeName` | string | no | 노선명 |
| `directionType` | `RouteDirectionType` | no | 운행 방향 구분 |
| `directionName` | string | no | 화면 표시용 방향명 |
| `latitude` | number | yes | 최신 위도 |
| `longitude` | number | yes | 최신 경도 |
| `speed` | number | no | 최신 위치 기준 속도. 위치가 없으면 `0` |
| `communicationStatus` | `BusCommunicationStatus` | no | 마지막 통신시간 기준 계산 상태 |
| `recordedAt` | string | yes | 최신 위치 수집 시각 |

## GET /api/v1/buses/{busId}/positions/latest

특정 버스의 최신 위치를 조회한다. 상세 패널에서 선택한 버스만 갱신할 때 사용할 수 있다.

### Path Variable

| Name | Type | 설명 |
| --- | --- | --- |
| `busId` | number | 버스 ID |

### Response 200

응답 스키마는 `GET /api/v1/buses/positions/latest`의 단일 객체와 같다.

```json
{
  "busId": 1,
  "busNumber": "7016-01",
  "routeNumber": "7016",
  "routeName": "은평공영차고지 ↔ 상명대",
  "directionType": "OUTBOUND",
  "directionName": "은평공영차고지 -> 상명대",
  "latitude": 37.5559460,
  "longitude": 126.9723170,
  "speed": 31.50,
  "communicationStatus": "ONLINE",
  "recordedAt": "2026-06-25T19:29:11.123456"
}
```

### Response 404

```json
{
  "message": "Bus not found: id=999",
  "timestamp": "2026-06-25T19:31:11.123456"
}
```

## GET /api/v1/events/recent

최근 이벤트 목록을 발생 시각 내림차순으로 조회한다.

### Query Parameter

| Name | Type | Required | Default | 설명 |
| --- | --- | --- | --- | --- |
| `limit` | number | no | `20` | 조회 개수. 서버에서 최소 `1`, 최대 `100`으로 보정한다. |

### Response 200

```json
[
  {
    "id": 1,
    "busId": 3,
    "busNumber": "143-01",
    "routeNumber": "143",
    "routeName": "정릉 ↔ 개포동",
    "eventType": "SUDDEN_ACCELERATION",
    "severity": "MEDIUM",
    "description": "강남 방향 주행 중 급가속 이벤트가 감지되었습니다.",
    "latitude": 37.5703770,
    "longitude": 126.9918950,
    "occurredAt": "2026-06-25T19:28:11.123456"
  }
]
```

### Field

| Field | Type | Nullable | 설명 |
| --- | --- | --- | --- |
| `id` | number | no | 이벤트 ID |
| `busId` | number | no | 이벤트 발생 버스 ID |
| `busNumber` | string | no | 이벤트 발생 버스번호 |
| `routeNumber` | string | no | 노선번호 |
| `routeName` | string | no | 노선명 |
| `eventType` | `EventType` | no | 이벤트 유형 |
| `severity` | `EventSeverity` | no | 이벤트 심각도 |
| `description` | string | no | 이벤트 설명 |
| `latitude` | number | no | 이벤트 발생 위도 |
| `longitude` | number | no | 이벤트 발생 경도 |
| `occurredAt` | string | no | 이벤트 발생 시각 |

## 프론트엔드 사용 가이드

### 대시보드 초기 로딩

대시보드 첫 진입 시 다음 API를 병렬 호출하면 된다.

```text
GET /api/v1/buses
GET /api/v1/buses/positions/latest
GET /api/v1/events/recent?limit=20
```

### 버스 선택

목록 또는 지도 마커에서 버스를 선택하면 다음 API를 호출한다.

```text
GET /api/v1/buses/{busId}
GET /api/v1/buses/{busId}/positions/latest
```

상세 API에도 최신 위치가 포함되어 있으므로, 상세 패널만 갱신할 때는 `GET /api/v1/buses/{busId}` 하나만 호출해도 된다. 지도 마커 좌표만 빠르게 갱신해야 할 때는 단건 위치 API를 사용한다.

### Polling 권장

MVP에서는 WebSocket 대신 polling으로 실시간성을 표현한다.

권장 주기:

- 버스 목록: 5초
- 전체 위치: 5초
- 최근 이벤트: 10초
- 선택 버스 상세: 5초 또는 선택 변경 시 즉시 호출

### TypeScript 타입 예시

```ts
export type BusCommunicationStatus = 'ONLINE' | 'OFFLINE';
export type RouteDirectionType = 'OUTBOUND' | 'INBOUND';
export type CameraType = 'FRONT' | 'REAR' | 'INTERIOR_1' | 'INTERIOR_2';
export type EventType = 'SUDDEN_STOP' | 'SUDDEN_ACCELERATION' | 'SUDDEN_DECELERATION' | 'IMPACT';
export type EventSeverity = 'LOW' | 'MEDIUM' | 'HIGH';

export interface BusSummaryResponse {
  id: number;
  busNumber: string;
  routeNumber: string;
  routeName: string;
  directionType: RouteDirectionType;
  directionName: string;
  currentSpeed: number;
  communicationStatus: BusCommunicationStatus;
  lastCommunicationAt: string;
  latitude: number | null;
  longitude: number | null;
  positionRecordedAt: string | null;
}

export interface BusDetailResponse extends BusSummaryResponse {
  plateNumber: string;
  originName: string;
  destinationName: string;
  currentStopName: string | null;
  nextStopName: string | null;
  cameraStatuses: CameraStatusResponse[];
}

export interface CameraStatusResponse {
  cameraType: CameraType;
  receiving: boolean;
  streamUrl: string | null;
  lastReceivedAt: string | null;
}

export interface BusPositionResponse {
  busId: number;
  busNumber: string;
  routeNumber: string;
  routeName: string;
  directionType: RouteDirectionType;
  directionName: string;
  latitude: number | null;
  longitude: number | null;
  speed: number;
  communicationStatus: BusCommunicationStatus;
  recordedAt: string | null;
}

export interface EventResponse {
  id: number;
  busId: number;
  busNumber: string;
  routeNumber: string;
  routeName: string;
  eventType: EventType;
  severity: EventSeverity;
  description: string;
  latitude: number;
  longitude: number;
  occurredAt: string;
}

export interface ErrorResponse {
  message: string;
  timestamp: string;
}
```
