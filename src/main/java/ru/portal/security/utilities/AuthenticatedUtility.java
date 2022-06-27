package ru.portal.security.utilities;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

/**
 * Разные вспомогательные методы для аутентификации пользователя.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticatedUtility {

    private AuthenticatedUtility() {

    }

    /**
     * Проверяет аутентифицирован ли пользователь.
     *
     * @return true если пользователь аутентифицирован, false
     * если нет.
     */
    public static boolean isAuthenticated() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    /**
     * Добавляет авторизированного пользователя в контекст SpringSecurity
     *
     * @param user авторизированый {@link UserDetails}
     */
    public static void authentication(@NonNull UserDetails user) {
        var authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}
