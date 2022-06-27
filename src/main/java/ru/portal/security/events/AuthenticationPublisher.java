package ru.portal.security.events;

import org.springframework.lang.NonNull;
import ru.portal.entities.User;
import ru.portal.entities.dto.request.DtoUserRequest;

public interface AuthenticationPublisher {

    void publishEvent(@NonNull DtoUserRequest request);

    void publishEvent(@NonNull User user);

}
