package ru.idc.labgatej.manager.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.model.DriverStatus;
import ru.idc.labgatej.manager.repo.DriverEntityRepository;
import ru.idc.labgatej.manager.services.RunningDriverManager;

import java.util.Optional;

/**
 * Веб-сервис конфигураций экземпляров драйверов.
 *
 * @author Roman Perminov
 */
@RestController
@RequestMapping("/services/drivers")
public class DriversController
{
    /**
     * Репозиторий списка конфигураций экземпляров драйверов.
     */
    final DriverEntityRepository driverEntityRepository;

    /**
     * Менеджер запущенных драйверов.
     */
    final RunningDriverManager runningDriverManager;

    /**
     * Создает веб-сервис  конфигураций экземпляров драйверов.
     *
     * @param driverEntityRepository
     *        репозиторий списка драйверов.
     * @param runningDriverManager
     *        менеджер запущенных драйверов.
     */
    public DriversController(
        DriverEntityRepository driverEntityRepository,
        RunningDriverManager runningDriverManager)
    {
        this.driverEntityRepository = driverEntityRepository;
        this.runningDriverManager = runningDriverManager;
    }

    /**
     * Получает список конфигураций экземпляров драйверов.
     *
     * @param pageable
     *        Параметры получаемой страницы.
     * @return список конфигураций экземпляров драйверов.
     *
     */
    @GetMapping("list")
    public Page<DriverEntity> list(Pageable pageable)
    {
        return driverEntityRepository.findAll(pageable);
    }

    /**
     * Находит конфигурацию экземпляра драйвера.
     *
     * @param id
     *        id конфигурации экземпляра драйвера.
     * @return контейнер содержащий конфигурацию экземпляра драйвера.
     */
    @GetMapping("/list/{id}")
    public Optional<DriverEntity> findById(@PathVariable Long id) {
        return driverEntityRepository.findById(id);
    }

    /**
     * Запускает/останавливает экземпляр драйвера.
     *
     * @param id
     *        id конфигурации экземпляра драйвера.
     * @return статус по окончанию работы драйвера.
     */
    @GetMapping("runStopDriver/{id}")
    public DriverStatus runStopDriver(@PathVariable Long id)
    {
        return runningDriverManager.runStopDriver(id);
    }
}
