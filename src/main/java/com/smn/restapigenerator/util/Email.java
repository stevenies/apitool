package com.smn.restapigenerator.util;

import org.springframework.beans.factory.annotation.Autowired;
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
     * @param accessToken the user's access token
     */
    public void sendWelcomeHtmlEmail(String userEmail, String userName, String accessToken) {
        try {
            String subject = "Welcome to REST API Generator";
            String htmlBody = String.format(
                "<html><body>" +
                "<h2>Welcome to REST API Generator!</h2>" +
                "<p>Hello <strong>%s</strong>,</p>" +
                "<p>Welcome to the REST API Generator tool!</p>" +
                "<p>Your access token is: <code>%s</code></p>" +
                "<p>You can now start generating API specifications and code.</p>" +
                "<br>" +
                "<p>Best regards,<br>" +
                "<em>REST API Generator Team</em></p>" +
                "</body></html>",
                userName, accessToken
            );
            sendHtmlEmail(userEmail, subject, htmlBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}