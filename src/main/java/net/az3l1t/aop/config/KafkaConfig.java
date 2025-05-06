package net.az3l1t.aop.config;

import lombok.extern.log4j.Log4j2;
import net.az3l1t.aop.dto.kafka.KafkaUpdatingDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Log4j2
public class KafkaConfig {
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${kafka.consumer.group-id}")
    private String groupId;
    @Value("${kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value("${kafka.consumer.max-partition-fetch-bytes}")
    private String maxPartitionFetchBytes;
    @Value("${kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value("${kafka.consumer.max-poll-interval-ms}")
    private String maxPollIntervalMs;
    @Value("${kafka.consumer.trusted-packages}")
    private String trustedPackages;
    @Value("${kafka.producer.retries}")
    private String retries;
    @Value("${kafka.listener.concurrency}")
    private String concurrency;
    @Value("${kafka.listener.ack-mode}")
    private String ackMode;
    @Value("${kafka.listener.backoff-interval}")
    private long backoffInterval;
    @Value("${kafka.listener.backoff-max-attempts}")
    private long backoffMaxAttempts;
    @Value("${kafka.consumer.fetch-min-bytes}")
    private String fetchMinBytes;
    @Value("${kafka.consumer.fetch-max-wait-ms}")
    private String fetchMaxWaitMs;

    @Bean
    public ProducerFactory<String, KafkaUpdatingDto> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.parseInt(retries));
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean("taskUpdatingKafkaTemplate")
    public KafkaTemplate<String, KafkaUpdatingDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, KafkaUpdatingDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Integer.parseInt(maxPollRecords));
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, Integer.parseInt(maxPartitionFetchBytes));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Integer.parseInt(maxPollIntervalMs));
        props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, Integer.parseInt(fetchMinBytes));
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, Integer.parseInt(fetchMaxWaitMs));
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaUpdatingDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaUpdatingDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.valueOf(ackMode));
        factory.setBatchListener(true);
        factory.setConcurrency(Integer.parseInt(concurrency));
        return factory;
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new FixedBackOff(backoffInterval, backoffMaxAttempts)
        );
        errorHandler.addNotRetryableExceptions(IllegalStateException.class);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("Retrying. message = {}, offset = {}, attempt = {}",
                    ex.getMessage(), record.offset(), deliveryAttempt);
        });
        return errorHandler;
    }
}
