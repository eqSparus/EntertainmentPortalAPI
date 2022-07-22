package ru.portal.security.details;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.portal.entities.Status;
import ru.portal.entities.User;

import java.util.Collection;
import java.util.Collections;

/**
 * Реализация {@link UserDetails} для хранения информации о пользователи.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UserDetailsImpl implements UserDetails {

    User user;

    private UserDetailsImpl(@NonNull User user) {
        this.user = user;
    }

    /**
     * Фабричный метод для создания {@link UserDetails}.
     *
     * @param user сущность пользователя.
     * @return класс с информацие о пользователи {@link UserDetails}.
     */
    public static UserDetails of(@NonNull User user) {
        return new UserDetailsImpl(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getStatus().equals(Status.BLOCK)
                && !user.getStatus().equals(Status.DELETE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !user.getStatus().equals(Status.AWAIT);
    }
}
