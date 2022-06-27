package ru.portal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.portal.entities.User;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    @Override
    <S extends User> S save(@NonNull S entity);

    Optional<User> findByEmail(@NonNull String email);

    Optional<User> findByUsername(@NonNull String username);

    Optional<User> findByUsernameOrEmail(@NonNull String username, @NonNull String email);

    boolean existsByEmail(@NonNull String email);
}
