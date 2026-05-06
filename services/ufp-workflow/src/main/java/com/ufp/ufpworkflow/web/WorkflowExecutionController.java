package com.ufp.ufpworkflow.web;

import com.ufp.common.response.ApiResponse;
import com.ufp.ufpworkflow.dto.request.TriggerExecutionRequest;
import com.ufp.ufpworkflow.dto.response.WorkflowExecutionResponse;
import com.ufp.ufpworkflow.service.WorkflowExecutionService;
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
public class WorkflowExecutionController {

    private final WorkflowExecutionService executionService;

    @PostMapping("/workflows/{workflowId}/executions")
    public ResponseEntity<ApiResponse<WorkflowExecutionResponse>> trigger(
            @PathVariable UUID workflowId,
            @RequestBody(required = false) TriggerExecutionRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "system") String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(executionService.trigger(workflowId, request, userId), "Execution triggered"));
    }

    @GetMapping("/executions/{executionId}")
    public ResponseEntity<ApiResponse<WorkflowExecutionResponse>> getById(@PathVariable UUID executionId) {
        return ResponseEntity.ok(ApiResponse.ok(executionService.getById(executionId)));
    }

    @GetMapping("/workflows/{workflowId}/executions")
    public ResponseEntity<ApiResponse<Page<WorkflowExecutionResponse>>> listByWorkflow(
            @PathVariable UUID workflowId,
            @PageableDefault(size = 20, sort = "startedAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(executionService.listByWorkflow(workflowId, pageable)));
    }

    @PostMapping("/executions/{executionId}/cancel")
    public ResponseEntity<ApiResponse<WorkflowExecutionResponse>> cancel(@PathVariable UUID executionId) {
        return ResponseEntity.ok(ApiResponse.ok(executionService.cancel(executionId), "Execution cancelled"));
    }
}
