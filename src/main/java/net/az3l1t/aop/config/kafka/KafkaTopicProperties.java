package net.az3l1t.aop.config.kafka;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
@Validated
public class KafkaTopicProperties {
    @NotNull
    private Map<String, TopicConfig> topics;

    @Data
    public static class TopicConfig {
        @NotBlank
        private String name;
        @Min(1)
        private int partitions;
        @Min(1)
        private short replicas;
        @NotNull
        private Map<String, String> configs;
    }
}
