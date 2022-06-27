package ru.portal.security.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import ru.portal.entities.dto.request.DtoUserRequest;

import java.time.Clock;

/**
 * Событие входа пользователя, хранит в себе тело запроса.
 * {@link DtoUserRequest}
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class LoginUserEvent extends ApplicationEvent {

    DtoUserRequest request;

    public LoginUserEvent(@NonNull Object source, @NonNull DtoUserRequest request) {
        super(source);
        this.request = request;
    }

    public LoginUserEvent(@NonNull Object source, @NonNull Clock clock, @NonNull DtoUserRequest request) {
        super(source, clock);
        this.request = request;
    }
}
