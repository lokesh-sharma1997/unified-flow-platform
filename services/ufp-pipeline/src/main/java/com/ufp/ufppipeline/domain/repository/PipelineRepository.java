package com.ufp.ufppipeline.domain.repository;

import com.ufp.ufppipeline.domain.entity.Pipeline;
import com.ufp.ufppipeline.domain.enums.PipelineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PipelineRepository extends JpaRepository<Pipeline, UUID> {
    boolean existsByName(String name);
    Optional<Pipeline> findByName(String name);
    Page<Pipeline> findByStatus(PipelineStatus status, Pageable pageable);
    Page<Pipeline> findByCreatedBy(String createdBy, Pageable pageable);
}
