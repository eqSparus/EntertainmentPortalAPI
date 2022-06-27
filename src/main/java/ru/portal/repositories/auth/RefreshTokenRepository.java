package ru.portal.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.portal.entities.auth.RefreshToken;

import java.util.Optional;

/**
 * Репозиторий для взамодействия с таблицей токенов обновления в БД.
 * @author Федорышин К.В.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @NonNull
    @Override
    <S extends RefreshToken> S save(@NonNull S entity);

    /**
     * Извлекает токен обновления по токену обновления.
     * @param token токен обновления
     * @return токен обновления или {@link Optional#empty()}, если ничего не найдено.
     */
    Optional<RefreshToken> findByToken(@NonNull String token);

    /**
     * Удаляет токен обновления из БД по токеном обновления.
     * @param token токен обновления
     */
    void deleteByToken(@NonNull String token);

}
