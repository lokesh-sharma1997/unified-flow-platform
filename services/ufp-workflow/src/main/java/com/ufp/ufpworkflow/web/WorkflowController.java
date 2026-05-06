package com.ufp.ufpworkflow.web;

import com.ufp.common.response.ApiResponse;
import com.ufp.ufpworkflow.domain.enums.WorkflowStatus;
import com.ufp.ufpworkflow.dto.request.CreateWorkflowRequest;
import com.ufp.ufpworkflow.dto.response.WorkflowResponse;
import com.ufp.ufpworkflow.service.WorkflowService;
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
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<ApiResponse<WorkflowResponse>> create(
            @Valid @RequestBody CreateWorkflowRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(workflowService.create(request, userId), "Workflow created"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkflowResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WorkflowResponse>>> list(
            @RequestParam(required = false) WorkflowStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<WorkflowResponse> page = status != null
            ? workflowService.listByStatus(status, pageable)
            : workflowService.list(pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<WorkflowResponse>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowService.activate(id), "Workflow activated"));
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<ApiResponse<WorkflowResponse>> pause(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(workflowService.pause(id), "Workflow paused"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        workflowService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Workflow deleted"));
    }
}
