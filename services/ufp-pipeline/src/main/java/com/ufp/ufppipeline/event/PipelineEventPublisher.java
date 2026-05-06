package com.ufp.ufppipeline.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineEventPublisher {

    static final String TOPIC = "ufp.pipeline.events";

    private final KafkaTemplate<String, PipelineEvent> kafkaTemplate;

    public void publish(PipelineEvent event) {
        kafkaTemplate.send(TOPIC, event.getRunId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish pipeline event type={} runId={}", event.getType(), event.getRunId(), ex);
                } else {
                    log.debug("Published pipeline event type={} runId={}", event.getType(), event.getRunId());
                }
            });
    }
}
