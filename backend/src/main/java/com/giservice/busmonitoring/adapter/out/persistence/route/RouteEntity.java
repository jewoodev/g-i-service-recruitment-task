package com.giservice.busmonitoring.adapter.out.persistence.route;

import com.giservice.busmonitoring.domain.route.Route;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "routes")
public class RouteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "route_number", nullable = false, length = 20)
	private String routeNumber;

	@Column(nullable = false, length = 100)
	private String name;

	protected RouteEntity() {
	}

	public Route toDomain() {
		return new Route(id, routeNumber, name);
	}

	public Long getId() {
		return id;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public String getName() {
		return name;
	}
}
