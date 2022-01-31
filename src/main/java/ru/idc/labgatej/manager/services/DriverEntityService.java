package ru.idc.labgatej.manager.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.idc.labgatej.base.IConfiguration;
import ru.idc.labgatej.base.ISendClientMessages;
import ru.idc.labgatej.manager.config.WebSocketConfiguration;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.repo.DriverEntityRepository;

import java.util.List;
import java.util.Optional;


/**
 * Сервис работы с конфигурациями экземпляров драйверов.
 *
 * @author Roman Perminov
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class DriverEntityService
implements ISendClientMessages
{
    /**
     * Репозиторий конфигураций экземпляров драйверов.
     */
    private final DriverEntityRepository driverEntityRepository;

    /**
     * Соединение с клиентским приложением.
     */
    private final SimpMessagingTemplate websocket;

    /**
     * Создает сервис работы с конфигурациями экземпляров драйверов.
     *
     * @param driverEntityRepository
     *        репозиторий конфигураций экземпляров драйверов.
     * @param websocket
     */
    public DriverEntityService(
        DriverEntityRepository driverEntityRepository,
        SimpMessagingTemplate websocket)
    {
        this.driverEntityRepository = driverEntityRepository;
        this.websocket = websocket;
    }

    /**
     * Сохраняет список конфигураций экземпляров драйверов.
     *
     * @param driverEntities
     *        список конфигураций экземпляров драйверов.
     * @return список сохраненных конфигураций экземпляров драйверов.
     */
    @Transactional(rollbackFor = Exception.class)
    public List<DriverEntity> saveAll(List<DriverEntity> driverEntities)
    {
        log.info("Saving {}", driverEntities);
        return driverEntityRepository.saveAll(driverEntities);
    }

    /**
     * Получает список конфигураций экземпляров драйверов с постраничной
     * разбивкой.
     *
     * @param pageable
     *        параметры постраничной разбивки.
     * @return список конфигураций экземпляров драйверов с постраничной
     * разбивкой.
     */
    public Page<DriverEntity> getAllDriverEntitiesPaged(Pageable pageable)
    {
        return driverEntityRepository.findAll(pageable);
    }

    /**
     * Сохраняет конфигурацию экземпляра драйвера.
     *
     * @param driverEntity
     *        конфигурация экземпляра драйвера.
     */
    @Transactional(readOnly = false)
    public void save(
        DriverEntity driverEntity)
    {
        driverEntityRepository.saveAndFlush(driverEntity);
    }

    /**
     * Находит конфигурацию экземпляра драйвера
     *
     * @param id
     *        id конфигурации экземпляра драйвера.
     * @return конфигурацию экземпляра драйвера.
     */
    public Optional<DriverEntity> findById(
        Long id)
    {
        return driverEntityRepository.findById(id);
    }

    @Override
    public void sendDriverIsRunning(IConfiguration config)
    {
        websocket.convertAndSend(WebSocketConfiguration.MESSAGE_PREFIX + "/runStopDriver", config);
    }

    @Override
    public void sendDriverIsStopped(IConfiguration config)
    {
        websocket.convertAndSend(WebSocketConfiguration.MESSAGE_PREFIX + "/runStopDriver", config);
    }
}
