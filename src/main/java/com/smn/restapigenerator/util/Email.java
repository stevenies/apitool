package com.smn.restapigenerator.util;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

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

    /**
     * Sends a welcome HTML email to a new user.
     * @param userEmail the user's email address
     * @param userName the user's first name
     */
    public void sendWelcomeEmail(String userEmail, String userName) {
        try {
            String subject = "Welcome to REST API Generator";
            String resourcePath = "templates/welcome.html";
            ClassPathResource resource = new ClassPathResource(resourcePath);
            String htmlBody = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            htmlBody = String.format(htmlBody, userName);
            this.sendHtmlEmail(userEmail, subject, htmlBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}