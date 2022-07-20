package ru.portal.security.utilities;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomTokenTests {

    private static final Pattern regex = Pattern.compile("\\w{32}");

    @Test
    void testGetToken() {
        var token = RandomToken.getToken();
        assertTrue(token.matches(regex.toString()),
                "Токен должен соответствовать регулярному выражению");
    }

}
