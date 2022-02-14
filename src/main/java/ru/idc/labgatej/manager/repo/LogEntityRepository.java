package ru.idc.labgatej.manager.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.idc.labgatej.manager.model.LogEntity;

public interface LogEntityRepository
extends JpaRepository<LogEntity, Long>
{
    /**
     * Возвращает количество записей в журнале для конкретного драйвера.
     *
     * @param driverId
     *        id драйвера.
     * @return количество записей в журнале для конкретного драйвера.
     */
    Long countByDriverInstance(
        Long driverId);

    /**
     * Получает список записей ввиде страницы для конкретного драйвера. Записи
     * отсортированы в порядке их появления.
     *
     * @param pageable
     *        параметры страницы.
     * @param driverId
     *        id драйвера.
     * @return список записей журнала в зависиомсти от параметров страницы.
     */
    Page<LogEntity> getLogByDriverInstanceOrderByLogDateAsc(
        Pageable pageable,
        Long driverId);

    /**
     * Получает список записей ввиде страницы для конкретного драйвера. Записи
     * отсортированы в обратном порядке их появления.
     *
     * @param pageable
     *        параметры страницы.
     * @param driverId
     *        id драйвера.
     * @return список записей журнала в зависиомсти от параметров страницы.
     */
    Page<LogEntity> getLogByDriverInstanceOrderByLogDateDesc(
        Pageable pageable,
        Long driverId);
}
