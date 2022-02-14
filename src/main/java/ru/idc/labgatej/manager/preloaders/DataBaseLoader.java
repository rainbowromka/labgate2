package ru.idc.labgatej.manager.preloaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.idc.labgatej.manager.controllers.AuthController;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.model.DriverParameter;
import ru.idc.labgatej.base.DriverStatus;
import ru.idc.labgatej.manager.model.DriverType;
import ru.idc.labgatej.manager.model.LogEntity;
import ru.idc.labgatej.manager.payload.request.SignupRequest;
import ru.idc.labgatej.manager.services.DriverEntityService;
import ru.idc.labgatej.manager.services.LogEntitiesService;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

/**
 * Класс, загружающий в базу данных предварительные данные.
 */
@Component
public class DataBaseLoader
implements CommandLineRunner
{
    /**
     * Отключаемая опция - выключает добавление тестовых данных.
     */
    private static final boolean isAddValues = true;

    /**
     * Сервис доступа к конфигурации экземпляров драйверов.
     */
    private final DriverEntityService driverEntityService;

    /**
     * Сервис авторизации пользователя.
     */
    private final AuthController authController;

    /**
     * Сервис работы с журналом логгирования.
     */
    private final LogEntitiesService logEntitiesService;

    /**
     * Создает класс, загружающий тестовые данные в базу данных.
     *
     * @param driverEntityService
     *        сервис доступа к конфигурации экземпляров драйверов.
     * @param authController
     *        сервис авторизации пользователя.
     */
    @Autowired
    public DataBaseLoader(
        DriverEntityService driverEntityService,
        AuthController authController,
        LogEntitiesService logEntitiesService)
    {
        this.driverEntityService = driverEntityService;
        this.authController = authController;
        this.logEntitiesService = logEntitiesService;
    }

    /**
     * Загружаем тестовые данные в базу.
     *
     * @param args
     *        аргументы командной строки, во время запуска.
     */
    @Override
    public void run(
        String... args)
    {
        if (!isAddValues) return;

        registerUsers();

        List<DriverEntity> driverEntities = addDriverEntities();

        List<LogEntity> logEntities = addLogEntities(driverEntities);

    }

    private List<LogEntity> addLogEntities(
        List<DriverEntity> driverEntities)
    {
        List<LogEntity> logEntities = new ArrayList<>();

        addLogEntity(logEntities, 10, 12, 14, 123, driverEntities.get(0).getDriverId(), "(1/7) Запуск драйвера: 1");
        addLogEntity(logEntities, 10, 12, 14, 250, driverEntities.get(0).getDriverId(), "(2/7) Работа драйвера: 1");
        addLogEntity(logEntities, 10, 12, 14, 983, driverEntities.get(0).getDriverId(), "(3/7) Передача данных: 1");
        addLogEntity(logEntities, 10, 12, 15, 003, driverEntities.get(0).getDriverId(), "(4/7) Окончание передачи данных: 1");
        addLogEntity(logEntities, 10, 12, 15, 154, driverEntities.get(0).getDriverId(), "(5/7) Слушаем: 1");
        addLogEntity(logEntities, 10, 12, 16, 789, driverEntities.get(0).getDriverId(), "(6/7) Ошибка подключения: 1");
        addLogEntity(logEntities, 10, 12, 16, 978, driverEntities.get(0).getDriverId(), "(7/7) Драйвер завершен: 1");

        addLogEntity(logEntities, 10, 12, 14, 255, driverEntities.get(1).getDriverId(), "(1/7) Запуск драйвера: 2");
        addLogEntity(logEntities, 10, 12, 15, 006, driverEntities.get(1).getDriverId(), "(2/7) Работа драйвера: 2");
        addLogEntity(logEntities, 10, 12, 15, 125, driverEntities.get(1).getDriverId(), "(3/7) Передача данных: 2");
        addLogEntity(logEntities, 10, 12, 15, 687, driverEntities.get(1).getDriverId(), "(4/7) Окончание передачи данных: 2");
        addLogEntity(logEntities, 10, 12, 16, 900, driverEntities.get(1).getDriverId(), "(5/7) Слушаем: 2");
        addLogEntity(logEntities, 10, 12, 17, 123, driverEntities.get(1).getDriverId(), "(6/7) Ошибка подключения: 2");
        addLogEntity(logEntities, 10, 12, 18, 456, driverEntities.get(1).getDriverId(), "(7/7) Драйвер завершен: 2");

        addLogEntity(logEntities, 10, 12, 15, 688, driverEntities.get(2).getDriverId(), "(1/7) Запуск драйвера: 3");
        addLogEntity(logEntities, 10, 12, 15, 902, driverEntities.get(2).getDriverId(), "(2/7) Работа драйвера: 3");
        addLogEntity(logEntities, 10, 12, 17, 125, driverEntities.get(2).getDriverId(), "(3/7) Передача данных: 3");
        addLogEntity(logEntities, 10, 12, 18, 457, driverEntities.get(2).getDriverId(), "(4/7) Окончание передачи данных: 3");
        addLogEntity(logEntities, 10, 12, 18, 458, driverEntities.get(2).getDriverId(), "(5/7) Слушаем: 3");
        addLogEntity(logEntities, 10, 12, 18, 600, driverEntities.get(2).getDriverId(), "(6/7) Ошибка подключения: 3");
        addLogEntity(logEntities, 10, 12, 18, 777, driverEntities.get(2).getDriverId(), "(7/7) Драйвер завершен: 3");

        logEntitiesService.saveAll(logEntities);

        return logEntities;
    }

    private void addLogEntity(
        List<LogEntity> list,
        int hour,
        int minutes,
        int seconds,
        int millisec,
        Long driverId,
        String message)
    {
        LocalDateTime dateTime = LocalDateTime
                .of(2022, Month.JANUARY, 15, hour, minutes, seconds, millisec);
        Timestamp logTime = Timestamp.valueOf(dateTime);

        LogEntity logEntity = new LogEntity();
        logEntity.setLogDate(logTime);
        logEntity.setDriverInstance(driverId);
        logEntity.setMessage(message);
        list.add(logEntity);
    }

    private void registerUsers()
    {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("admin");
        signupRequest.setEmail("admin@admin.admin");
        signupRequest.setPassword("123456");
        Set<String> roles = new HashSet<>();
        roles.add("mod");
        roles.add("user");
        roles.add("admin");
        signupRequest.setRole(roles);

        authController.registerUser(signupRequest);
    }

    private List<DriverEntity> addDriverEntities()
    {
        List<DriverEntity> driverEntities = new ArrayList<>();
        TreeMap<String, DriverParameter> parameters = new TreeMap<>();
        parameters.put("kdlprime.port.result", makeParameter("kdlprime.port.result", "2002"));
//        parameters.put("result.port", makeParameter("result.port", "2000"));


        driverEntities.add(makeDriverEntity("KDLPrime3", "KDLPrime",
                DriverType.SOCKET,  DriverStatus.STOP, parameters));

        parameters = new TreeMap<>();
        parameters.put("result.host", makeParameter("result.host", "localhost1"));
        parameters.put("result.port", makeParameter("result.port", "2001"));

        driverEntities.add(makeDriverEntity("KDLMax2", "KDLMax",
                DriverType.SOCKET, DriverStatus.STOP, parameters));

        parameters = new TreeMap<>();
        parameters.put("result.host", makeParameter("result.host", "localhost2"));
        parameters.put("result.port", makeParameter("result.port", "2003"));

        driverEntities.add(makeDriverEntity("CITM1", "CITM",
                DriverType.SOCKET, DriverStatus.STOP, parameters));

        driverEntityService.saveAll(driverEntities);

        return driverEntities;
    }

    /**
     * Создаем объект конфигурации экземпляра драйвера.
     *
     * @param name
     *        имя драйвера.
     * @param code
     *        код драйвера.
     * @param type
     *        тип драйвера.
     * @param status
     *        статус драйвера.
     * @param parameters
     *        параметры конфигурации драйвера.
     * @return объект конфигурации драйвера.
     */
    public static DriverEntity makeDriverEntity(
        String name,
        String code,
        DriverType type,
        DriverStatus status,
        TreeMap<String, DriverParameter> parameters)
    {
        DriverEntity driverEntity;
        driverEntity = new DriverEntity();
        driverEntity.setCode(code);
        driverEntity.setName(name);
        driverEntity.setType(type);
        driverEntity.setStatus(status);

        parameters.keySet().forEach(key -> {
            driverEntity.addDriverParameter(key, parameters.get(key));
        });
        driverEntity.setParameters(parameters);

        return driverEntity;
    }

    /**
     * Создаем параметр для конфигурации экземпляра драйвера.
     *
     * @param name
     *        имя параметра.
     * @param value
     *        значение параметра.
     * @return параметр для конфигурации экземпляра драйвера.
     */
    public static DriverParameter makeParameter(
        String name,
        String value)
    {
        DriverParameter parameter = new DriverParameter();
        parameter.setName(name);
        parameter.setValue(value);
        return parameter;
    }
}
