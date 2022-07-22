package ru.portal.mail;

import org.springframework.lang.NonNull;
import org.thymeleaf.context.Context;

import java.util.function.UnaryOperator;

/**
 * Интерфейс для отправки сообщений по электронной почте пользователю
 *
 * @author Федорышин К.В.
 */
public interface EmailService {

    /**
     * Метод для отправки сообщения по электронной почте.
     *
     * @param email электронный адрес получателя
     * @param title тема письма
     * @param message текст письма
     */
    void sendEmail(@NonNull String email, @NonNull String title, @NonNull String message);

    /**
     * Метод для получения шаблона письма
     *
     * @param titleMail название шаблона
     * @param variable контекст шаблона
     * @return html строку письма
     */
    String getHtmlMail(@NonNull String titleMail, @NonNull UnaryOperator<Context> variable);

    /**
     * Метод для получения шаблона письма
     *
     * @param titleMail название шаблона
     * @return html строку письма
     */
    String getHtmlMail(@NonNull String titleMail);
}
