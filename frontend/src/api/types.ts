export type BusCommunicationStatus = 'ONLINE' | 'OFFLINE'
export type RouteDirectionType = 'OUTBOUND' | 'INBOUND'
export type CameraType = 'FRONT' | 'REAR' | 'INTERIOR_1' | 'INTERIOR_2'
export type EventType =
  | 'SUDDEN_STOP'
  | 'SUDDEN_ACCELERATION'
  | 'SUDDEN_DECELERATION'
  | 'IMPACT'
export type EventSeverity = 'LOW' | 'MEDIUM' | 'HIGH'

export interface BusSummaryResponse {
  id: number
  busNumber: string
  routeNumber: string
  routeName: string
  directionType: RouteDirectionType
  directionName: string
  currentSpeed: number
  communicationStatus: BusCommunicationStatus
  lastCommunicationAt: string
  latitude: number | null
  longitude: number | null
  positionRecordedAt: string | null
}

export interface BusDetailResponse extends BusSummaryResponse {
  plateNumber: string
  originName: string
  destinationName: string
  currentStopName: string | null
  nextStopName: string | null
  cameraStatuses: CameraStatusResponse[]
}

export interface CameraStatusResponse {
  cameraType: CameraType
  receiving: boolean
  streamUrl: string | null
  lastReceivedAt: string | null
}

export interface BusPositionResponse {
  busId: number
  busNumber: string
  routeNumber: string
  routeName: string
  directionType: RouteDirectionType
  directionName: string
  latitude: number | null
  longitude: number | null
  speed: number
  communicationStatus: BusCommunicationStatus
  recordedAt: string | null
}

export interface EventResponse {
  id: number
  busId: number
  busNumber: string
  routeNumber: string
  routeName: string
  eventType: EventType
  severity: EventSeverity
  description: string
  latitude: number
  longitude: number
  occurredAt: string
}

export interface ErrorResponse {
  message: string
  timestamp: string
}
