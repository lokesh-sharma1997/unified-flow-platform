package com.ufp.ufppipeline.dto.request;

import com.ufp.ufppipeline.domain.enums.ConnectorType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ConnectorRequest {
    @NotNull
    private ConnectorType type;

    @NotNull
    @NotEmpty
    private Map<String, Object> config;
}
