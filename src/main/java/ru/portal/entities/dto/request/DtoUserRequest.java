package ru.portal.entities.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Value
public class DtoUserRequest {

    String username;
    String email;
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
