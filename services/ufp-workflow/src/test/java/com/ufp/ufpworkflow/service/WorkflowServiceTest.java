package com.ufp.ufpworkflow.service;

import com.ufp.common.exception.UfpException;
import com.ufp.ufpworkflow.domain.entity.Workflow;
import com.ufp.ufpworkflow.domain.enums.WorkflowStatus;
import com.ufp.ufpworkflow.domain.repository.WorkflowRepository;
import com.ufp.ufpworkflow.dto.request.CreateWorkflowRequest;
import com.ufp.ufpworkflow.dto.response.WorkflowResponse;
import com.ufp.ufpworkflow.mapper.WorkflowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    @Mock WorkflowRepository workflowRepository;
    @Mock WorkflowMapper workflowMapper;
    @InjectMocks WorkflowService workflowService;

    private CreateWorkflowRequest createRequest;
    private Workflow savedWorkflow;
    private WorkflowResponse expectedResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateWorkflowRequest();
        createRequest.setName("test-workflow");
        createRequest.setDescription("A test workflow");
        createRequest.setSteps(new ArrayList<>());

        savedWorkflow = Workflow.builder()
            .id(UUID.randomUUID())
            .name("test-workflow")
            .version(1)
            .status(WorkflowStatus.DRAFT)
            .createdBy("user-1")
            .steps(new ArrayList<>())
            .build();

        expectedResponse = new WorkflowResponse();
        expectedResponse.setId(savedWorkflow.getId());
        expectedResponse.setName("test-workflow");
        expectedResponse.setStatus(WorkflowStatus.DRAFT);
    }

    @Test
    void create_succeeds_when_name_is_unique() {
        when(workflowRepository.existsByName("test-workflow")).thenReturn(false);
        when(workflowMapper.toEntity(createRequest)).thenReturn(savedWorkflow);
        when(workflowRepository.save(any())).thenReturn(savedWorkflow);
        when(workflowMapper.toResponse(savedWorkflow)).thenReturn(expectedResponse);

        WorkflowResponse result = workflowService.create(createRequest, "user-1");

        assertThat(result.getName()).isEqualTo("test-workflow");
        assertThat(result.getStatus()).isEqualTo(WorkflowStatus.DRAFT);
        verify(workflowRepository).save(any());
    }

    @Test
    void create_throws_conflict_when_name_already_exists() {
        when(workflowRepository.existsByName("test-workflow")).thenReturn(true);

        assertThatThrownBy(() -> workflowService.create(createRequest, "user-1"))
            .isInstanceOf(UfpException.class)
            .hasMessageContaining("already exists");

        verify(workflowRepository, never()).save(any());
    }

    @Test
    void getById_throws_not_found_for_unknown_id() {
        UUID unknown = UUID.randomUUID();
        when(workflowRepository.findById(unknown)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workflowService.getById(unknown))
            .isInstanceOf(UfpException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void activate_throws_bad_request_when_no_steps() {
        savedWorkflow.setSteps(new ArrayList<>());
        when(workflowRepository.findById(savedWorkflow.getId())).thenReturn(Optional.of(savedWorkflow));

        assertThatThrownBy(() -> workflowService.activate(savedWorkflow.getId()))
            .isInstanceOf(UfpException.class)
            .hasMessageContaining("no steps");
    }

    @Test
    void delete_throws_bad_request_when_workflow_is_active() {
        savedWorkflow.setStatus(WorkflowStatus.ACTIVE);
        when(workflowRepository.findById(savedWorkflow.getId())).thenReturn(Optional.of(savedWorkflow));

        assertThatThrownBy(() -> workflowService.delete(savedWorkflow.getId()))
            .isInstanceOf(UfpException.class)
            .hasMessageContaining("Cannot delete");
    }
}
