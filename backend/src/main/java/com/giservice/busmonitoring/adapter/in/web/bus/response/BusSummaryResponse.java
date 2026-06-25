package com.giservice.busmonitoring.adapter.in.web.bus.response;

import com.giservice.busmonitoring.domain.bus.BusCommunicationStatus;
import com.giservice.busmonitoring.domain.bus.Bus;
import com.giservice.busmonitoring.domain.route.RouteDirection;
import com.giservice.busmonitoring.domain.route.RouteDirectionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BusSummaryResponse(
		Long id,
		String busNumber,
		String routeNumber,
		String routeName,
		RouteDirectionType directionType,
		String directionName,
		BigDecimal currentSpeed,
		BusCommunicationStatus communicationStatus,
		LocalDateTime lastCommunicationAt,
		BigDecimal latitude,
		BigDecimal longitude,
		LocalDateTime positionRecordedAt
) {

	public static BusSummaryResponse from(Bus bus, LocalDateTime referenceTime) {
		RouteDirection routeDirection = bus.routeDirection();
		return new BusSummaryResponse(
			bus.id(),
			bus.busNumber(),
			routeDirection.route().routeNumber(),
			routeDirection.route().name(),
			routeDirection.directionType(),
			routeDirection.displayName(),
			bus.currentSpeed(),
			bus.communicationStatusAt(referenceTime),
			bus.lastCommunicationAt(),
			bus.latestPosition() == null ? null : bus.latestPosition().coordinate().latitude(),
			bus.latestPosition() == null ? null : bus.latestPosition().coordinate().longitude(),
			bus.latestPosition() == null ? null : bus.latestPosition().recordedAt()
		);
	}
}
