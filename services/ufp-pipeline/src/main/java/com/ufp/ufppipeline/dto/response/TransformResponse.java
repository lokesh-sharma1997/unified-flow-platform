package com.ufp.ufppipeline.dto.response;

import com.ufp.ufppipeline.domain.enums.TransformType;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class TransformResponse {
    private UUID id;
    private String name;
    private TransformType type;
    private Integer transformOrder;
    private Map<String, Object> config;
}
