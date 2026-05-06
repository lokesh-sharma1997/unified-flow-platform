package com.ufp.ufpworkflow.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowEventPublisher {

    static final String TOPIC = "ufp.workflow.events";

    private final KafkaTemplate<String, WorkflowEvent> kafkaTemplate;

    public void publish(WorkflowEvent event) {
        kafkaTemplate.send(TOPIC, event.getExecutionId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish workflow event type={} executionId={}", event.getType(), event.getExecutionId(), ex);
                } else {
                    log.debug("Published workflow event type={} executionId={}", event.getType(), event.getExecutionId());
                }
            });
    }
}
