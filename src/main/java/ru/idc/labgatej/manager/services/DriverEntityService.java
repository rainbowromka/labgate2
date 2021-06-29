package ru.idc.labgatej.manager.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.repo.DriverEntityRepository;

import java.util.List;


@Service
@Transactional(readOnly = true)
@Slf4j
public class DriverEntityService
{
    private final DriverEntityRepository driverEntityRepository;

    public DriverEntityService(
        DriverEntityRepository driverEntityRepository)
    {
        this.driverEntityRepository = driverEntityRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<DriverEntity> saveAll(List<DriverEntity> driverEntities)
    {
        log.info("Saving {}", driverEntities);
        return driverEntityRepository.saveAll(driverEntities);
    }

    public Page<DriverEntity> getAllDriverEntitiesPaged(Pageable pageable)
    {
        return driverEntityRepository.findAll(pageable);
    }
}
