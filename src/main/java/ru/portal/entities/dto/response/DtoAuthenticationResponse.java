package ru.portal.entities.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import ru.portal.entities.Role;
import ru.portal.entities.dto.Views;

import java.time.ZonedDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Value
@Builder
public class DtoAuthenticationResponse {

    @JsonView(Views.RefreshToken.class)
    String authorization;

    @JsonView(Views.RefreshToken.class)
    String refreshToken;

    @JsonView(Views.RefreshToken.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    ZonedDateTime timestamp;

    @JsonView(Views.Login.class)
    String username;

    @JsonView(Views.Login.class)
    String email;

    @JsonView(Views.Login.class)
    Role role;

    @JsonView(Views.Login.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    ZonedDateTime createAt;

    @JsonView(Views.Login.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    ZonedDateTime updateAt;

}
