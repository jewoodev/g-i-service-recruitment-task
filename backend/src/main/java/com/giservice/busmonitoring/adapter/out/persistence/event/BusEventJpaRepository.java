package com.giservice.busmonitoring.adapter.out.persistence.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusEventJpaRepository extends JpaRepository<BusEventEntity, Long> {

	@EntityGraph(attributePaths = { "bus", "bus.routeDirection.route" })
	List<BusEventEntity> findAllByOrderByOccurredAtDesc(Pageable pageable);
}
