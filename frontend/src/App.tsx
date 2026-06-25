import { useCallback, useEffect, useMemo, useState } from 'react'
import { busMonitoringApi } from './api/client'
import type {
  BusCommunicationStatus,
  BusDetailResponse,
  BusPositionResponse,
  BusSummaryResponse,
  CameraType,
  EventResponse,
  EventSeverity,
  EventType,
  RouteDirectionType,
} from './api/types'
import './App.css'
import { BusMap } from './components/BusMap'

const BUS_POLLING_MS = 5_000
const EVENT_POLLING_MS = 10_000
const RECENT_EVENT_LIMIT = 20

const communicationStatusLabel: Record<BusCommunicationStatus, string> = {
  ONLINE: '온라인',
  OFFLINE: '오프라인',
}

const directionLabel: Record<RouteDirectionType, string> = {
  OUTBOUND: '상행',
  INBOUND: '하행',
}

const eventTypeLabel: Record<EventType, string> = {
  SUDDEN_STOP: '급정거',
  SUDDEN_ACCELERATION: '급가속',
  SUDDEN_DECELERATION: '급감속',
  IMPACT: '충격',
}

const severityLabel: Record<EventSeverity, string> = {
  LOW: '낮음',
  MEDIUM: '보통',
  HIGH: '높음',
}

const cameraTypeLabel: Record<CameraType, string> = {
  FRONT: '전면',
  REAR: '후면',
  INTERIOR_1: '내부 1',
  INTERIOR_2: '내부 2',
}

function App() {
  const [buses, setBuses] = useState<BusSummaryResponse[]>([])
  const [positions, setPositions] = useState<BusPositionResponse[]>([])
  const [events, setEvents] = useState<EventResponse[]>([])
  const [selectedBusId, setSelectedBusId] = useState<number | null>(null)
  const [selectedBus, setSelectedBus] = useState<BusDetailResponse | null>(null)
  const [dashboardError, setDashboardError] = useState<string | null>(null)
  const [detailError, setDetailError] = useState<string | null>(null)
  const [isDashboardLoading, setDashboardLoading] = useState(true)
  const [lastUpdatedAt, setLastUpdatedAt] = useState<Date | null>(null)

  const loadDashboard = useCallback(async (signal?: AbortSignal) => {
    const [nextBuses, nextPositions] = await Promise.all([
      busMonitoringApi.getBuses(signal),
      busMonitoringApi.getLatestBusPositions(signal),
    ])

    setBuses(nextBuses)
    setPositions(nextPositions)
    setDashboardError(null)
    setDashboardLoading(false)
    setLastUpdatedAt(new Date())
  }, [])

  const loadEvents = useCallback(async (signal?: AbortSignal) => {
    const nextEvents = await busMonitoringApi.getRecentEvents(RECENT_EVENT_LIMIT, signal)
    setEvents(nextEvents)
  }, [])

  useEffect(() => {
    const controller = new AbortController()

    const refresh = async () => {
      try {
        await loadDashboard(controller.signal)
      } catch (error) {
        if (!controller.signal.aborted) {
          setDashboardError(toErrorMessage(error))
          setDashboardLoading(false)
        }
      }
    }

    void refresh()
    const intervalId = window.setInterval(refresh, BUS_POLLING_MS)

    return () => {
      controller.abort()
      window.clearInterval(intervalId)
    }
  }, [loadDashboard])

  useEffect(() => {
    const controller = new AbortController()

    const refresh = async () => {
      try {
        await loadEvents(controller.signal)
      } catch (error) {
        if (!controller.signal.aborted) {
          setDashboardError(toErrorMessage(error))
        }
      }
    }

    void refresh()
    const intervalId = window.setInterval(refresh, EVENT_POLLING_MS)

    return () => {
      controller.abort()
      window.clearInterval(intervalId)
    }
  }, [loadEvents])

  useEffect(() => {
    if (buses.length === 0) {
      setSelectedBusId(null)
      return
    }

    setSelectedBusId((current) => {
      if (current !== null && buses.some((bus) => bus.id === current)) {
        return current
      }

      return buses[0].id
    })
  }, [buses])

  useEffect(() => {
    if (selectedBusId === null) {
      setSelectedBus(null)
      return
    }

    const controller = new AbortController()

    const refresh = async () => {
      try {
        const detail = await busMonitoringApi.getBus(selectedBusId, controller.signal)
        setSelectedBus(detail)
        setDetailError(null)
      } catch (error) {
        if (!controller.signal.aborted) {
          setDetailError(toErrorMessage(error))
        }
      }
    }

    void refresh()
    const intervalId = window.setInterval(refresh, BUS_POLLING_MS)

    return () => {
      controller.abort()
      window.clearInterval(intervalId)
    }
  }, [selectedBusId])

  const summary = useMemo(() => {
    const onlineCount = buses.filter((bus) => bus.communicationStatus === 'ONLINE').length
    const averageSpeed =
      buses.length === 0
        ? 0
        : buses.reduce((sum, bus) => sum + bus.currentSpeed, 0) / buses.length

    return {
      totalBusCount: buses.length,
      onlineCount,
      eventCount: events.length,
      averageSpeed,
    }
  }, [buses, events])

  return (
    <main className="app">
      <header className="app-header">
        <div>
          <p className="eyebrow">Bus Monitoring MVP</p>
          <h1>서울 버스 관제</h1>
        </div>
        <time className="clock">
          {lastUpdatedAt ? `${formatTime(lastUpdatedAt)} 갱신` : '데이터 연동 대기'}
        </time>
      </header>

      {dashboardError && (
        <div className="alert" role="alert">
          API 호출 실패: {dashboardError}
        </div>
      )}

      <section className="summary-grid" aria-label="운행 요약">
        <SummaryCard label="운행 버스" value={formatCount(summary.totalBusCount)} />
        <SummaryCard label="온라인" value={formatCount(summary.onlineCount)} />
        <SummaryCard label="최근 이벤트" value={formatCount(summary.eventCount)} />
        <SummaryCard label="평균 속도" value={`${summary.averageSpeed.toFixed(1)} km/h`} />
      </section>

      <section className="dashboard">
        <section className="map-panel" aria-label="버스 위치 지도">
          <div className="panel-header">
            <h2>버스 위치</h2>
            <span>{isDashboardLoading ? '조회 중' : `${positions.length}대 표시`}</span>
          </div>
          <BusMap
            positions={positions}
            selectedBusId={selectedBusId}
            onSelectBus={setSelectedBusId}
          />
        </section>

        <aside className="side-panel" aria-label="운행 정보">
          <section className="panel-section bus-list-section">
            <h2>버스 목록</h2>
            {buses.length === 0 ? (
              <div className="empty-row">
                {isDashboardLoading ? '버스 정보를 조회 중입니다.' : '조회된 버스가 없습니다.'}
              </div>
            ) : (
              <div className="bus-list">
                {buses.map((bus) => (
                  <button
                    key={bus.id}
                    type="button"
                    className={bus.id === selectedBusId ? 'bus-row selected' : 'bus-row'}
                    onClick={() => setSelectedBusId(bus.id)}
                  >
                    <span className="bus-main">
                      <strong>{bus.busNumber}</strong>
                      <span>{bus.routeName}</span>
                    </span>
                    <span className="bus-meta">
                      <StatusPill status={bus.communicationStatus} />
                      <span>{bus.currentSpeed.toFixed(1)} km/h</span>
                    </span>
                  </button>
                ))}
              </div>
            )}
          </section>

          <section className="panel-section detail-section">
            <h2>버스 상세</h2>
            {detailError && <div className="inline-error">{detailError}</div>}
            {selectedBus ? (
              <BusDetail bus={selectedBus} />
            ) : (
              <div className="empty-row">선택된 버스가 없습니다.</div>
            )}
          </section>

          <section className="panel-section event-section">
            <h2>최근 이벤트</h2>
            {events.length === 0 ? (
              <div className="empty-row">조회된 이벤트가 없습니다.</div>
            ) : (
              <div className="event-list">
                {events.map((event) => (
                  <article key={event.id} className="event-row">
                    <div>
                      <span className={`severity ${event.severity.toLowerCase()}`}>
                        {severityLabel[event.severity]}
                      </span>
                      <strong>{eventTypeLabel[event.eventType]}</strong>
                    </div>
                    <p>{event.description}</p>
                    <span className="event-meta">
                      {event.busNumber} · {formatDateTime(event.occurredAt)}
                    </span>
                  </article>
                ))}
              </div>
            )}
          </section>
        </aside>
      </section>
    </main>
  )
}

function SummaryCard({ label, value }: { label: string; value: string }) {
  return (
    <article className="summary-card">
      <span className="summary-label">{label}</span>
      <strong>{value}</strong>
    </article>
  )
}

function StatusPill({ status }: { status: BusCommunicationStatus }) {
  return <span className={`status-pill ${status.toLowerCase()}`}>{communicationStatusLabel[status]}</span>
}

function BusDetail({ bus }: { bus: BusDetailResponse }) {
  return (
    <div className="detail-card">
      <div className="detail-title">
        <strong>{bus.busNumber}</strong>
        <StatusPill status={bus.communicationStatus} />
      </div>
      <dl className="detail-list">
        <div>
          <dt>차량번호</dt>
          <dd>{bus.plateNumber}</dd>
        </div>
        <div>
          <dt>노선</dt>
          <dd>
            {bus.routeNumber} · {bus.routeName}
          </dd>
        </div>
        <div>
          <dt>방향</dt>
          <dd>
            {directionLabel[bus.directionType]} · {bus.directionName}
          </dd>
        </div>
        <div>
          <dt>정류장</dt>
          <dd>
            {bus.currentStopName ?? '-'} → {bus.nextStopName ?? '-'}
          </dd>
        </div>
        <div>
          <dt>속도</dt>
          <dd>{bus.currentSpeed.toFixed(1)} km/h</dd>
        </div>
        <div>
          <dt>통신</dt>
          <dd>{formatDateTime(bus.lastCommunicationAt)}</dd>
        </div>
      </dl>
      <div className="camera-grid">
        {bus.cameraStatuses.map((camera) => (
          <span
            key={camera.cameraType}
            className={camera.receiving ? 'camera-chip receiving' : 'camera-chip'}
          >
            {cameraTypeLabel[camera.cameraType]}
          </span>
        ))}
      </div>
    </div>
  )
}

function formatCount(value: number) {
  return value.toLocaleString('ko-KR')
}

function formatTime(value: Date) {
  return value.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

function formatDateTime(value: string) {
  return new Date(value).toLocaleString('ko-KR', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function toErrorMessage(error: unknown) {
  return error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.'
}

export default App
