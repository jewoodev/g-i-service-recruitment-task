package com.giservice.busmonitoring.adapter.out.persistence.stop;

import com.giservice.busmonitoring.domain.common.Coordinate;
import com.giservice.busmonitoring.domain.stop.Stop;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "stops")
public class StopEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "stop_number", nullable = false, length = 30)
	private String stopNumber;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal latitude;

	@Column(nullable = false, precision = 10, scale = 7)
	private BigDecimal longitude;

	protected StopEntity() {
	}

	public Stop toDomain() {
		return new Stop(id, stopNumber, name, new Coordinate(latitude, longitude));
	}

	public Long getId() {
		return id;
	}

	public String getStopNumber() {
		return stopNumber;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}
}
