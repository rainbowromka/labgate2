package ru.idc.labgatej.manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.idc.labgatej.manager.model.DriverParameter;

/**
 * Репозиторий параметров экземпляра драйвера.
 */
public interface DriverParameterRepository
extends JpaRepository<DriverParameter, Long>
{
}
