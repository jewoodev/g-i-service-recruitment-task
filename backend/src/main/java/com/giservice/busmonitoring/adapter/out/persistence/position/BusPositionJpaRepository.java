package com.giservice.busmonitoring.adapter.out.persistence.position;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BusPositionJpaRepository extends JpaRepository<BusPositionEntity, Long> {

	Optional<BusPositionEntity> findTopByBus_IdOrderByRecordedAtDesc(Long busId);

	@Query("""
			select position
			from BusPositionEntity position
			where position.recordedAt = (
				select max(candidate.recordedAt)
				from BusPositionEntity candidate
				where candidate.bus.id = position.bus.id
			)
			""")
	List<BusPositionEntity> findLatestPositions();
}
