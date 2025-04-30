package net.az3l1t.aop.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TopicConfig {
    private final KafkaTopicProperties kafkaTopicProperties;

    @Bean
    public List<NewTopic> kafkaTopics(KafkaAdmin kafkaAdmin) {
        List<NewTopic> topics =  kafkaTopicProperties.getTopics().values().stream()
                .map(topic -> {
                    log.info("Creating topic: {} with {} partitions and {} replicas",
                            topic.getName(), topic.getPartitions(), topic.getReplicas());
                    return new NewTopic(topic.getName(), topic.getPartitions(), topic.getReplicas())
                            .configs(topic.getConfigs());
                }).toList();

        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            adminClient.createTopics(topics).all().get(10, TimeUnit.SECONDS);
            log.info("Successfully created topics: {}", topics.stream().map(NewTopic::name).toList());
        } catch (Exception e) {
            log.error("Failed to create topics", e);
        }

        log.info("Total topics created: {}", topics.size());
        return topics;
    }
}
