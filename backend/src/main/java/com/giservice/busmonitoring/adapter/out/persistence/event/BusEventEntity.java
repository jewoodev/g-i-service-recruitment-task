package com.giservice.busmonitoring.adapter.out.persistence.event;

import com.giservice.busmonitoring.adapter.out.persistence.bus.BusEntity;
import com.giservice.busmonitoring.domain.common.Coordinate;
import com.giservice.busmonitoring.domain.event.BusEvent;
import com.giservice.busmonitoring.domain.event.EventSeverity;
import com.giservice.busmonitoring.domain.event.EventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_events")
public class BusEventEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bus_id", nullable = false)
	private BusEntity bus;

	@Enumerated(EnumType.STRING)
	@Column(name = "event_type", nullable = false, length = 40)
	private EventType eventType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EventSeverity severity;

	@Column(nullable = false, length = 255)
	private String description;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal longitude;

	@Column(name = "occurred_at", nullable = false)
	private LocalDateTime occurredAt;

	protected BusEventEntity() {
	}

	public BusEvent toDomain() {
		return new BusEvent(
			id,
			bus.getId(),
			bus.getBusNumber(),
			bus.getRouteDirection().getRoute().getRouteNumber(),
			bus.getRouteDirection().getRoute().getName(),
			eventType,
			severity,
			description,
			new Coordinate(latitude, longitude),
			occurredAt
		);
	}
}
