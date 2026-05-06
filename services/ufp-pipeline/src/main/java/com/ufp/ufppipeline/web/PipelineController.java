package com.ufp.ufppipeline.web;

import com.ufp.common.response.ApiResponse;
import com.ufp.ufppipeline.domain.enums.PipelineStatus;
import com.ufp.ufppipeline.dto.request.CreatePipelineRequest;
import com.ufp.ufppipeline.dto.response.PipelineResponse;
import com.ufp.ufppipeline.service.PipelineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pipelines")
@RequiredArgsConstructor
public class PipelineController {

    private final PipelineService pipelineService;

    @PostMapping
    public ResponseEntity<ApiResponse<PipelineResponse>> create(
            @Valid @RequestBody CreatePipelineRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(pipelineService.create(request, userId), "Pipeline created"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PipelineResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(pipelineService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PipelineResponse>>> list(
            @RequestParam(required = false) PipelineStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<PipelineResponse> page = status != null
            ? pipelineService.listByStatus(status, pageable)
            : pipelineService.list(pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PipelineResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(pipelineService.activate(id), "Pipeline activated"));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<PipelineResponse>> pause(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(pipelineService.pause(id), "Pipeline paused"));
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<ApiResponse<PipelineResponse>> stop(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(pipelineService.stop(id), "Pipeline stopped"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        pipelineService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Pipeline deleted"));
    }
}
