package com.ufp.ufppipeline.event;

import com.ufp.ufppipeline.domain.enums.RunStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PipelineEvent {
    public enum Type { RUN_STARTED, RUN_SUCCEEDED, RUN_FAILED, RUN_CANCELLED }

    private Type type;
    private UUID runId;
    private UUID pipelineId;
    private String pipelineName;
    private RunStatus status;
    private String triggeredBy;
    private Long recordsIn;
    private Long recordsOut;
    @Builder.Default
    private Instant occurredAt = Instant.now();
    private String errorMessage;
}
