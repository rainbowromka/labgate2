package ru.idc.labgatej.manager.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.idc.labgatej.manager.model.ERole;
import ru.idc.labgatej.manager.model.Role;

import java.util.Optional;

/**
 * Репозиторий ролей.
 */
public interface RoleRepository extends JpaRepository<Role, Long>
{
    /**
     * Поиск ролей по имени роли.
     *
     * @param name
     *        имя роли.
     * @return объект роли.
     */
    Optional<Role> findByName(ERole name);
}
