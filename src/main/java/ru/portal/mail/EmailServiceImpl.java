package ru.portal.mail;


import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    String emailSender;

    final JavaMailSender javaMailSender;
    final ITemplateEngine templateEngine;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender,
                            ITemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Метод для отправки сообщения по электронной почте.
     *
     * @param email электронный адрес получателя
     * @param title тема письма
     * @param message текст письма
     */
    @Async
    @Override
    public void sendEmail(@NonNull String email, @NonNull String title, @NonNull String message) {
        try {
            var messageBody = javaMailSender.createMimeMessage();
            var helper = new MimeMessageHelper(messageBody, true, StandardCharsets.UTF_8.toString());

            helper.setFrom(emailSender);
            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(message, true);
            javaMailSender.send(messageBody);
        } catch (MessagingException e) {
            //TODO Обработать случай с несуществующим адресом
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод для получения контекста письма
     *
     * @param titleMail название шаблона
     * @param variable контекст шаблона
     * @return шаблон строки письма в html
     */
    @Override
    public String getHtmlMail(@NonNull String titleMail, @NonNull Supplier<Context> variable) {
        return templateEngine.process(titleMail, variable.get());
    }

    /**
     * Метод для получения контекста письма
     *
     * @param titleMail название шаблона
     * @return шаблон строки письма в html
     */
    @Override
    public String getHtmlMail(@NonNull String titleMail) {
        return getHtmlMail(titleMail, Context::new);
    }


}
