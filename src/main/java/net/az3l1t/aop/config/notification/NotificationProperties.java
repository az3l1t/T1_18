package net.az3l1t.aop.config.notification;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@ConfigurationProperties(prefix = "notification.email")
public class NotificationProperties {
    private String sender;
    private String recipient;
    private String subjectUpdate;
    private String basicTextUpdate;
}
