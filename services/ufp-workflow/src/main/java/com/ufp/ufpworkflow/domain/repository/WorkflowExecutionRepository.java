package com.ufp.ufpworkflow.domain.repository;

import com.ufp.ufpworkflow.domain.entity.WorkflowExecution;
import com.ufp.ufpworkflow.domain.enums.ExecutionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, UUID> {
    Page<WorkflowExecution> findByWorkflowId(UUID workflowId, Pageable pageable);
    Page<WorkflowExecution> findByStatus(ExecutionStatus status, Pageable pageable);
    long countByWorkflowIdAndStatus(UUID workflowId, ExecutionStatus status);
}
