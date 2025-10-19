package com.smn.restapigenerator.util;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class Email {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an HTML email to the specified recipient.
     * @param to recipient email address
     * @param subject email subject
     * @param htmlBody HTML email body content
     * @throws Exception if sending fails
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true indicates HTML content
        helper.setFrom("testniesapi@gmail.com");
        
        mailSender.send(message);
    }

 }