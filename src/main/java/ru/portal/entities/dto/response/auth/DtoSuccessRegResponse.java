package ru.portal.entities.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DtoSuccessRegResponse {

    Integer status;

    String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    OffsetDateTime timestamp = OffsetDateTime.now();

}
