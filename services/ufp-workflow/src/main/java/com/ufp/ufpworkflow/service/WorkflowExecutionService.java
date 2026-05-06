package com.ufp.ufpworkflow.service;

import com.ufp.common.exception.UfpException;
import com.ufp.ufpworkflow.domain.entity.WorkflowExecution;
import com.ufp.ufpworkflow.domain.entity.WorkflowExecutionStep;
import com.ufp.ufpworkflow.domain.enums.ExecutionStatus;
import com.ufp.ufpworkflow.domain.enums.WorkflowStatus;
import com.ufp.ufpworkflow.domain.repository.WorkflowExecutionRepository;
import com.ufp.ufpworkflow.dto.request.TriggerExecutionRequest;
import com.ufp.ufpworkflow.dto.response.WorkflowExecutionResponse;
import com.ufp.ufpworkflow.event.WorkflowEvent;
import com.ufp.ufpworkflow.event.WorkflowEventPublisher;
import com.ufp.ufpworkflow.mapper.WorkflowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowExecutionService {

    private final WorkflowExecutionRepository executionRepository;
    private final WorkflowService workflowService;
    private final WorkflowEventPublisher eventPublisher;
    private final WorkflowMapper workflowMapper;

    @Transactional
    public WorkflowExecutionResponse trigger(UUID workflowId, TriggerExecutionRequest request, String triggeredBy) {
        var workflow = workflowService.findOrThrow(workflowId);

        if (workflow.getStatus() != WorkflowStatus.ACTIVE) {
            throw UfpException.badRequest("Workflow must be ACTIVE to trigger execution");
        }

        WorkflowExecution execution = WorkflowExecution.builder()
            .workflow(workflow)
            .triggeredBy(triggeredBy)
            .inputParams(request != null ? request.getInputParams() : null)
            .status(ExecutionStatus.RUNNING)
            .build();

        workflow.getSteps().forEach(step -> {
            WorkflowExecutionStep stepExec = WorkflowExecutionStep.builder()
                .execution(execution)
                .step(step)
                .status(ExecutionStatus.PENDING)
                .startedAt(Instant.now())
                .build();
            execution.getStepExecutions().add(stepExec);
        });

        WorkflowExecution saved = executionRepository.save(execution);

        eventPublisher.publish(WorkflowEvent.builder()
            .type(WorkflowEvent.Type.EXECUTION_STARTED)
            .executionId(saved.getId())
            .workflowId(workflow.getId())
            .workflowName(workflow.getName())
            .status(ExecutionStatus.RUNNING)
            .triggeredBy(triggeredBy)
            .build());

        log.info("Triggered execution id={} for workflow id={} by={}", saved.getId(), workflowId, triggeredBy);
        return workflowMapper.toExecutionResponse(saved);
    }

    @Transactional(readOnly = true)
    public WorkflowExecutionResponse getById(UUID executionId) {
        return workflowMapper.toExecutionResponse(findOrThrow(executionId));
    }

    @Transactional(readOnly = true)
    public Page<WorkflowExecutionResponse> listByWorkflow(UUID workflowId, Pageable pageable) {
        workflowService.findOrThrow(workflowId);
        return executionRepository.findByWorkflowId(workflowId, pageable)
            .map(workflowMapper::toExecutionResponse);
    }

    @Transactional
    public WorkflowExecutionResponse cancel(UUID executionId) {
        WorkflowExecution execution = findOrThrow(executionId);
        if (!execution.getStatus().equals(ExecutionStatus.RUNNING) &&
            !execution.getStatus().equals(ExecutionStatus.PENDING)) {
            throw UfpException.badRequest("Execution is not in a cancellable state: " + execution.getStatus());
        }
        execution.setStatus(ExecutionStatus.CANCELLED);
        execution.setCompletedAt(Instant.now());
        WorkflowExecution saved = executionRepository.save(execution);

        eventPublisher.publish(WorkflowEvent.builder()
            .type(WorkflowEvent.Type.EXECUTION_CANCELLED)
            .executionId(saved.getId())
            .workflowId(saved.getWorkflow().getId())
            .workflowName(saved.getWorkflow().getName())
            .status(ExecutionStatus.CANCELLED)
            .build());

        return workflowMapper.toExecutionResponse(saved);
    }

    private WorkflowExecution findOrThrow(UUID id) {
        return executionRepository.findById(id)
            .orElseThrow(() -> UfpException.notFound("Execution not found: " + id));
    }
}
