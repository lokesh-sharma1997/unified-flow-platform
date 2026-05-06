package com.ufp.ufpworkflow.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class TriggerExecutionRequest {
    private Map<String, Object> inputParams;
}
