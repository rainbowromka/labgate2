package ru.idc.labgatej.manager.preloaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.idc.labgatej.manager.controllers.AuthController;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.model.DriverParameter;
import ru.idc.labgatej.manager.model.DriverStatus;
import ru.idc.labgatej.manager.model.DriverType;
import ru.idc.labgatej.manager.payload.request.SignupRequest;
import ru.idc.labgatej.manager.services.DriverEntityService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

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
        AuthController authController)
    {
        this.driverEntityService = driverEntityService;
        this.authController = authController;
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
