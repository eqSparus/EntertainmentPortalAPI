package ru.portal.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.portal.entities.User;
import ru.portal.entities.auth.LoginAttempt;

import java.util.Optional;

/**
 * Репозиторий для взамодействия с таблицей попыток входа пользователя в БД.
 * @author Федорышин К.В.
 */
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    @NonNull
    @Override
    <S extends LoginAttempt> S save(@NonNull S entity);

    /**
     * Извлекает попытки входа пользователя по пользователю.
     * @param user пользователь.
     * @return попытки входа или {@link Optional#empty()}, если ничего не найдено.
     */
    Optional<LoginAttempt> findByUser(@NonNull User user);

}
