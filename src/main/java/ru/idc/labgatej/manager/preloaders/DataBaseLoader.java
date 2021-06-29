package ru.idc.labgatej.manager.preloaders;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.model.DriverParameter;
import ru.idc.labgatej.manager.model.DriverStatus;
import ru.idc.labgatej.manager.model.DriverType;
import ru.idc.labgatej.manager.services.DriverEntityService;
import ru.idc.labgatej.manager.services.DriverParameterService;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Component
public class DataBaseLoader
implements CommandLineRunner
{
    private static final boolean isAddValues = true;

    private final DriverEntityService driverEntityService;
    private final DriverParameterService driverParameterService;

    @Autowired
    public DataBaseLoader(
        DriverEntityService driverEntityService,
        DriverParameterService driverParameterService)
    {
        this.driverEntityService = driverEntityService;
        this.driverParameterService = driverParameterService;
    }

    @Override
    public void run(
        String... args)
    throws Exception
    {
        if (!isAddValues) return;

        List<DriverEntity> driverEntities = new ArrayList<>();
        TreeMap<String, DriverParameter> parameters = new TreeMap<>();
        parameters.put("result.host", makeParameter("result.host", "localhost"));
        parameters.put("result.port", makeParameter("result.port", "2000"));

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
    }

    public static DriverEntity makeDriverEntity(String name, String code, DriverType type, DriverStatus status, TreeMap<String, DriverParameter> parameters)
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

    public static DriverParameter makeParameter(String name, String value) {
        DriverParameter parameter = new DriverParameter();
        parameter.setName(name);
        parameter.setValue(value);
        return parameter;
    }
}
