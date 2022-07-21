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
public class DtoFailedResponse {

    Integer status;

    String message;

    String path;

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    OffsetDateTime timestamp = OffsetDateTime.now();

}
