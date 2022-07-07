package ru.portal.entities.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Тело запроса при регистрации и авторизации пользователя.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Value
public class DtoUserRequest {


    @Size(max = 32, message = "Имя не должно быть больше 32 символов")
    @NotBlank(message = "Имя не должно быть пустым!")
    String username;

    @Size(max = 60,message = "Электронный адрес не должен быть больше 60 символов")
    @Email(message = "Это не адрес электронной почты!")
    String email;

    @Size(min = 8, max = 60, message = "Пароль не должен быть меньше 8 и больше 60 символов")
    @NotBlank(message = "Пароль не долежн быть пустым")
    String password;

    @JsonCreator
    public DtoUserRequest(
            @JsonProperty(value = "username", required = true) String username,
            @JsonProperty(value = "email") String email,
            @JsonProperty(value = "password", required = true) String password
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}
