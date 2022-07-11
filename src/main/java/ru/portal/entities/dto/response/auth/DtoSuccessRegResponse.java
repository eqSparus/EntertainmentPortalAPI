package ru.portal.entities.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Value
@Builder
public class DtoSuccessRegResponse {

    Integer status;

    String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    OffsetDateTime timestamp = OffsetDateTime.now();

}
