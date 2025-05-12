package net.az3l1t.aop.service;

import lombok.RequiredArgsConstructor;
import net.az3l1t.aop.config.notification.NotificationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;

    @Async("notificationTaskExecutor")
    public void sendStatusUpdateNotification(Long taskId, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationProperties.getRecipient());
        message.setFrom(notificationProperties.getSender());
        message.setSubject(notificationProperties.getSubjectUpdate());
        message.setText(buildNotificationMessage(taskId, status, notificationProperties.getBasicTextUpdate()));
        mailSender.send(message);
    }

    private String buildNotificationMessage(Long taskId, String status, String basicText) {
        return String.format(basicText + " %s, %d", status, taskId);
    }
}
