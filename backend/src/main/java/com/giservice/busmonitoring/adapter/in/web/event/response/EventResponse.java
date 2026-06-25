package com.giservice.busmonitoring.adapter.in.web.event.response;

import com.giservice.busmonitoring.domain.event.BusEvent;
import com.giservice.busmonitoring.domain.event.EventSeverity;
import com.giservice.busmonitoring.domain.event.EventType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
		Long id,
		Long busId,
		String busNumber,
		String routeNumber,
		String routeName,
		EventType eventType,
		EventSeverity severity,
		String description,
		BigDecimal latitude,
		BigDecimal longitude,
		LocalDateTime occurredAt
) {

	public static EventResponse from(BusEvent event) {
		return new EventResponse(
			event.id(),
			event.busId(),
			event.busNumber(),
			event.routeNumber(),
			event.routeName(),
			event.eventType(),
			event.severity(),
			event.description(),
			event.coordinate().latitude(),
			event.coordinate().longitude(),
			event.occurredAt()
		);
	}
}
