package ru.portal.security.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import ru.portal.entities.User;

import java.time.Clock;

/**
 * Событие регистрации пользователя, хранит в себе
 * сущность пользователя {@link User}.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class RegistrationUserEvent extends ApplicationEvent {

    User user;

    public RegistrationUserEvent(@NonNull Object source, @NonNull User user) {
        super(source);
        this.user = user;
    }

    public RegistrationUserEvent(@NonNull Object source, @NonNull Clock clock, @NonNull User user) {
        super(source, clock);
        this.user = user;
    }
}
