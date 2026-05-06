package com.ufp.ufppipeline.domain.repository;

import com.ufp.ufppipeline.domain.entity.PipelineRun;
import com.ufp.ufppipeline.domain.enums.RunStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PipelineRunRepository extends JpaRepository<PipelineRun, UUID> {
    Page<PipelineRun> findByPipelineId(UUID pipelineId, Pageable pageable);
    Page<PipelineRun> findByStatus(RunStatus status, Pageable pageable);
    Optional<PipelineRun> findFirstByPipelineIdAndStatusOrderByStartedAtDesc(UUID pipelineId, RunStatus status);

    @Query("SELECT SUM(r.recordsIn) FROM PipelineRun r WHERE r.pipeline.id = :pipelineId")
    Long sumRecordsInByPipelineId(UUID pipelineId);
}
