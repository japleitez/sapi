package com.peecko.api.service;

import com.peecko.api.service.context.EmailContext;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(EmailContext cxt) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(cxt.getFrom());
            helper.setTo(cxt.getTo());
            helper.setSubject(cxt.getSubject());
            helper.setText(cxt.getText(), true);
            if (StringUtils.hasText(cxt.getAttachmentPath())) {
                FileSystemResource file = new FileSystemResource(new File(cxt.getAttachmentPath()));
                helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
            }
            mailSender.send(message);
        } catch (Exception e) {
            CompletableFuture.completedFuture(false);
            return;
        }
        CompletableFuture.completedFuture(true);
    }
}
