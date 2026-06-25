package com.giservice.busmonitoring.adapter.out.persistence.position;

import com.giservice.busmonitoring.adapter.out.persistence.bus.BusEntity;
import com.giservice.busmonitoring.domain.bus.BusPosition;
import com.giservice.busmonitoring.domain.common.Coordinate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "bus_positions")
public class BusPositionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bus_id", nullable = false)
	private BusEntity bus;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal longitude;

	@Column(nullable = false, precision = 5, scale = 2)
	private BigDecimal speed;

	@Column(name = "recorded_at", nullable = false)
	private LocalDateTime recordedAt;

	protected BusPositionEntity() {
	}

	public BusPosition toDomain() {
		return new BusPosition(id, bus.getId(), new Coordinate(latitude, longitude), speed, recordedAt);
	}

	public Long getId() {
		return id;
	}

	public Long getBusId() {
		return bus.getId();
	}
}
