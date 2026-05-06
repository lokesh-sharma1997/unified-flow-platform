package com.ufp.ufppipeline.service;

import com.ufp.common.exception.UfpException;
import com.ufp.ufppipeline.domain.entity.Pipeline;
import com.ufp.ufppipeline.domain.entity.PipelineSink;
import com.ufp.ufppipeline.domain.entity.PipelineSource;
import com.ufp.ufppipeline.domain.entity.PipelineTransform;
import com.ufp.ufppipeline.domain.enums.PipelineStatus;
import com.ufp.ufppipeline.domain.repository.PipelineRepository;
import com.ufp.ufppipeline.dto.request.CreatePipelineRequest;
import com.ufp.ufppipeline.dto.response.PipelineResponse;
import com.ufp.ufppipeline.mapper.PipelineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineMapper pipelineMapper;

    @Transactional
    public PipelineResponse create(CreatePipelineRequest request, String createdBy) {
        if (pipelineRepository.existsByName(request.getName())) {
            throw UfpException.conflict("Pipeline already exists with name: " + request.getName());
        }

        Pipeline pipeline = Pipeline.builder()
            .name(request.getName())
            .description(request.getDescription())
            .createdBy(createdBy)
            .build();

        PipelineSource source = pipelineMapper.toSourceEntity(request.getSource());
        source.setPipeline(pipeline);
        pipeline.setSource(source);

        PipelineSink sink = pipelineMapper.toSinkEntity(request.getSink());
        sink.setPipeline(pipeline);
        pipeline.setSink(sink);

        request.getTransforms().forEach(t -> {
            PipelineTransform transform = pipelineMapper.toTransformEntity(t);
            transform.setPipeline(pipeline);
            pipeline.getTransforms().add(transform);
        });

        Pipeline saved = pipelineRepository.save(pipeline);
        log.info("Created pipeline id={} name={} by={}", saved.getId(), saved.getName(), createdBy);
        return pipelineMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PipelineResponse getById(UUID id) {
        return pipelineMapper.toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<PipelineResponse> list(Pageable pageable) {
        return pipelineRepository.findAll(pageable).map(pipelineMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PipelineResponse> listByStatus(PipelineStatus status, Pageable pageable) {
        return pipelineRepository.findByStatus(status, pageable).map(pipelineMapper::toResponse);
    }

    @Transactional
    public PipelineResponse activate(UUID id) {
        Pipeline pipeline = findOrThrow(id);
        if (pipeline.getSource() == null || pipeline.getSink() == null) {
            throw UfpException.badRequest("Pipeline must have a source and sink before activation");
        }
        pipeline.setStatus(PipelineStatus.ACTIVE);
        log.info("Activated pipeline id={}", id);
        return pipelineMapper.toResponse(pipelineRepository.save(pipeline));
    }

    @Transactional
    public PipelineResponse pause(UUID id) {
        Pipeline pipeline = findOrThrow(id);
        if (pipeline.getStatus() != PipelineStatus.ACTIVE) {
            throw UfpException.badRequest("Only ACTIVE pipelines can be paused");
        }
        pipeline.setStatus(PipelineStatus.PAUSED);
        return pipelineMapper.toResponse(pipelineRepository.save(pipeline));
    }

    @Transactional
    public PipelineResponse stop(UUID id) {
        Pipeline pipeline = findOrThrow(id);
        if (pipeline.getStatus() == PipelineStatus.STOPPED || pipeline.getStatus() == PipelineStatus.DRAFT) {
            throw UfpException.badRequest("Pipeline is already stopped or in DRAFT state");
        }
        pipeline.setStatus(PipelineStatus.STOPPED);
        return pipelineMapper.toResponse(pipelineRepository.save(pipeline));
    }

    @Transactional
    public void delete(UUID id) {
        Pipeline pipeline = findOrThrow(id);
        if (pipeline.getStatus() == PipelineStatus.ACTIVE) {
            throw UfpException.badRequest("Cannot delete a running pipeline; stop it first");
        }
        pipelineRepository.delete(pipeline);
        log.info("Deleted pipeline id={}", id);
    }

    Pipeline findOrThrow(UUID id) {
        return pipelineRepository.findById(id)
            .orElseThrow(() -> UfpException.notFound("Pipeline not found: " + id));
    }
}
