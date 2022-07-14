package ru.portal.security.utilities;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class RandomToken {

    private RandomToken() {

    }

    public static String getToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
