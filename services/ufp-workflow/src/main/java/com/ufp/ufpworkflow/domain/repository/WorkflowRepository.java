package com.ufp.ufpworkflow.domain.repository;

import com.ufp.ufpworkflow.domain.entity.Workflow;
import com.ufp.ufpworkflow.domain.enums.WorkflowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowRepository extends JpaRepository<Workflow, UUID> {
    Optional<Workflow> findByName(String name);
    boolean existsByName(String name);
    Page<Workflow> findByStatus(WorkflowStatus status, Pageable pageable);
    Page<Workflow> findByCreatedBy(String createdBy, Pageable pageable);
}
