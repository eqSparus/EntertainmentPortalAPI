package ru.portal.entities.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Value
@Builder
public class DtoAuthenticationResponse {

    String authorization;

    String refreshToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    ZonedDateTime timestamp = ZonedDateTime.now();

}
