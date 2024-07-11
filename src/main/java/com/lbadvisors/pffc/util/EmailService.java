package com.lbadvisors.pffc.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine htmlTemplateEngine;
    private final TemplateEngine textTemplateEngine;

    @Autowired
    EnvironmentChecker environmentChecker;

    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.mailSender = emailSender;
        this.htmlTemplateEngine = createHtmlTemplateEngine(templateEngine);
        this.textTemplateEngine = createTextTemplateEngine(templateEngine);
    }

    public void sendEmail(String to, Map<String, Object> templateModel) {
        try {

            String subject = "Your order has been received";

            if (environmentChecker.isDev() || environmentChecker.isLocal()) {
                subject = "TESTING - ignore this email - " + subject;
            }

            // Process the HTML template into a String
            String htmlContent = htmlTemplateEngine.process("order-email-template",
                    new Context(null, templateModel));

            // Process the text template into a String
            String textContent = textTemplateEngine.process("order-email-template",
                    new Context(null, templateModel));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(textContent, htmlContent);
            helper.addInline("logo", new ClassPathResource("static/logo.png"));

            mailSender.send(message);

        } catch (MailException | MessagingException ex) {
            logger.error("Cannot send email", ex);
        }
    }

    private TemplateEngine createHtmlTemplateEngine(TemplateEngine templateEngine) {
        return createTemplateEngine(templateEngine, TemplateMode.HTML, ".html");
    }

    private TemplateEngine createTextTemplateEngine(TemplateEngine templateEngine) {
        return createTemplateEngine(templateEngine, TemplateMode.TEXT, ".txt");
    }

    private TemplateEngine createTemplateEngine(TemplateEngine templateEngine, TemplateMode templateMode,
            String suffix) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(templateMode);
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(suffix);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);

        return engine;
    }
}
