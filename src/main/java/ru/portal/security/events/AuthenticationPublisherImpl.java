package ru.portal.security.events;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.portal.entities.User;
import ru.portal.entities.dto.request.auth.DtoUserRequest;

/**
 * Издает события связаные с регистрацией и авторизацией пользователя
 * Издает события {@link  RegistrationUserEvent} и {@link LoginUserEvent}.
 *
 * @author Федорышин К.В.
 * @see ru.portal.security.events.AuthenticationPublisher
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class AuthenticationPublisherImpl implements AuthenticationPublisher {

    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AuthenticationPublisherImpl(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Издает события до авторизации пользователя для проверки исходных данных.
     *
     * @param request тело запроса авторизации.
     */
    @Override
    public void publishEventLogin(@NonNull DtoUserRequest request) {
        applicationEventPublisher.publishEvent(new LoginUserEvent(this, request));
    }

    /**
     * Издает события после регистрации пользователя.
     *
     * @param user зарегистрированный пользователь.
     */
    @Override
    public void publishEventRegistration(@NonNull User user) {
        applicationEventPublisher.publishEvent(new RegistrationUserEvent(this, user));
    }

}
