package ru.portal.security.details;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.portal.repositories.UserRepository;

/**
 * Сервис для извлечения пользователя из хранилища и предоставления SpringSecurity
 * {@link UserDetails}
 *
 * @author Федорышин К.В.
 * @see ru.portal.security.details.UserDetailsImpl
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserDetailsImpl.of(userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Такого пользователя не существует!")));
    }
}
