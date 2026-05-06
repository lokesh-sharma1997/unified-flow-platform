package com.ufp.ufppipeline.service;

import com.ufp.common.exception.UfpException;
import com.ufp.ufppipeline.domain.entity.Pipeline;
import com.ufp.ufppipeline.domain.entity.PipelineSink;
import com.ufp.ufppipeline.domain.entity.PipelineSource;
import com.ufp.ufppipeline.domain.enums.ConnectorType;
import com.ufp.ufppipeline.domain.enums.PipelineStatus;
import com.ufp.ufppipeline.domain.repository.PipelineRepository;
import com.ufp.ufppipeline.dto.request.ConnectorRequest;
import com.ufp.ufppipeline.dto.request.CreatePipelineRequest;
import com.ufp.ufppipeline.dto.response.PipelineResponse;
import com.ufp.ufppipeline.mapper.PipelineMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PipelineServiceTest {

    @Mock PipelineRepository pipelineRepository;
    @Mock PipelineMapper pipelineMapper;
    @InjectMocks PipelineService pipelineService;

    private CreatePipelineRequest createRequest;
    private Pipeline savedPipeline;
    private PipelineResponse expectedResponse;

    @BeforeEach
    void setUp() {
        ConnectorRequest sourceReq = new ConnectorRequest();
        sourceReq.setType(ConnectorType.KAFKA);
        sourceReq.setConfig(Map.of("topic", "input-topic"));

        ConnectorRequest sinkReq = new ConnectorRequest();
        sinkReq.setType(ConnectorType.POSTGRESQL);
        sinkReq.setConfig(Map.of("table", "events"));

        createRequest = new CreatePipelineRequest();
        createRequest.setName("test-pipeline");
        createRequest.setDescription("A test pipeline");
        createRequest.setSource(sourceReq);
        createRequest.setSink(sinkReq);
        createRequest.setTransforms(new ArrayList<>());

        PipelineSource source = PipelineSource.builder().type(ConnectorType.KAFKA).config(Map.of("topic", "input-topic")).build();
        PipelineSink sink = PipelineSink.builder().type(ConnectorType.POSTGRESQL).config(Map.of("table", "events")).build();

        savedPipeline = Pipeline.builder()
            .id(UUID.randomUUID())
            .name("test-pipeline")
            .status(PipelineStatus.DRAFT)
            .createdBy("user-1")
            .source(source)
            .sink(sink)
            .transforms(new ArrayList<>())
            .build();

        expectedResponse = new PipelineResponse();
        expectedResponse.setId(savedPipeline.getId());
        expectedResponse.setName("test-pipeline");
        expectedResponse.setStatus(PipelineStatus.DRAFT);
    }

    @Test
    void create_succeeds_when_name_is_unique() {
        when(pipelineRepository.existsByName("test-pipeline")).thenReturn(false);
        when(pipelineMapper.toSourceEntity(any())).thenReturn(savedPipeline.getSource());
        when(pipelineMapper.toSinkEntity(any())).thenReturn(savedPipeline.getSink());
        when(pipelineRepository.save(any())).thenReturn(savedPipeline);
        when(pipelineMapper.toResponse(savedPipeline)).thenReturn(expectedResponse);

        PipelineResponse result = pipelineService.create(createRequest, "user-1");

        assertThat(result.getName()).isEqualTo("test-pipeline");
        assertThat(result.getStatus()).isEqualTo(PipelineStatus.DRAFT);
        verify(pipelineRepository).save(any());
    }

    @Test
    void create_throws_conflict_when_name_exists() {
        when(pipelineRepository.existsByName("test-pipeline")).thenReturn(true);

        assertThatThrownBy(() -> pipelineService.create(createRequest, "user-1"))
            .isInstanceOf(UfpException.class)
            .hasMessageContaining("already exists");

        verify(pipelineRepository, never()).save(any());
    }

    @Test
    void getById_throws_not_found_for_unknown_id() {
        UUID unknown = UUID.randomUUID();
        when(pipelineRepository.findById(unknown)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pipelineService.getById(unknown))
            .isInstanceOf(UfpException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void activate_succeeds_when_source_and_sink_present() {
        when(pipelineRepository.findById(savedPipeline.getId())).thenReturn(Optional.of(savedPipeline));
        when(pipelineRepository.save(any())).thenReturn(savedPipeline);
        when(pipelineMapper.toResponse(any())).thenReturn(expectedResponse);

        pipelineService.activate(savedPipeline.getId());

        assertThat(savedPipeline.getStatus()).isEqualTo(PipelineStatus.ACTIVE);
    }

    @Test
    void delete_throws_bad_request_when_pipeline_is_active() {
        savedPipeline.setStatus(PipelineStatus.ACTIVE);
        when(pipelineRepository.findById(savedPipeline.getId())).thenReturn(Optional.of(savedPipeline));

        assertThatThrownBy(() -> pipelineService.delete(savedPipeline.getId()))
            .isInstanceOf(UfpException.class)
            .hasMessageContaining("Cannot delete");
    }

    @Test
    void pause_throws_bad_request_when_not_active() {
        savedPipeline.setStatus(PipelineStatus.DRAFT);
        when(pipelineRepository.findById(savedPipeline.getId())).thenReturn(Optional.of(savedPipeline));

        assertThatThrownBy(() -> pipelineService.pause(savedPipeline.getId()))
            .isInstanceOf(UfpException.class)
            .hasMessageContaining("Only ACTIVE");
    }
}
