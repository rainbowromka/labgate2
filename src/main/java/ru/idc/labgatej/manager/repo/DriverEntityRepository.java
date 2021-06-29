package ru.idc.labgatej.manager.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.idc.labgatej.manager.model.DriverEntity;

/**
 * Репозиторий экземпляров драйвера. Это те экземпляры драйвера, которые должны
 * быть запущены в памяти.
 */
public interface DriverEntityRepository
extends JpaRepository<DriverEntity, Long>
{
}
