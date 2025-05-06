package net.az3l1t.aop.service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.az3l1t.aop.dto.kafka.KafkaUpdatingDto;
import net.az3l1t.aop.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class TaskStatusConsumer {
    private final NotificationService notificationService;

    @Value("${kafka.topics.task-updating}")
    private String taskUpdatingTopic;

    @KafkaListener(
            topics = "${kafka.topics.task-updating}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenUpdating(List<KafkaUpdatingDto> messages, Acknowledgment acknowledgment) {
        log.info("Got batch of {} messages from topic {}", messages.size(), taskUpdatingTopic);
        for (KafkaUpdatingDto message : messages) {
            log.debug("Processing message: taskId={}, newStatus={}", message.taskId(), message.newStatus());
            try {
                notificationService.sendStatusUpdateNotification(message.taskId(), message.newStatus());
                log.debug("Successfully sent notification for taskId={}", message.taskId());
            } catch (Exception e) {
                log.error("Failed to send notification for taskId={}: {}", message.taskId(), e.getMessage());
                throw e;
            }
        }
        acknowledgment.acknowledge();
    }
}
