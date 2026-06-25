package com.giservice.busmonitoring.adapter.out.persistence.bus;

import com.giservice.busmonitoring.adapter.out.persistence.position.BusPositionEntity;
import com.giservice.busmonitoring.adapter.out.persistence.position.BusPositionJpaRepository;
import com.giservice.busmonitoring.application.required.bus.BusQueryRepository;
import com.giservice.busmonitoring.domain.bus.Bus;
import com.giservice.busmonitoring.domain.bus.BusPosition;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class BusQueryRepositoryImpl implements BusQueryRepository {

	private final BusJpaRepository busJpaRepository;

	private final BusPositionJpaRepository busPositionJpaRepository;

	public BusQueryRepositoryImpl(BusJpaRepository busJpaRepository, BusPositionJpaRepository busPositionJpaRepository) {
		this.busJpaRepository = busJpaRepository;
		this.busPositionJpaRepository = busPositionJpaRepository;
	}

	@Override
	public List<Bus> findAll() {
		Map<Long, BusPosition> latestPositionsByBusId = busPositionJpaRepository.findLatestPositions().stream()
			.map(BusPositionEntity::toDomain)
			.collect(Collectors.toMap(BusPosition::busId, Function.identity()));

		return busJpaRepository.findAllByOrderByBusNumberAsc().stream()
			.map(bus -> bus.toDomain(latestPositionsByBusId.get(bus.getId())))
			.toList();
	}

	@Override
	public Optional<Bus> findById(Long id) {
		return busJpaRepository.findById(id)
			.map(bus -> bus.toDomain(busPositionJpaRepository.findTopByBus_IdOrderByRecordedAtDesc(id)
				.map(BusPositionEntity::toDomain)
				.orElse(null)));
	}
}
