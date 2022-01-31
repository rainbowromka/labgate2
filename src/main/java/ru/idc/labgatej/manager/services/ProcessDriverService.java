package ru.idc.labgatej.manager.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.idc.labgatej.Manager;

/**
 * Сервис асинхронного запуска экземпляра драйвера.
 */
@Service
public class ProcessDriverService
{
    /**
     * Асинхронно запускает менеджер драйвера, который в свою очередь создает,
     * конфигурирует и запускает экземпляр драйвера. Менеджер следит за
     * состоянием драйвера, посылает ему команды управления.
     * @param manager
     *        заранее созданный объект менеджера, в который передана
     *        конфигурация экземпляра драйвера, пул доступа к базе данных.
     */
    @Async
    public void runDriver(
        Manager manager)
    {
        try
        {
            manager.runManager();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
