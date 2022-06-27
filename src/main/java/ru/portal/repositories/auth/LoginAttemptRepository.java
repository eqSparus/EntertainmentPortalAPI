package ru.portal.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.portal.entities.User;
import ru.portal.entities.auth.LoginAttempt;

import java.util.Optional;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    @NonNull
    @Override
    <S extends LoginAttempt> S save(@NonNull S entity);

    Optional<LoginAttempt> findByUser(@NonNull User user);

}
