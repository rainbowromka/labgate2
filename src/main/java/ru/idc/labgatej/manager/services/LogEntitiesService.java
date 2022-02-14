package ru.idc.labgatej.manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.idc.labgatej.manager.model.LogEntity;
import ru.idc.labgatej.manager.repo.LogEntityRepository;

import java.util.List;

/**
 * Сервис работы с журналом.
 */
@Service
@Transactional(readOnly = true)
public class LogEntitiesService
{
    /**
     * Репозиторий журнала.
     */
    LogEntityRepository logEntityRepository;

    /**
     * Создает сервис работы с журналом.
     *
     * @param logEntityRepository
     *        репозиторий журнала.
     */
    @Autowired
    public LogEntitiesService(
        LogEntityRepository logEntityRepository)
    {
        this.logEntityRepository = logEntityRepository;
    }

    /**
     * Возвращает количество записей в журнале для конкретного драйвера.
     *
     * @param driverId
     *        id драйвера.
     * @return количество записей в журнале для конкретного драйвера.
     */
    public Long countLogEntities(
        Long driverId)
    {
        return logEntityRepository.countByDriverInstance(driverId);
    }

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
    public Page<LogEntity> getPageAsc(
        Pageable pageable,
        Long driverId)
    {
        return logEntityRepository
            .getLogByDriverInstanceOrderByLogDateAsc(pageable, driverId);
    }

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
    public Page<LogEntity> getPageDesc(
        Pageable pageable,
        Long driverId)
    {
        return logEntityRepository
            .getLogByDriverInstanceOrderByLogDateDesc(pageable, driverId);
    }

    /**
     * Сохраняет запись журнала.
     * @param logEntity
     *        запись журнала.
     */
    @Transactional(readOnly = false)
    public void save(
        LogEntity logEntity)
    {
        logEntityRepository.save(logEntity);
    }

    /**
     * Сохраняет список записей журнала.
     *
     * @param logEntities
     *        список записей журнала.
     */
    @Transactional(readOnly = false)
    public void saveAll(
        List<LogEntity> logEntities)
    {
        logEntityRepository.saveAll(logEntities);
    }
}
