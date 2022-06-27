package ru.portal.repositories.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.portal.entities.auth.ConfirmationToken;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    @NonNull
    @Override
    <S extends ConfirmationToken> S save(@NonNull S entity);

    Optional<ConfirmationToken> findByToken(@NonNull String token);

    void deleteByToken(@NonNull String token);
}
