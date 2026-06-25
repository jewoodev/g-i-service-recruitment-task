package com.giservice.busmonitoring.adapter.out.persistence.camera;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusCameraStatusJpaRepository extends JpaRepository<BusCameraStatusEntity, Long> {
}
