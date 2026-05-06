package com.ufp.ufppipeline.dto.response;

import com.ufp.ufppipeline.domain.enums.ConnectorType;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ConnectorResponse {
    private UUID id;
    private ConnectorType type;
    private Map<String, Object> config;
}
