package com.ufp.ufppipeline.web;

import com.ufp.common.response.ApiResponse;
import com.ufp.ufppipeline.dto.response.PipelineRunResponse;
import com.ufp.ufppipeline.service.PipelineRunService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PipelineRunController {

    private final PipelineRunService runService;

    @PostMapping("/pipelines/{pipelineId}/runs")
    public ResponseEntity<ApiResponse<PipelineRunResponse>> start(
            @PathVariable UUID pipelineId,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(runService.start(pipelineId, userId), "Pipeline run started"));
    }

    @GetMapping("/runs/{runId}")
    public ResponseEntity<ApiResponse<PipelineRunResponse>> getById(@PathVariable UUID runId) {
        return ResponseEntity.ok(ApiResponse.ok(runService.getById(runId)));
    }

    @GetMapping("/pipelines/{pipelineId}/runs")
    public ResponseEntity<ApiResponse<Page<PipelineRunResponse>>> listByPipeline(
            @PathVariable UUID pipelineId,
            @PageableDefault(size = 20, sort = "startedAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(runService.listByPipeline(pipelineId, pageable)));
    }

    @PostMapping("/runs/{runId}/cancel")
    public ResponseEntity<ApiResponse<PipelineRunResponse>> cancel(@PathVariable UUID runId) {
        return ResponseEntity.ok(ApiResponse.ok(runService.cancel(runId), "Run cancelled"));
    }
}
