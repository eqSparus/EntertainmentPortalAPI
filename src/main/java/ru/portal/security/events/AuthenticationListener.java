package ru.portal.security.events;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.portal.entities.Status;
import ru.portal.entities.auth.ConfirmationToken;
import ru.portal.repositories.UserRepository;
import ru.portal.repositories.auth.ConfirmationTokenRepository;
import ru.portal.repositories.auth.LoginAttemptRepository;
import ru.portal.security.services.UserService;

import java.time.Instant;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
public class AuthenticationListener {

    @Value("${security.confirmation.lifetimeInSecond}")
    Long confirmationTime;

    @Value("${security.blocking.blockingPeriod}")
    Long blockingPeriod;

    @Value("${security.blocking.maxAttempts}")
    Integer maxAttempt;

    final UserRepository userRepository;
    final LoginAttemptRepository attemptRepository;
    final UserService userService;
    final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public AuthenticationListener(UserRepository userRepository,
                                  LoginAttemptRepository attemptRepository,
                                  UserService userService,
                                  ConfirmationTokenRepository confirmationTokenRepository) {
        this.userRepository = userRepository;
        this.attemptRepository = attemptRepository;
        this.userService = userService;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Transactional
    @EventListener(classes = AuthenticationFailureBadCredentialsEvent.class)
    public void onApplicationFailure(AuthenticationFailureBadCredentialsEvent event) {

        var username = (String) event.getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(username);

        log.info("{}", username);

        user.ifPresent(u -> {

            var attempt = attemptRepository.findByUser(u)
                    .orElseThrow(IllegalArgumentException::new);

            if (attempt.getNumberAttempt().equals(maxAttempt)) {
                attempt.setNumberAttempt(0);
                attempt.setLockTime(Instant.now().plusSeconds(blockingPeriod).toEpochMilli());
                u.setStatus(Status.BLOCK);
                userRepository.save(u);
            } else {
                attempt.setNumberAttempt(attempt.getNumberAttempt() + 1);
            }
            attemptRepository.save(attempt);
        });
    }


    @Transactional
    @EventListener(classes = AuthenticationSuccessEvent.class)
    public void onApplicationSuccess(AuthenticationSuccessEvent event) {

        var userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(IllegalArgumentException::new);

        var attempt = attemptRepository.findByUser(user)
                .orElseThrow(IllegalArgumentException::new);

        attempt.setNumberAttempt(0);
        attemptRepository.save(attempt);
    }

    @Transactional
    @EventListener(classes = LoginUserEvent.class)
    public void onApplicationLogin(LoginUserEvent event) {

        var request = event.getRequest();

        userRepository.findByUsername(request.getUsername())
                .ifPresent(u -> {
                    if (userService.isTimeBlock(u)) {
                        userService.updateStatus(Status.ACTIVE, u);
                    }
                });

    }

    @Transactional
    @EventListener(classes = RegistrationUserEvent.class)
    public void onApplicationEmail(RegistrationUserEvent event) {
        var user = event.getUser();

        var confirmationToken = ConfirmationToken.builder()
                .token(UUID.randomUUID().toString())
                .lifetime(Instant.now().plusSeconds(confirmationTime).toEpochMilli())
                .user(user)
                .build();
        log.info("Отправка токена {}", confirmationToken.getToken());
        confirmationTokenRepository.save(confirmationToken);
        //TODO отправка письма
    }
}
