package ru.portal.security.services;

import org.springframework.lang.NonNull;
import ru.portal.entities.Status;
import ru.portal.entities.User;
import ru.portal.entities.dto.request.DtoUserRequest;
import ru.portal.entities.dto.response.DtoAuthenticationResponse;
import ru.portal.entities.dto.response.DtoSuccessAuthResponse;
import ru.portal.security.services.exception.IncorrectCredentialsException;
import ru.portal.security.services.exception.UserBannedException;
import ru.portal.security.services.exception.UserExistsException;

public interface UserService {

    DtoSuccessAuthResponse registrationUser(@NonNull DtoUserRequest request) throws UserExistsException;

    @NonNull
    DtoAuthenticationResponse login(@NonNull DtoUserRequest request)
            throws IncorrectCredentialsException, UserBannedException;

    boolean isTimeBlock(@NonNull User user) throws IncorrectCredentialsException;

    void updateStatus(@NonNull Status status, @NonNull User user);

    void logout(@NonNull String refreshToken);

}
