package ru.portal.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.portal.entities.auth.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @NonNull
    @Override
    <S extends RefreshToken> S save(@NonNull S entity);

    Optional<RefreshToken> findByToken(@NonNull String token);

    void deleteByToken(@NonNull String token);

}
