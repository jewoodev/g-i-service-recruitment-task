# 서울 버스 관제 도메인

- 관제 담당자는 현재 운행 중인 버스의 목록, 상세 상태, 지도상 위치, 최근 이벤트를 확인할 수 있다.
- 버스는 하나의 노선 방향 패턴 위에서 운행한다.
  - 노선은 노선번호와 노선명으로 식별되는 운행 노선 자체를 의미한다.
  - 노선 방향은 하나의 노선 안에서 상행/하행 또는 기점/종점 방향으로 구분되는 운행 패턴이다.
  - 정류장 순서는 노선 자체가 아니라 노선 방향별로 관리한다.
  - 같은 정류장 집합이라도 방향에 따라 정류장 순서가 달라질 수 있다.
- 버스의 통신 상태는 마지막 통신시간을 기준으로 계산한다.
  - 마지막 통신시간이 현재 시각 기준 5분 이하이면 `ONLINE`이다.
  - 마지막 통신시간이 현재 시각 기준 5분 초과이면 `OFFLINE`이다.
  - 현재상태는 저장하지 않고 조회 시점에 계산한다.
- 버스 위치는 GPS 위치 이력으로 저장한다.
  - 관제 화면의 지도는 각 버스의 최신 위치를 사용한다.
  - 현재속도는 최신 위치의 속도 값을 사용한다.
- 버스 이벤트는 급정거, 급가속, 급감속, 충격 등 관제 담당자가 확인해야 하는 운행 이벤트다.
  - 최근 이벤트 목록은 발생시간 기준 최신순으로 조회한다.
- 버스 영상은 MVP에서 실제 스트리밍을 구현하지 않고 카메라 수신 상태만 모델링한다.
  - 전면 블랙박스, 후면 블랙박스, 내부 CCTV 1, 내부 CCTV 2를 구분한다.
  - 영상 원본 저장과 실시간 전송은 향후 확장 영역으로 둔다.

# 서울 버스 관제 도메인 모델

---

## [노선 애그리거트]

## 노선(Route)
_Entity_
### 속성
- `id`: `Long`
- `routeNumber`: 노선번호. 승객과 관제자가 인식하는 공개 업무 식별자
- `name`: 노선명. 예: `은평공영차고지 ↔ 상명대`
- `directions`: `RouteDirection` 1:N
### 행위
- `getDirection()`: 지정한 방향 구분값에 해당하는 노선 방향을 찾는다.
- `addDirection()`: 노선에 방향별 운행 패턴을 추가한다.
### 규칙
- 노선번호는 시스템 안에서 중복될 수 없다.
- 노선번호는 숫자뿐 아니라 영문 접두사/접미사를 포함할 수 있으므로 문자열로 관리한다.
- 노선은 방향을 직접 갖지 않는다. 방향별 운행 패턴은 `RouteDirection`으로 분리한다.

## 노선 방향(RouteDirection)
_Entity_
### 속성
- `id`: `Long`
- `routeId`: `Long`
- `directionType`: `RouteDirectionType` 노선 방향 구분
- `originName`: 기점 이름
- `destinationName`: 종점 이름
- `routeStops`: `RouteStop` 1:N
### 행위
- `displayName()`: 사람이 읽을 수 있는 방향명 생성. 예: `은평공영차고지 → 상명대`
- `getOrderedStops()`: 정류장 순서 기준으로 정렬된 정류장 목록 조회
- `findNextStop()`: 현재 정류장 기준 다음 정류장 조회
### 규칙
- 하나의 노선 안에서 같은 `directionType`은 중복될 수 없다.
- `originName`과 `destinationName`은 해당 방향 패턴의 시작과 끝을 사람이 이해할 수 있게 표현한다.
- 정류장 순서는 노선 방향별로 관리한다.
- 버스는 `directionType` 문자열을 직접 저장하지 않고 현재 운행 중인 `RouteDirection`을 참조한다.

## 노선 방향 구분(RouteDirectionType)
_Enum_
### 상수
- `OUTBOUND`: 기점에서 종점으로 향하는 방향
- `INBOUND`: 종점에서 기점으로 향하는 방향

## 노선 정류장(RouteStop)
_Entity_
### 속성
- `id`: `Long`
- `routeDirectionId`: `Long`
- `stop`: `Stop` N:1
- `stopOrder`: 방향 내 정류장 순서
### 행위
- `isBefore()`: 같은 노선 방향 안에서 다른 정류장보다 앞서는지 확인한다.
- `isAfter()`: 같은 노선 방향 안에서 다른 정류장보다 뒤에 있는지 확인한다.
### 규칙
- 같은 노선 방향 안에서 `stopOrder`는 중복될 수 없다.
- 같은 노선 방향 안에서 같은 정류장은 중복될 수 없다.
- 상행과 하행은 같은 정류장을 공유하더라도 서로 다른 `RouteStop` 순서를 가질 수 있다.

---

## [정류장 애그리거트]

## 정류장(Stop)
_Entity_
### 속성
- `id`: `Long`
- `stopNumber`: 정류장번호. 외부에서 인식하는 정류장 식별자
- `name`: 정류장명
- `coordinate`: `Coordinate` 정류장 위치 좌표
### 행위
- `position()`: 정류장의 지도 표시 위치 반환
### 규칙
- 정류장번호는 시스템 안에서 중복될 수 없다.
- 위치 좌표는 지도 표시와 위치 비교를 위해 필수다.
- 정류장은 여러 노선 방향에 포함될 수 있다.

---

## [공유 값 객체]

## 위치 좌표(Coordinate)
_Value Object_
### 속성
- `latitude`: 위도
- `longitude`: 경도
### 규칙
- 위도와 경도는 부동소수점 오차를 줄이기 위해 `BigDecimal`로 다룬다.
- 위도는 -90 이상 90 이하, 경도는 -180 이상 180 이하 범위를 가져야 한다.
- 정류장 위치, 버스 위치, 이벤트 발생 위치에서 공통으로 사용한다.

---

## [버스 애그리거트]

## 버스(Bus)
_Entity_
### 속성
- `id`: `Long`
- `busNumber`: 버스번호. 관제 시스템 안에서 운행 차량을 구분하는 번호
- `plateNumber`: 차량번호. 실제 차량 등록번호
- `routeDirection`: `RouteDirection` 현재 운행 중인 노선 방향 패턴
- `currentStop`: `Stop` 현재 정류장
- `nextStop`: `Stop` 다음 정류장
- `lastCommunicationAt`: 마지막 통신시간
- `latestPosition`: `BusPosition`
- `cameraStatuses`: `BusCameraStatus` 1:N
### 행위
- `communicationStatusAt()`: 기준 시각을 받아 `BusCommunicationStatus`를 계산한다.
- `currentSpeed()`: 최신 위치 기준 현재속도를 반환한다.
- `changeRouteDirection()`: 운행 중인 노선 방향 패턴을 변경한다.
- `updateStops()`: 현재 정류장과 다음 정류장을 갱신한다.
- `recordCommunication()`: 마지막 통신시간을 갱신한다.
### 규칙
- 버스번호는 시스템 안에서 중복될 수 없다.
- 차량번호는 시스템 안에서 중복될 수 없다.
- 버스의 통신 상태는 저장하지 않고 마지막 통신시간 기준으로 계산한다.
- 버스는 반드시 하나의 `RouteDirection` 위에서 운행한다.
- 현재 정류장과 다음 정류장은 버스가 참조하는 `RouteDirection`의 정류장 순서 안에 있어야 한다.
- 다음 정류장은 현재 정류장보다 뒤의 순서를 가져야 한다.

## 버스 통신 상태(BusCommunicationStatus)
_Enum_
### 상수
- `ONLINE`: 마지막 통신시간이 현재 시각 기준 5분 이하
- `OFFLINE`: 마지막 통신시간이 현재 시각 기준 5분 초과

## 버스 위치(BusPosition)
_Entity_
### 속성
- `id`: `Long`
- `busId`: `Long`
- `coordinate`: `Coordinate` 위치 좌표
- `speed`: 속도
- `recordedAt`: 위치 기록 시각
### 행위
- `position()`: 지도 표시용 위치 좌표 반환
### 규칙
- 위치는 버스별 이력으로 저장한다.
- 지도와 목록에서 사용하는 현재 위치는 `recordedAt`이 가장 최신인 위치다.
- 속도는 음수가 될 수 없다.

## 버스 카메라 상태(BusCameraStatus)
_Entity_
### 속성
- `id`: `Long`
- `busId`: `Long`
- `cameraType`: `CameraType`
- `receiving`: 영상 수신 여부
- `streamUrl`: 영상 스트림 URL
- `lastReceivedAt`: 마지막 영상 수신시간
### 행위
- `markReceiving()`: 영상 수신 상태로 변경하고 마지막 수신시간을 갱신한다.
- `markNotReceiving()`: 영상 미수신 상태로 변경한다.
### 규칙
- 하나의 버스 안에서 같은 카메라 타입은 중복될 수 없다.
- MVP에서는 실제 영상 스트림을 저장하지 않고 수신 상태만 관리한다.
- 향후 실제 서비스에서는 영상 원본은 미디어 서버 또는 Object Storage에 저장하고, DB에는 영상 메타데이터와 수신 상태를 저장한다.

## 카메라 유형(CameraType)
_Enum_
### 상수
- `FRONT`: 전면 블랙박스
- `REAR`: 후면 블랙박스
- `INTERIOR_1`: 내부 CCTV 1
- `INTERIOR_2`: 내부 CCTV 2


---

## [이벤트 애그리거트]

## 버스 이벤트(BusEvent)
_Entity_
### 속성
- `id`: `Long`
- `busId`: `Long`
- `busNumber`: 이벤트 조회 화면에 표시할 버스번호
- `routeNumber`: 이벤트 조회 화면에 표시할 노선번호
- `routeName`: 이벤트 조회 화면에 표시할 노선명
- `eventType`: `EventType`
- `severity`: `EventSeverity`
- `description`: 이벤트 설명
- `coordinate`: `Coordinate` 이벤트 발생 위치 좌표
- `occurredAt`: 이벤트 발생 시각
### 행위
- `position()`: 이벤트 발생 위치 반환
### 규칙
- 이벤트는 반드시 특정 버스에 연결된다.
- 최근 이벤트 목록은 `occurredAt` 기준 최신순으로 조회한다.
- 이벤트 유형과 심각도는 정해진 값 중 하나여야 한다.

## 이벤트 유형(EventType)
_Enum_
### 상수
- `SUDDEN_STOP`: 급정거
- `SUDDEN_ACCELERATION`: 급가속
- `SUDDEN_DECELERATION`: 급감속
- `IMPACT`: 충격

## 이벤트 심각도(EventSeverity)
_Enum_
### 상수
- `LOW`: 낮음
- `MEDIUM`: 보통
- `HIGH`: 높음

---

## BusNotFoundException
_Exception_

## RouteDirectionNotFoundException
_Exception_

## DuplicatedRouteNumberException
_Exception_

## DuplicatedBusNumberException
_Exception_

## DuplicatedPlateNumberException
_Exception_
