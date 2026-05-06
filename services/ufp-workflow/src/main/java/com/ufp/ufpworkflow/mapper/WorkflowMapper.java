package com.ufp.ufpworkflow.mapper;

import com.ufp.ufpworkflow.domain.entity.Workflow;
import com.ufp.ufpworkflow.domain.entity.WorkflowExecution;
import com.ufp.ufpworkflow.domain.entity.WorkflowStep;
import com.ufp.ufpworkflow.dto.request.CreateStepRequest;
import com.ufp.ufpworkflow.dto.request.CreateWorkflowRequest;
import com.ufp.ufpworkflow.dto.response.WorkflowExecutionResponse;
import com.ufp.ufpworkflow.dto.response.WorkflowResponse;
import com.ufp.ufpworkflow.dto.response.WorkflowStepResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", constant = "1")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "steps", ignore = true)
    Workflow toEntity(CreateWorkflowRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workflow", ignore = true)
    WorkflowStep toStepEntity(CreateStepRequest request);

    WorkflowResponse toResponse(Workflow workflow);

    WorkflowStepResponse toStepResponse(WorkflowStep step);

    @Mapping(target = "workflowId", source = "workflow.id")
    @Mapping(target = "workflowName", source = "workflow.name")
    WorkflowExecutionResponse toExecutionResponse(WorkflowExecution execution);
}
