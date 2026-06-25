import { useEffect } from 'react'
import { CircleMarker, MapContainer, Popup, TileLayer, useMap } from 'react-leaflet'
import type { BusPositionResponse } from '../api/types'

const SEOUL_CENTER: [number, number] = [37.5665, 126.978]

interface BusMapProps {
  positions: BusPositionResponse[]
  selectedBusId: number | null
  onSelectBus: (busId: number) => void
}

function hasCoordinate(
  position: BusPositionResponse,
): position is BusPositionResponse & { latitude: number; longitude: number } {
  return position.latitude !== null && position.longitude !== null
}

function MapViewport({ positions }: { positions: BusPositionResponse[] }) {
  const map = useMap()

  useEffect(() => {
    const coordinates = positions.filter(hasCoordinate)

    if (coordinates.length === 0) {
      map.setView(SEOUL_CENTER, 12)
      return
    }

    const bounds = coordinates.map(
      (position) => [position.latitude, position.longitude] as [number, number],
    )
    map.fitBounds(bounds, { maxZoom: 15, padding: [44, 44] })
  }, [map, positions])

  return null
}

function markerColor(position: BusPositionResponse, selectedBusId: number | null) {
  if (position.busId === selectedBusId) {
    return '#2563eb'
  }

  return position.communicationStatus === 'ONLINE' ? '#059669' : '#dc2626'
}

export function BusMap({ positions, selectedBusId, onSelectBus }: BusMapProps) {
  const mappedPositions = positions.filter(hasCoordinate)

  return (
    <div className="map-surface">
      <MapContainer
        center={SEOUL_CENTER}
        zoom={12}
        scrollWheelZoom
        className="map-container"
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <MapViewport positions={positions} />
        {mappedPositions.map((position) => {
          const color = markerColor(position, selectedBusId)

          return (
            <CircleMarker
              key={position.busId}
              center={[position.latitude, position.longitude]}
              eventHandlers={{
                click: () => onSelectBus(position.busId),
              }}
              pathOptions={{
                color,
                fillColor: color,
                fillOpacity: 0.86,
                weight: position.busId === selectedBusId ? 4 : 2,
              }}
              radius={position.busId === selectedBusId ? 10 : 8}
            >
              <Popup>
                <strong>{position.busNumber}</strong>
                <br />
                {position.routeName}
                <br />
                {position.speed.toFixed(1)} km/h
              </Popup>
            </CircleMarker>
          )
        })}
      </MapContainer>
      {mappedPositions.length === 0 && (
        <div className="map-empty">표시할 위치 데이터가 없습니다.</div>
      )}
    </div>
  )
}
