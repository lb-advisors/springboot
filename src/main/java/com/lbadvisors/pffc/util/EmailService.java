package com.lbadvisors.pffc.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EnvironmentChecker environmentChecker;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        if (environmentChecker.isDev()) {
            message.setTo("oleblond@gmail.com");
        }

        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}
