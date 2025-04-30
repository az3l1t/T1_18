package net.az3l1t.aop.service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.az3l1t.aop.dto.kafka.TaskEventDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskConsumerService {

    @Value("${kafka.topics.task-events.name}")
    private String taskEventsTopic;

    @KafkaListener(topics = "${kafka.topics.task-events.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenTaskEvents(List<ConsumerRecord<String, TaskEventDto>> records, Acknowledgment ack) {
        try {
            log.info("Took batch of size: {} messages from topic {}", records.size(), taskEventsTopic);
            for (ConsumerRecord<String, TaskEventDto> record : records) {
                TaskEventDto taskEventDto = record.value();
                log.info("Processing message from topic {} with key {}: {}",
                        taskEventsTopic, record.key(), taskEventDto);
                processTaskEvent(taskEventDto);
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing message from topic {}: {}. It is going to DLQ", taskEventsTopic, e.getMessage());
            throw new RuntimeException("Failed to process task event", e);
        }
    }

    private void processTaskEvent(TaskEventDto event) {
        log.info("Processing task event: taskId={}", event.taskId());
    }
}
