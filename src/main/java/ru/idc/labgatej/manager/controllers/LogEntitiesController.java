package ru.idc.labgatej.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.idc.labgatej.manager.model.LogEntity;
import ru.idc.labgatej.manager.services.LogEntitiesService;

/**
 * REST сервис журнала работы драйверов.
 */
@RestController
@RequestMapping("/api/logs")
public class LogEntitiesController
{
    /**
     * Сервис журнала.
     */
    LogEntitiesService logEntitiesService;

    /**
     * Создает контроллер журнала.
     *
     * @param logEntitiesService
     *        сервис журнала.
     */
    @Autowired
    public LogEntitiesController(
        LogEntitiesService logEntitiesService
    )
    {
        this.logEntitiesService = logEntitiesService;
    }

    @GetMapping("/pagedesc")
    public Page<LogEntity> getPageDesc(Pageable pageable, Long driverId)
    {
        return logEntitiesService.getPageDesc(pageable, driverId);
    }
}
