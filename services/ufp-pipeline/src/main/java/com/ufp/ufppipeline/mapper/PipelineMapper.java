package com.ufp.ufppipeline.mapper;

import com.ufp.ufppipeline.domain.entity.*;
import com.ufp.ufppipeline.dto.request.*;
import com.ufp.ufppipeline.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PipelineMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pipeline", ignore = true)
    PipelineSource toSourceEntity(ConnectorRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pipeline", ignore = true)
    PipelineSink toSinkEntity(ConnectorRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pipeline", ignore = true)
    PipelineTransform toTransformEntity(TransformRequest request);

    PipelineResponse toResponse(Pipeline pipeline);

    ConnectorResponse toSourceResponse(PipelineSource source);

    ConnectorResponse toSinkResponse(PipelineSink sink);

    TransformResponse toTransformResponse(PipelineTransform transform);

    @Mapping(target = "pipelineId", source = "pipeline.id")
    @Mapping(target = "pipelineName", source = "pipeline.name")
    PipelineRunResponse toRunResponse(PipelineRun run);
}
