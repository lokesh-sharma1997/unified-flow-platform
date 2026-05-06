package com.ufp.ufppipeline.dto.response;

import com.ufp.ufppipeline.domain.enums.RunStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class PipelineRunResponse {
    private UUID id;
    private UUID pipelineId;
    private String pipelineName;
    private RunStatus status;
    private String triggeredBy;
    private Long recordsIn;
    private Long recordsOut;
    private Long recordsFailed;
    private String errorMessage;
    private Instant startedAt;
    private Instant completedAt;
}
