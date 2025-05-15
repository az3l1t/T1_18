package net.az3l1t.aop.config.pool;

import lombok.RequiredArgsConstructor;
import net.az3l1t.aop.config.notification.AsyncNotificationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {
    private final AsyncNotificationProperties asyncNotificationProperties;

    @Bean("notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncNotificationProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncNotificationProperties.getMaxPoolSize());
        executor.setQueueCapacity(asyncNotificationProperties.getQueueCapacity());
        executor.setThreadNamePrefix(asyncNotificationProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
