package net.az3l1t.aop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {
    private final JavaMailSender mailSender;

    @Value("${notification.email.sender}")
    private String sender;

    @Value("${notification.email.recipient}")
    private String recipient;

    @Value("${notification.email.subject-update}")
    private String subject;

    public void sendStatusUpdateNotification(Long taskId, String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setFrom(sender);
        message.setSubject(subject);
        message.setText("Task Id: " + taskId + " has changed status: " + status);

        try {
            mailSender.send(message);
            log.info("Successfully sent email notification for taskId={} to {}", taskId, recipient);
        } catch (MailException e) {
            log.error("Failed to send email notification for taskId={}: {}", taskId, e.getMessage());
        }
    }
}
