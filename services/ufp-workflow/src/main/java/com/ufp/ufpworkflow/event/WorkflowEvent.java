package com.ufp.ufpworkflow.event;

import com.ufp.ufpworkflow.domain.enums.ExecutionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class WorkflowEvent {
    public enum Type { EXECUTION_STARTED, EXECUTION_SUCCEEDED, EXECUTION_FAILED, EXECUTION_CANCELLED }

    private Type type;
    private UUID executionId;
    private UUID workflowId;
    private String workflowName;
    private ExecutionStatus status;
    private String triggeredBy;
    @Builder.Default
    private Instant occurredAt = Instant.now();
    private String errorMessage;
}
