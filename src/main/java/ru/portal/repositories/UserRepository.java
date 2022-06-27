package ru.portal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.portal.entities.User;

import java.util.Optional;


/**
 * Репозиторий для взамодействия с таблицей пользователя в БД.
 * @author Федорышин К.В.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    @Override
    <S extends User> S save(@NonNull S entity);



    /**
     * Извлекает пользователя из БД по его адресу электронному адресу.
     * @param email электронный адрес пользователя.
     * @return пользователя с заданым электронным адресом
     * или {@link Optional#empty()}, если ничего не найдено.
     */
    Optional<User> findByEmail(@NonNull String email);

    /**
     * Извлекает пользователя из БД по его имени.
     * @param username имя пользователя.
     * @return пользователя с заданым именем или
     * {@link Optional#empty()}, если ничего не найдено.
     */
    Optional<User> findByUsername(@NonNull String username);

    /**
     * Извлекает пользвателя по имени и адресу электронной почты.
     * @param username имя пользователя.
     * @param email электронный адрес пользователя.
     * @return пользователя с заданым электронным адресом и именем пользователя
     * или {@link Optional#empty()}, если ничего не найдено.
     */
    Optional<User> findByUsernameOrEmail(@NonNull String username, @NonNull String email);

    /**
     * Проверка содержит ли хранилище данных пользователя
     * с заданым электронным адресом.
     * @param email электронный адрес пользователя.
     * @return Значение true если хранилище содержит пользователя,
     * false если нет.
     */
    boolean existsByEmail(@NonNull String email);

}
