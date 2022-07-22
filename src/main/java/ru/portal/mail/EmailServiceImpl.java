package ru.portal.mail;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;

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
     * @param email   электронный адрес получателя
     * @param title   тема письма
     * @param message текст письма
     */
    @Async
    @Override
    public void sendEmail(@NonNull String email, @NonNull String title, @NonNull String message) {
        try {
            javaMailSender.send(mimeMessage -> {
                var helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
                helper.setFrom(emailSender);
                helper.setTo(email);
                helper.setSubject(title);
                helper.setText(message, true);
            });
        } catch (MailSendException e) {
            //TODO Обработать случай с несуществующим адресом
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод для получения контекста письма
     *
     * @param titleMail название шаблона
     * @param variable  контекст шаблона
     * @return шаблон строки письма в html
     */
    @Override
    public String getHtmlMail(@NonNull String titleMail, @NonNull UnaryOperator<Context> variable) {
        return templateEngine.process(titleMail, variable.apply(new Context()));
    }

    /**
     * Метод для получения контекста письма
     *
     * @param titleMail название шаблона
     * @return шаблон строки письма в html
     */
    @Override
    public String getHtmlMail(@NonNull String titleMail) {
        return getHtmlMail(titleMail, context -> context);
    }


}
