package com.ufp.ufpworkflow.service;

import com.ufp.common.exception.UfpException;
import com.ufp.ufpworkflow.domain.entity.Workflow;
import com.ufp.ufpworkflow.domain.entity.WorkflowStep;
import com.ufp.ufpworkflow.domain.enums.WorkflowStatus;
import com.ufp.ufpworkflow.domain.repository.WorkflowRepository;
import com.ufp.ufpworkflow.dto.request.CreateWorkflowRequest;
import com.ufp.ufpworkflow.dto.response.WorkflowResponse;
import com.ufp.ufpworkflow.mapper.WorkflowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;

    @Transactional
    public WorkflowResponse create(CreateWorkflowRequest request, String createdBy) {
        if (workflowRepository.existsByName(request.getName())) {
            throw UfpException.conflict("Workflow already exists with name: " + request.getName());
        }
        Workflow workflow = workflowMapper.toEntity(request);
        workflow.setCreatedBy(createdBy);

        request.getSteps().forEach(stepReq -> {
            WorkflowStep step = workflowMapper.toStepEntity(stepReq);
            step.setWorkflow(workflow);
            workflow.getSteps().add(step);
        });

        Workflow saved = workflowRepository.save(workflow);
        log.info("Created workflow id={} name={} by={}", saved.getId(), saved.getName(), createdBy);
        return workflowMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getById(UUID id) {
        return workflowMapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<WorkflowResponse> list(Pageable pageable) {
        return workflowRepository.findAll(pageable).map(workflowMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<WorkflowResponse> listByStatus(WorkflowStatus status, Pageable pageable) {
        return workflowRepository.findByStatus(status, pageable).map(workflowMapper::toResponse);
    }

    @Transactional
    public WorkflowResponse activate(UUID id) {
        Workflow workflow = findOrThrow(id);
        if (workflow.getSteps().isEmpty()) {
            throw UfpException.badRequest("Cannot activate a workflow with no steps");
        }
        workflow.setStatus(WorkflowStatus.ACTIVE);
        log.info("Activated workflow id={}", id);
        return workflowMapper.toResponse(workflowRepository.save(workflow));
    }

    @Transactional
    public WorkflowResponse pause(UUID id) {
        Workflow workflow = findOrThrow(id);
        if (workflow.getStatus() != WorkflowStatus.ACTIVE) {
            throw UfpException.badRequest("Only ACTIVE workflows can be paused");
        }
        workflow.setStatus(WorkflowStatus.PAUSED);
        return workflowMapper.toResponse(workflowRepository.save(workflow));
    }

    @Transactional
    public void delete(UUID id) {
        Workflow workflow = findOrThrow(id);
        if (workflow.getStatus() == WorkflowStatus.ACTIVE) {
            throw UfpException.badRequest("Cannot delete an ACTIVE workflow; pause it first");
        }
        workflowRepository.delete(workflow);
        log.info("Deleted workflow id={}", id);
    }

    Workflow findOrThrow(UUID id) {
        return workflowRepository.findById(id)
            .orElseThrow(() -> UfpException.notFound("Workflow not found: " + id));
    }
}
