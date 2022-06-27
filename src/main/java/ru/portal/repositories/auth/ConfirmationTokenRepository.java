package ru.portal.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.portal.entities.auth.ConfirmationToken;

import java.util.Optional;

/**
 * Репозиторий для взамодействия с таблицей токеном подтверждения регистрации в БД.
 * @author Федорышин К.В.
 */
@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    @NonNull
    @Override
    <S extends ConfirmationToken> S save(@NonNull S entity);

    /**
     * Извлекает токен подтверждения регистрации из хранилища данных.
     * @param token токен подтверждения.
     * @return токен подтверждения с заданым токеном
     * или {@link Optional#empty()}, если ничего не найдено.
     */
    Optional<ConfirmationToken> findByToken(@NonNull String token);

    /**
     * Удаляет токен подтверждения регистрации из
     * хранилищя.
     * @param token токен подтверждения.
     */
    void deleteByToken(@NonNull String token);
}
