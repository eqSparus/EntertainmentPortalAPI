package ru.portal.security.services;

import org.springframework.lang.NonNull;
import ru.portal.entities.dto.response.DtoSuccessAuthResponse;

public interface ConfirmationService {

    DtoSuccessAuthResponse confirmation(@NonNull String token);

}
