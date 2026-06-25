import type {
  BusDetailResponse,
  BusPositionResponse,
  BusSummaryResponse,
  ErrorResponse,
  EventResponse,
} from './types'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api/v1'

async function requestJson<T>(path: string, signal?: AbortSignal): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      Accept: 'application/json',
    },
    signal,
  })

  if (!response.ok) {
    throw new Error(await getErrorMessage(response))
  }

  return response.json() as Promise<T>
}

async function getErrorMessage(response: Response): Promise<string> {
  try {
    const error = (await response.json()) as Partial<ErrorResponse>
    return error.message ?? `${response.status} ${response.statusText}`
  } catch {
    return `${response.status} ${response.statusText}`
  }
}

export const busMonitoringApi = {
  getBuses(signal?: AbortSignal) {
    return requestJson<BusSummaryResponse[]>('/buses', signal)
  },

  getBus(busId: number, signal?: AbortSignal) {
    return requestJson<BusDetailResponse>(`/buses/${busId}`, signal)
  },

  getLatestBusPositions(signal?: AbortSignal) {
    return requestJson<BusPositionResponse[]>('/buses/positions/latest', signal)
  },

  getRecentEvents(limit = 20, signal?: AbortSignal) {
    const params = new URLSearchParams({ limit: String(limit) })
    return requestJson<EventResponse[]>(`/events/recent?${params}`, signal)
  },
}
