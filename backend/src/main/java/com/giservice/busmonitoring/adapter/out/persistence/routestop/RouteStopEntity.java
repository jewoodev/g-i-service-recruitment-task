package com.giservice.busmonitoring.adapter.out.persistence.routestop;

import com.giservice.busmonitoring.adapter.out.persistence.route.RouteDirectionEntity;
import com.giservice.busmonitoring.adapter.out.persistence.stop.StopEntity;
import com.giservice.busmonitoring.domain.route.RouteStop;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "route_stops")
public class RouteStopEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "route_direction_id", nullable = false)
	private RouteDirectionEntity routeDirection;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stop_id", nullable = false)
	private StopEntity stop;

	@Column(name = "stop_order", nullable = false)
	private Integer stopOrder;

	protected RouteStopEntity() {
	}

	public RouteStop toDomain() {
		return new RouteStop(id, routeDirection.toDomain(), stop.toDomain(), stopOrder);
	}

	public Long getId() {
		return id;
	}
}
