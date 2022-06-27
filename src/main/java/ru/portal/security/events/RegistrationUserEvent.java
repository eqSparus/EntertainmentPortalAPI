package ru.portal.security.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;
import ru.portal.entities.User;

import java.time.Clock;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class RegistrationUserEvent extends ApplicationEvent {

    User user;

    public RegistrationUserEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public RegistrationUserEvent(Object source, Clock clock, User user) {
        super(source, clock);
        this.user = user;
    }
}
