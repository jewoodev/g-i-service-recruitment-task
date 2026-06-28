# RDBMS 인덱스 설계

이 문서는 현재 백엔드의 RDBMS 인덱스 설계를 설명한다. 실제 스키마 기준은 Flyway 마이그레이션인 `src/main/resources/db/migration/V1__init_schema.sql`이다.

## 설계 목표

서울 버스 관제 MVP에서 가장 자주 호출되는 조회는 다음 두 가지다.

- 지도와 목록 화면에서 각 버스의 최신 GPS 위치를 조회한다.
- 관제 화면에서 최근 이벤트를 최신순으로 조회한다.

따라서 현재 인덱스는 대량 쓰기 이력 테이블인 `bus_positions`, `bus_events`의 최신 데이터 조회 비용을 낮추는 데 우선순위를 둔다. 노선, 정류장, 차량 기본 정보는 상대적으로 변경 빈도와 데이터 규모가 작고, 업무 식별자 중복 방지 목적의 `UNIQUE` 제약을 통해 필요한 인덱스를 확보한다.

## 명시적 보조 인덱스

### `bus_positions`

```sql
CREATE INDEX idx_bus_positions_bus_recorded_at
    ON bus_positions (bus_id, recorded_at);
```

이 인덱스는 버스별 위치 이력에서 최신 위치를 찾기 위한 복합 인덱스다.

적용되는 조회 흐름은 다음과 같다.

| 조회 | Repository 메서드 | 인덱스 사용 의도 |
| --- | --- | --- |
| 특정 버스 최신 위치 | `findTopByBus_IdOrderByRecordedAtDesc(busId)` | `bus_id`로 범위를 좁힌 뒤 `recorded_at` 최신순으로 1건 조회 |
| 전체 버스 최신 위치 | `findLatestPositions()` | 버스별 `max(recorded_at)` 계산 시 `bus_id`, `recorded_at` 순서 활용 |
| 버스 목록/상세의 현재속도 | `BusQueryRepositoryImpl`에서 최신 위치 조합 | 최신 위치의 `speed`를 현재속도로 사용 |

현재 인덱스는 `(bus_id, recorded_at)` 순서다. 버스 한 대의 위치 이력을 좁혀서 최신값을 찾는 조회에 맞춘 선택이다. MySQL은 오름차순 인덱스도 역방향으로 스캔할 수 있으므로 `ORDER BY recorded_at DESC LIMIT 1` 형태에도 사용할 수 있다.

동일한 버스에 같은 `recorded_at` 값이 여러 건 들어올 가능성이 커지면 정렬 결과가 모호해질 수 있다. 운영 단계에서는 `ORDER BY recorded_at DESC, id DESC`로 조회 기준을 명확히 하고, 인덱스도 `(bus_id, recorded_at, id)`로 확장하는 방안을 검토한다.

### `bus_events`

```sql
CREATE INDEX idx_bus_events_occurred_at
    ON bus_events (occurred_at);
```

이 인덱스는 최근 이벤트 피드를 최신순으로 조회하기 위한 단일 컬럼 인덱스다.

적용되는 조회 흐름은 다음과 같다.

| 조회 | Repository 메서드 | 인덱스 사용 의도 |
| --- | --- | --- |
| 최근 이벤트 목록 | `findAllByOrderByOccurredAtDesc(Pageable)` | `occurred_at` 최신순으로 정렬하고 `limit`만큼 조회 |

현재 API는 `GET /api/v1/events/recent?limit=...`처럼 전체 이벤트에서 최신 이벤트를 가져온다. 그래서 `occurred_at` 단일 인덱스가 가장 단순한 선택이다.

향후 버스별 이벤트, 이벤트 유형별 필터, 심각도별 필터가 추가되면 다음 복합 인덱스를 별도로 검토한다.

- `bus_events (bus_id, occurred_at)`
- `bus_events (event_type, occurred_at)`
- `bus_events (severity, occurred_at)`

필터 조건의 선택도가 낮은 컬럼을 무분별하게 앞에 두면 쓰기 비용만 늘고 조회 이점이 작을 수 있으므로, 실제 API 조건과 실행 계획을 기준으로 추가한다.

## 제약 조건 기반 인덱스

다음 제약 조건은 데이터 정합성을 보장하면서 RDBMS 내부에서 인덱스 역할도 한다.

| 테이블 | 제약 조건 | 목적 |
| --- | --- | --- |
| `routes` | `UNIQUE (route_number)` | 노선번호 중복 방지 |
| `route_directions` | `UNIQUE (route_id, direction_type)` | 하나의 노선 안에서 같은 방향 중복 방지 |
| `stops` | `UNIQUE (stop_number)` | 정류장번호 중복 방지 |
| `route_stops` | `UNIQUE (route_direction_id, stop_order)` | 방향별 정류장 순서 중복 방지 |
| `route_stops` | `UNIQUE (route_direction_id, stop_id)` | 방향별 같은 정류장 중복 방지 |
| `buses` | `UNIQUE (bus_number)` | 버스번호 중복 방지 |
| `buses` | `UNIQUE (plate_number)` | 차량번호 중복 방지 |
| `bus_camera_statuses` | `UNIQUE (bus_id, camera_type)` | 버스별 카메라 유형 중복 방지 |

현재 `buses` 목록 조회는 `bus_number` 오름차순 정렬을 사용한다. `uk_buses_bus_number`는 중복 방지뿐 아니라 목록 정렬에도 활용될 수 있다.

## 외래키 인덱스

외래키 컬럼은 조인, 부모 row 변경 검증, 향후 필터 조회에서 반복적으로 사용될 수 있다. MySQL InnoDB는 외래키 제약을 만들 때 필요한 인덱스가 없으면 자동으로 인덱스를 생성할 수 있지만, 이 프로젝트에서는 스키마 의도를 명확히 하기 위해 명시적인 인덱스를 Flyway로 관리한다.

`V3__add_foreign_key_indexes`에서 보장하는 인덱스는 다음과 같다. 기존 DB가 외래키 생성 시 같은 컬럼의 보조 인덱스를 이미 만든 경우에는 중복 생성하지 않고 명시적인 `idx_*` 이름으로 맞춘다.

| 인덱스 | 테이블 | 컬럼 | 목적 |
| --- | --- | --- | --- |
| `idx_route_stops_stop_id` | `route_stops` | `stop_id` | 정류장 기준 노선 순서 조인/조회 보조 |
| `idx_buses_route_direction_id` | `buses` | `route_direction_id` | 노선 방향 기준 버스 조인/조회 보조 |
| `idx_buses_current_stop_id` | `buses` | `current_stop_id` | 현재 정류장 기준 버스 조인/조회 보조 |
| `idx_buses_next_stop_id` | `buses` | `next_stop_id` | 다음 정류장 기준 버스 조인/조회 보조 |
| `idx_bus_events_bus_id` | `bus_events` | `bus_id` | 버스 기준 이벤트 조인/조회 보조 |

다음 외래키 컬럼은 이미 기존 인덱스의 선두 컬럼으로 커버된다. 별도 단일 컬럼 인덱스를 추가하면 중복 인덱스가 되므로 만들지 않는다.

| 외래키 컬럼 | 커버하는 인덱스 |
| --- | --- |
| `route_directions.route_id` | `uk_route_directions_route_type (route_id, direction_type)` |
| `route_stops.route_direction_id` | `uk_route_stops_direction_order (route_direction_id, stop_order)` |
| `bus_positions.bus_id` | `idx_bus_positions_bus_recorded_at (bus_id, recorded_at)` |
| `bus_camera_statuses.bus_id` | `uk_bus_camera_statuses_bus_camera (bus_id, camera_type)` |

## 현재 설계의 범위와 한계

현재 인덱스 설계는 MVP 조회 API에 맞춘 최소 설계다.

- 버스 수가 7,000대 수준이어도 최신 위치 조회와 최근 이벤트 조회 중심이면 현재 구조로 출발할 수 있다.
- 위치 이력과 이벤트 이력은 시간이 지날수록 계속 증가하므로, 운영 단계에서는 기간 파티셔닝이나 보관 정책이 필요하다.
- 전체 버스 최신 위치 조회는 현재 버스별 `max(recorded_at)`을 계산한다. 위치 이력이 커지면 별도 `latest_bus_positions` 테이블이나 Redis 같은 캐시로 최신 상태를 분리하는 방안을 검토한다.
- 지도 화면에서 영역 기반 검색이 필요해지면 위도/경도 범위 조건에 맞는 인덱스 또는 공간 인덱스를 별도로 설계한다.
- 이벤트 조회가 필터 중심으로 확장되면 실제 API 조건 조합에 맞춰 복합 인덱스를 추가한다.

## 변경 원칙

새 인덱스는 다음 기준을 만족할 때 추가한다.

- 실제 API 또는 배치 작업의 조회 조건이 명확하다.
- `WHERE`, `ORDER BY`, `LIMIT`, `JOIN` 조건에서 반복적으로 사용된다.
- 데이터 증가 시 실행 계획상 테이블 풀스캔이나 큰 정렬 비용이 확인된다.
- 쓰기 비용과 저장 공간 증가보다 조회 이점이 크다.

인덱스 추가는 JPA 엔티티 어노테이션보다 Flyway 마이그레이션으로 관리한다. 이 프로젝트는 테이블 생성과 초기 데이터를 Flyway로 관리하므로, 운영 DB 스키마 변경 이력도 Flyway에 남기는 것이 일관적이다.
