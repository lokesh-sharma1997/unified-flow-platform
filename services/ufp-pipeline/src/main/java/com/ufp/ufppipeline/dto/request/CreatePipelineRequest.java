package com.ufp.ufppipeline.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreatePipelineRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    @Valid
    private ConnectorRequest source;

    @NotNull
    @Valid
    private ConnectorRequest sink;

    @Valid
    private List<TransformRequest> transforms = new ArrayList<>();
}
