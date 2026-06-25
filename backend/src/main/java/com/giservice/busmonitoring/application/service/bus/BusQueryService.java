package com.giservice.busmonitoring.application.service.bus;

import com.giservice.busmonitoring.application.provided.bus.BusQueryProvider;
import com.giservice.busmonitoring.application.required.bus.BusQueryRepository;
import com.giservice.busmonitoring.domain.bus.Bus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class BusQueryService implements BusQueryProvider {

	private final BusQueryRepository busQueryRepository;

	public BusQueryService(BusQueryRepository busQueryRepository) {
		this.busQueryRepository = busQueryRepository;
	}

	@Override
	public List<Bus> getBuses() {
		return busQueryRepository.findAll();
	}

	@Override
	public Bus getBus(Long id) {
		return busQueryRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("Bus not found: id=" + id));
	}
}
