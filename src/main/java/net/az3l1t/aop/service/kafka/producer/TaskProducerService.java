package net.az3l1t.aop.service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.az3l1t.aop.dto.kafka.TaskEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.task-events.name}")
    private String taskEventsTopic;

    public void sendTaskEvent(TaskEventDto taskEvent) {
        try {
            kafkaTemplate.send(taskEventsTopic, taskEvent.taskId().toString(), taskEvent)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to send message: {} to topic: {} with key: {}",
                                    exception.getMessage(), taskEventsTopic, taskEvent.taskId());
                        } else {
                            log.info("Sent message: {} to topic: {}",
                                    taskEvent.taskId(), taskEventsTopic);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage());
        }
    }
}
