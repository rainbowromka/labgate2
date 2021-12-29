package ru.idc.labgatej.manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.idc.labgatej.manager.model.User;

import java.util.Optional;

/**
 * Репозиторий пользователей.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    /**
     * Найти пользователя по имени.
     *
     * @param username
     *        имя пользователя.
     * @return контейнер, содержащий объект пользователя.
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверяет, существует ли пользователь в системе по имени.
     *
     * @param username
     *        имя пользователя.
     * @return признак существования пользователя.
     */
    Boolean existsByUsername(String username);

    /**
     * Проверяет, существует ли пользователь в системе по электронной почте.
     *
     * @param email
     *        электронная почта.
     * @return признак существования пользователя.
     */
    Boolean existsByEmail(String email);
}
