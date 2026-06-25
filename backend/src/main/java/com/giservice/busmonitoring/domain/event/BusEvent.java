package com.giservice.busmonitoring.domain.event;

import com.giservice.busmonitoring.domain.common.Coordinate;

import java.time.LocalDateTime;

public record BusEvent(
		Long id,
		Long busId,
		String busNumber,
		String routeNumber,
		String routeName,
		EventType eventType,
		EventSeverity severity,
		String description,
		Coordinate coordinate,
		LocalDateTime occurredAt
) {

	public Coordinate position() {
		return coordinate;
	}
}
