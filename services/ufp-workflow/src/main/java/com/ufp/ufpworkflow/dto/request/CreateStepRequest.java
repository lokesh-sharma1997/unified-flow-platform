package com.ufp.ufpworkflow.dto.request;

import com.ufp.ufpworkflow.domain.enums.StepType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class CreateStepRequest {
    @NotBlank
    private String name;

    @NotNull
    private StepType type;

    @NotNull
    @Min(1)
    private Integer stepOrder;

    private Map<String, Object> config;
    private Integer timeoutSeconds;
    private Integer retryCount;
    private String dependsOn;
}
