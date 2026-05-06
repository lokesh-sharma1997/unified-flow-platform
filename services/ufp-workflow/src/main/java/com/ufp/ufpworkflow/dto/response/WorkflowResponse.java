package com.ufp.ufpworkflow.dto.response;

import com.ufp.ufpworkflow.domain.enums.WorkflowStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class WorkflowResponse {
    private UUID id;
    private String name;
    private String description;
    private Integer version;
    private WorkflowStatus status;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private List<WorkflowStepResponse> steps;
}
