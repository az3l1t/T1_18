package net.az3l1t.aop.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractTestcontainersConfig {
    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:15");
    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("apache/kafka:latest");

    @Container
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(POSTGRES_IMAGE)
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Container
    public static final KafkaContainer kafkaContainer = new KafkaContainer(KAFKA_IMAGE)
            .withExposedPorts(9092);

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("kafka.topics.task-updating", () -> "task-updating-topic");
    }
}
