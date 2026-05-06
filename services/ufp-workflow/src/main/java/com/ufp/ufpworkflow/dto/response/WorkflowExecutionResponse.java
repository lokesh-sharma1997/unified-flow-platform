package com.ufp.ufpworkflow.dto.response;

import com.ufp.ufpworkflow.domain.enums.ExecutionStatus;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class WorkflowExecutionResponse {
    private UUID id;
    private UUID workflowId;
    private String workflowName;
    private ExecutionStatus status;
    private String triggeredBy;
    private Map<String, Object> inputParams;
    private Map<String, Object> outputParams;
    private String errorMessage;
    private Instant startedAt;
    private Instant completedAt;
}
