package ru.idc.labgatej.manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import ru.idc.labgatej.Manager;
import ru.idc.labgatej.manager.common.DbPoolService;
import ru.idc.labgatej.manager.common.RunningDriver;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.model.DriverStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final List<RunningDriver> runningListDrivers = new ArrayList<>();

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
        Optional<RunningDriver> driver = runningListDrivers.stream()
            .filter(a -> a.getDriverEntity().getId().equals(id))
            .findFirst();
        // Драйвер запущен, стопаем его.
        if (driver.isPresent()) {
            Manager runninDriver = driver.get().getDriverManager();
            runninDriver.stop();
            DriverEntity driverEntity = driver.get().getDriverEntity();
            driverEntity.setStatus(DriverStatus.STOP);
            driverEntityService.save(driverEntity);
            runningListDrivers.remove(driver.get());
            return DriverStatus.STOP;
        // Драйвер не запущен, запускаем его.
        } else {
            Optional<DriverEntity> optionalDriverEntity
                = driverEntityService.findById(id);
            Manager manager = null;

            if (optionalDriverEntity.isPresent()) {
                DriverEntity driverEntity = optionalDriverEntity.get();

                manager = new Manager(driverEntity, dbPoolService.getCpds(),
                    driverEntityService);

                runningListDrivers.add(new RunningDriver(manager, driverEntity));

                processDriverService.runDriver(manager);

                driverEntity.setStatus(DriverStatus.WORK);
                driverEntityService.save(driverEntity);
                return DriverStatus.WORK;
            }
            else return DriverStatus.STOP;
        }
    }
}
