package com.giservice.busmonitoring.adapter.out.persistence.stop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StopJpaRepository extends JpaRepository<StopEntity, Long> {
}
