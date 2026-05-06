package com.ufp.ufpworkflow.dto.response;

import com.ufp.ufpworkflow.domain.enums.StepType;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class WorkflowStepResponse {
    private UUID id;
    private String name;
    private StepType type;
    private Integer stepOrder;
    private Map<String, Object> config;
    private Integer timeoutSeconds;
    private Integer retryCount;
    private String dependsOn;
}
