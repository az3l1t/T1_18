package net.az3l1t.aop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.az3l1t.aop.config.notification.NotificationProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {
    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;

    public void sendStatusUpdateNotification(Long taskId, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notificationProperties.getRecipient());
        message.setFrom(notificationProperties.getSender());
        message.setSubject(notificationProperties.getSubjectUpdate());
        message.setText(buildNotificationMessage(taskId, status, notificationProperties.getBasicTextUpdate()));
        mailSender.send(message);
    }

    private String buildNotificationMessage(Long taskId, String status, String basicText) {
        return String.format(basicText + " %s, %s", status, taskId);
    }
}
