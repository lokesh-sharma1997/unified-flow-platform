package com.ufp.ufppipeline.domain.entity;

import com.ufp.ufppipeline.domain.enums.RunStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pipeline_runs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PipelineRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false)
    private Pipeline pipeline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RunStatus status = RunStatus.STARTING;

    @Column(name = "triggered_by")
    private String triggeredBy;

    @Column(name = "records_in")
    @Builder.Default
    private Long recordsIn = 0L;

    @Column(name = "records_out")
    @Builder.Default
    private Long recordsOut = 0L;

    @Column(name = "records_failed")
    @Builder.Default
    private Long recordsFailed = 0L;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
