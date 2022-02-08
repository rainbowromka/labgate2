package ru.idc.labgatej.manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import ru.idc.labgatej.Manager;
import ru.idc.labgatej.manager.common.DbPoolService;
import ru.idc.labgatej.manager.common.RunningDriver;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.base.DriverStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Менеджера списка работающих драйверов.
 *
 * @author Roman Perminov
 */
@Service
@ApplicationScope
public class RunningDriverManager
{
    /**
     * Список запущенных драйверов.
     */
    private final Map<Long, RunningDriver> runningListDrivers = new TreeMap<>();

    /**
     * Запускаемый асинхронный процесс.
     */
    private final ProcessDriverService processDriverService;

    /**
     * Сервис, содержащий конфигурации экземпляров драйверов.
     */
    private DriverEntityService driverEntityService;

    /**
     * Сервис доступа к пулу базе данных KRYPTON для записи результатов
     * исследований.
     */
    private DbPoolService dbPoolService;

    /**
     * Конструктор менеджера списка рабочих драйверов.
     *
     * @param driverEntityService
     *        сервис справочных данных Драйвера.
     * @param dbPoolService
     *        сервис доступа к пулу базы данных KRYPTON.
     * @param processDriverService
     *        сервис асинхронного запуска драйвера.
     */
    @Autowired
    public RunningDriverManager(
            DriverEntityService driverEntityService,
            DbPoolService dbPoolService,
            ProcessDriverService processDriverService)
    {
        this.driverEntityService = driverEntityService;
        this.dbPoolService = dbPoolService;
        this.processDriverService = processDriverService;
    }

    /**
     * Запускает/останавливает экземпляр драйвера.
     *
     * @param id
     *        id - драйвера.
     * @return возвращает статус драйвера.
     */
    public DriverStatus runStopDriver(
        Long id)
    {
        RunningDriver driver = runningListDrivers.get(id);
        // Драйвер запущен, стопаем его.
        if (driver != null) {
            Manager runninDriver = driver.getDriverManager();
            driver.setStatus(DriverStatus.STOPPING);
            runninDriver.stop();
            runningListDrivers.remove(id);
            return DriverStatus.STOPPING;
        // Драйвер не запущен, запускаем его.
        } else {
            Optional<DriverEntity> optionalDriverEntity
                = driverEntityService.findById(id);
            Manager manager = null;

            if (optionalDriverEntity.isPresent()) {
                DriverEntity driverEntity = optionalDriverEntity.get();

                manager = new Manager(driverEntity, dbPoolService.getCpds(),
                    driverEntityService);

                runningListDrivers.put(driverEntity.getDriverId(),
                    new RunningDriver(manager, driverEntity));

                processDriverService.runDriver(manager);

                driverEntity.setStatus(DriverStatus.STARTING);
//                driverEntityService.save(driverEntity);
                return DriverStatus.STARTING;
            }
            else return DriverStatus.STOP;
        }
    }

    /**
     * Возвращает статус драйвера. Находит по
     * @param driverEntity
     * @return
     */
    public DriverStatus getDriverStatus(DriverEntity driverEntity)
    {
        RunningDriver runningDriver =
            runningListDrivers.get(driverEntity.getDriverId());

        if (runningDriver != null)
        {
            return runningDriver.getStatus();
        }
        else
        {
            return DriverStatus.STOP;
        }
    }
}
