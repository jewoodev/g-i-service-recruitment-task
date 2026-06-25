package com.giservice.busmonitoring.adapter.out.persistence.camera;

import com.giservice.busmonitoring.adapter.out.persistence.bus.BusEntity;
import com.giservice.busmonitoring.domain.bus.BusCameraStatus;
import com.giservice.busmonitoring.domain.bus.CameraType;
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

import java.time.LocalDateTime;

@Entity
@Table(name = "bus_camera_statuses")
public class BusCameraStatusEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "bus_id", nullable = false)
	private BusEntity bus;

	@Enumerated(EnumType.STRING)
	@Column(name = "camera_type", nullable = false, length = 30)
	private CameraType cameraType;

	@Column(nullable = false)
	private boolean receiving;

	@Column(name = "stream_url", length = 255)
	private String streamUrl;

	@Column(name = "last_received_at")
	private LocalDateTime lastReceivedAt;

	protected BusCameraStatusEntity() {
	}

	public BusCameraStatus toDomain() {
		return new BusCameraStatus(id, bus.getId(), cameraType, receiving, streamUrl, lastReceivedAt);
	}
}
