package net.az3l1t.aop.config.notification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "notification.email")
public class NotificationProperties {
    private String sender;
    private String recipient;
    private String subjectUpdate;
    private String basicTextUpdate;
}
