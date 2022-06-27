package ru.portal.entities.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Value
@Builder
public class DtoSuccessAuthResponse {

    Integer status;

    String message;

    Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    OffsetDateTime timestamp;

}
