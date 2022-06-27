package ru.portal.security.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;
import ru.portal.entities.dto.request.DtoUserRequest;

import java.time.Clock;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class LoginUserEvent extends ApplicationEvent {

    DtoUserRequest request;

    public LoginUserEvent(Object source, DtoUserRequest request) {
        super(source);
        this.request = request;
    }

    public LoginUserEvent(Object source, Clock clock, DtoUserRequest request) {
        super(source, clock);
        this.request = request;
    }
}
