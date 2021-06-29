package ru.idc.labgatej.manager.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.model.DriverParameter;
import ru.idc.labgatej.manager.repo.DriverParameterRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DriverParameterService {
    private final DriverParameterRepository driverParameterRepository;

    public DriverParameterService(
            DriverParameterRepository driverParameterService)
    {
        this.driverParameterRepository = driverParameterService;
    }

    @Transactional(rollbackFor = Exception.class)
    public DriverParameter save(DriverParameter driverParameter)
    {
        log.info("Saving {}", driverParameter);
        return driverParameterRepository.save(driverParameter);
    }

//    public Page<DriverEntity> getAllDriverEntitiesPaged(Pageable pageable)
//    {
//        return driverEntityRepository.findAll(pageable);
//    }
}
