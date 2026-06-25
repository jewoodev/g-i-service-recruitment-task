package com.giservice.busmonitoring.adapter.out.persistence.route;

import com.giservice.busmonitoring.domain.route.RouteDirection;
import com.giservice.busmonitoring.domain.route.RouteDirectionType;
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

@Entity
@Table(name = "route_directions")
public class RouteDirectionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "route_id", nullable = false)
	private RouteEntity route;

	@Enumerated(EnumType.STRING)
	@Column(name = "direction_type", nullable = false, length = 20)
	private RouteDirectionType directionType;

	@Column(name = "origin_name", nullable = false, length = 100)
	private String originName;

	@Column(name = "destination_name", nullable = false, length = 100)
	private String destinationName;

	protected RouteDirectionEntity() {
	}

	public RouteDirection toDomain() {
		return new RouteDirection(id, route.toDomain(), directionType, originName, destinationName);
	}

	public Long getId() {
		return id;
	}

	public RouteEntity getRoute() {
		return route;
	}
}
