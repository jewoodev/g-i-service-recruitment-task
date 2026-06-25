package com.giservice.busmonitoring.adapter.in.web.bus.response;

import com.giservice.busmonitoring.domain.bus.Bus;
import com.giservice.busmonitoring.domain.bus.BusCommunicationStatus;
import com.giservice.busmonitoring.domain.route.RouteDirection;
import com.giservice.busmonitoring.domain.route.RouteDirectionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BusPositionResponse(
		Long busId,
		String busNumber,
		String routeNumber,
		String routeName,
		RouteDirectionType directionType,
		String directionName,
		BigDecimal latitude,
		BigDecimal longitude,
		BigDecimal speed,
		BusCommunicationStatus communicationStatus,
		LocalDateTime recordedAt
) {

	public static BusPositionResponse from(Bus bus, LocalDateTime referenceTime) {
		RouteDirection routeDirection = bus.routeDirection();
		if (bus.latestPosition() == null) {
			return new BusPositionResponse(
				bus.id(),
				bus.busNumber(),
				routeDirection.route().routeNumber(),
				routeDirection.route().name(),
				routeDirection.directionType(),
				routeDirection.displayName(),
				null,
				null,
				BigDecimal.ZERO,
				bus.communicationStatusAt(referenceTime),
				null
			);
		}

		return new BusPositionResponse(
			bus.id(),
			bus.busNumber(),
			routeDirection.route().routeNumber(),
			routeDirection.route().name(),
			routeDirection.directionType(),
			routeDirection.displayName(),
			bus.latestPosition().coordinate().latitude(),
			bus.latestPosition().coordinate().longitude(),
			bus.latestPosition().speed(),
			bus.communicationStatusAt(referenceTime),
			bus.latestPosition().recordedAt()
		);
	}
}
