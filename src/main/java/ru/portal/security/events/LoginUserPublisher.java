package ru.portal.security.events;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.portal.entities.User;
import ru.portal.entities.dto.request.DtoUserRequest;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class LoginUserPublisher implements AuthenticationPublisher {

    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public LoginUserPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publishEvent(@NonNull DtoUserRequest request) {
        applicationEventPublisher.publishEvent(new LoginUserEvent(this, request));
    }

    @Override
    public void publishEvent(@NonNull User user) {
        applicationEventPublisher.publishEvent(new RegistrationUserEvent(this, user));
    }

}
