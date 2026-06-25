package com.giservice.busmonitoring.adapter.out.persistence.bus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusJpaRepository extends JpaRepository<BusEntity, Long> {

	@Override
	@EntityGraph(attributePaths = { "routeDirection.route", "currentStop", "nextStop", "cameraStatuses" })
	Optional<BusEntity> findById(Long id);

	@EntityGraph(attributePaths = { "routeDirection.route", "currentStop", "nextStop", "cameraStatuses" })
	List<BusEntity> findAllByOrderByBusNumberAsc();
}
