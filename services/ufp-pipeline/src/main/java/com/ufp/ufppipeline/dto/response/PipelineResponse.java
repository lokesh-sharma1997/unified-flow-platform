package com.ufp.ufppipeline.dto.response;

import com.ufp.ufppipeline.domain.enums.PipelineStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class PipelineResponse {
    private UUID id;
    private String name;
    private String description;
    private PipelineStatus status;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private ConnectorResponse source;
    private ConnectorResponse sink;
    private List<TransformResponse> transforms;
}
