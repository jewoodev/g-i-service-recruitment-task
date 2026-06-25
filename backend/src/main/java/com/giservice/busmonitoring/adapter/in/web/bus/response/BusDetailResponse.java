package com.giservice.busmonitoring.adapter.in.web.bus.response;

import com.giservice.busmonitoring.domain.bus.Bus;
import com.giservice.busmonitoring.domain.bus.BusCommunicationStatus;
import com.giservice.busmonitoring.domain.route.RouteDirection;
import com.giservice.busmonitoring.domain.route.RouteDirectionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BusDetailResponse(
		Long id,
		String busNumber,
		String plateNumber,
		String routeNumber,
		String routeName,
		RouteDirectionType directionType,
		String directionName,
		String originName,
		String destinationName,
		BigDecimal currentSpeed,
		BusCommunicationStatus communicationStatus,
		LocalDateTime lastCommunicationAt,
		String currentStopName,
		String nextStopName,
		BigDecimal latitude,
		BigDecimal longitude,
		LocalDateTime positionRecordedAt,
		List<CameraStatusResponse> cameraStatuses
) {

	public static BusDetailResponse from(Bus bus, LocalDateTime referenceTime) {
		RouteDirection routeDirection = bus.routeDirection();
		return new BusDetailResponse(
			bus.id(),
			bus.busNumber(),
			bus.plateNumber(),
			routeDirection.route().routeNumber(),
			routeDirection.route().name(),
			routeDirection.directionType(),
			routeDirection.displayName(),
			routeDirection.originName(),
			routeDirection.destinationName(),
			bus.currentSpeed(),
			bus.communicationStatusAt(referenceTime),
			bus.lastCommunicationAt(),
			bus.currentStop() == null ? null : bus.currentStop().name(),
			bus.nextStop() == null ? null : bus.nextStop().name(),
			bus.latestPosition() == null ? null : bus.latestPosition().coordinate().latitude(),
			bus.latestPosition() == null ? null : bus.latestPosition().coordinate().longitude(),
			bus.latestPosition() == null ? null : bus.latestPosition().recordedAt(),
			bus.cameraStatuses().stream()
				.map(CameraStatusResponse::from)
				.toList()
		);
	}
}
