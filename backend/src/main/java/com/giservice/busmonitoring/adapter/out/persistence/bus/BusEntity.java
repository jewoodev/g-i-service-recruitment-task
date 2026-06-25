package com.giservice.busmonitoring.adapter.out.persistence.bus;

import com.giservice.busmonitoring.adapter.out.persistence.camera.BusCameraStatusEntity;
import com.giservice.busmonitoring.adapter.out.persistence.route.RouteDirectionEntity;
import com.giservice.busmonitoring.adapter.out.persistence.stop.StopEntity;
import com.giservice.busmonitoring.domain.bus.Bus;
import com.giservice.busmonitoring.domain.bus.BusCameraStatus;
import com.giservice.busmonitoring.domain.bus.BusPosition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buses")
public class BusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "bus_number", nullable = false, length = 30)
	private String busNumber;

	@Column(name = "plate_number", nullable = false, length = 30)
	private String plateNumber;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "route_direction_id", nullable = false)
	private RouteDirectionEntity routeDirection;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_stop_id")
	private StopEntity currentStop;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "next_stop_id")
	private StopEntity nextStop;

	@Column(name = "last_communication_at", nullable = false)
	private LocalDateTime lastCommunicationAt;

	@OneToMany(mappedBy = "bus")
	@OrderBy("cameraType ASC")
	private List<BusCameraStatusEntity> cameraStatuses = new ArrayList<>();

	protected BusEntity() {
	}

	public Bus toDomain(BusPosition latestPosition) {
		List<BusCameraStatus> cameraStatusDomains = cameraStatuses.stream()
			.map(BusCameraStatusEntity::toDomain)
			.toList();

		return new Bus(
			id,
			busNumber,
			plateNumber,
			routeDirection.toDomain(),
			currentStop == null ? null : currentStop.toDomain(),
			nextStop == null ? null : nextStop.toDomain(),
			lastCommunicationAt,
			latestPosition,
			cameraStatusDomains
		);
	}

	public Long getId() {
		return id;
	}

	public String getBusNumber() {
		return busNumber;
	}

	public RouteDirectionEntity getRouteDirection() {
		return routeDirection;
	}
}
