package com.ufp.ufppipeline.dto.request;

import com.ufp.ufppipeline.domain.enums.TransformType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class TransformRequest {
    @NotBlank
    private String name;

    @NotNull
    private TransformType type;

    @NotNull
    @Min(1)
    private Integer transformOrder;

    private Map<String, Object> config;
}
