package com.ufp.ufppipeline.service;

import com.ufp.common.exception.UfpException;
import com.ufp.ufppipeline.domain.entity.Pipeline;
import com.ufp.ufppipeline.domain.entity.PipelineRun;
import com.ufp.ufppipeline.domain.enums.PipelineStatus;
import com.ufp.ufppipeline.domain.enums.RunStatus;
import com.ufp.ufppipeline.domain.repository.PipelineRunRepository;
import com.ufp.ufppipeline.dto.response.PipelineRunResponse;
import com.ufp.ufppipeline.event.PipelineEvent;
import com.ufp.ufppipeline.event.PipelineEventPublisher;
import com.ufp.ufppipeline.mapper.PipelineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineRunService {

    private final PipelineRunRepository runRepository;
    private final PipelineService pipelineService;
    private final PipelineEventPublisher eventPublisher;
    private final PipelineMapper pipelineMapper;

    @Transactional
    public PipelineRunResponse start(UUID pipelineId, String triggeredBy) {
        Pipeline pipeline = pipelineService.findOrThrow(pipelineId);

        if (pipeline.getStatus() != PipelineStatus.ACTIVE) {
            throw UfpException.badRequest("Pipeline must be ACTIVE to start a run");
        }

        // Only one active run per pipeline
        runRepository.findFirstByPipelineIdAndStatusOrderByStartedAtDesc(pipelineId, RunStatus.RUNNING)
            .ifPresent(r -> { throw UfpException.conflict("Pipeline already has an active run: " + r.getId()); });

        PipelineRun run = PipelineRun.builder()
            .pipeline(pipeline)
            .triggeredBy(triggeredBy)
            .status(RunStatus.RUNNING)
            .build();

        PipelineRun saved = runRepository.save(run);

        eventPublisher.publish(PipelineEvent.builder()
            .type(PipelineEvent.Type.RUN_STARTED)
            .runId(saved.getId())
            .pipelineId(pipeline.getId())
            .pipelineName(pipeline.getName())
            .status(RunStatus.RUNNING)
            .triggeredBy(triggeredBy)
            .build());

        log.info("Started run id={} for pipeline id={} by={}", saved.getId(), pipelineId, triggeredBy);
        return pipelineMapper.toRunResponse(saved);
    }

    @Transactional(readOnly = true)
    public PipelineRunResponse getById(UUID runId) {
        return pipelineMapper.toRunResponse(findOrThrow(runId));
    }

    @Transactional(readOnly = true)
    public Page<PipelineRunResponse> listByPipeline(UUID pipelineId, Pageable pageable) {
        pipelineService.findOrThrow(pipelineId);
        return runRepository.findByPipelineId(pipelineId, pageable).map(pipelineMapper::toRunResponse);
    }

    @Transactional
    public PipelineRunResponse cancel(UUID runId) {
        PipelineRun run = findOrThrow(runId);
        if (run.getStatus() != RunStatus.RUNNING && run.getStatus() != RunStatus.STARTING) {
            throw UfpException.badRequest("Run is not in a cancellable state: " + run.getStatus());
        }
        run.setStatus(RunStatus.CANCELLED);
        run.setCompletedAt(Instant.now());
        PipelineRun saved = runRepository.save(run);

        eventPublisher.publish(PipelineEvent.builder()
            .type(PipelineEvent.Type.RUN_CANCELLED)
            .runId(saved.getId())
            .pipelineId(saved.getPipeline().getId())
            .pipelineName(saved.getPipeline().getName())
            .status(RunStatus.CANCELLED)
            .recordsIn(saved.getRecordsIn())
            .recordsOut(saved.getRecordsOut())
            .build());

        return pipelineMapper.toRunResponse(saved);
    }

    private PipelineRun findOrThrow(UUID id) {
        return runRepository.findById(id)
            .orElseThrow(() -> UfpException.notFound("Pipeline run not found: " + id));
    }
}
