package ru.idc.labgatej.manager.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.repo.DriverEntityRepository;

import java.util.Optional;

/**
 * @author Roman Perminov
 * Контроллер веб-сервиса предоставляющего список драйверов в json-формате.
 */
@RestController
@RequestMapping("/services/drivers")
public class DriversController
{
    /**
     * Репозиторий списка драйверов.
     */
    final DriverEntityRepository driverEntityRepository;

    /**
     * Создание контроллера веб-сервиса предоставляющего список драйверов
     * в json-формате.
     * @param driverEntityRepository
     *        репозиторий списка драйверов.
     */
    public DriversController(DriverEntityRepository driverEntityRepository)
    {
        this.driverEntityRepository = driverEntityRepository;
    }

    /**
     * Получаем список драйверов.
     *
     * @param pageable
     *        Параметры страницы, которую нужно отобразить.
     * @return список драйверов.
     *
     */
    @GetMapping("list")
    public Page<DriverEntity> list(Pageable pageable)
    {
        return driverEntityRepository.findAll(pageable);
    }

    @GetMapping("/list/{id}")
    public Optional<DriverEntity> findById(@PathVariable Long id) {
        return driverEntityRepository.findById(id);
    }
}
