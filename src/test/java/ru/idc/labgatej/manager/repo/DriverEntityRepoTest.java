package ru.idc.labgatej.manager.repo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import ru.idc.labgatej.manager.model.DriverEntity;
import ru.idc.labgatej.manager.model.DriverParameter;
import ru.idc.labgatej.base.DriverStatus;
import ru.idc.labgatej.manager.model.DriverType;

import java.util.TreeMap;

import static org.assertj.core.api.Assertions.*;
import static ru.idc.labgatej.manager.preloaders.DataBaseLoader.*;
/**
 * Тестируем репозиторий экземпляра драйвера.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DriverEntityRepoTest
{
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private DriverEntityRepository driverEntityRepository;

    @Test
    public void findByEventDate()
    {
        TreeMap<String, DriverParameter> paramters = new TreeMap<>();
        paramters.put("result.host", makeParameter("result.host", "localhost"));
        paramters.put("result.port", makeParameter("result.port", "2000"));
        testEntityManager.persist(makeDriverEntity("KDLPrime3", "KDLPrime",
            DriverType.SOCKET, DriverStatus.STOP, paramters));

        paramters = new TreeMap<>();
        paramters.put("result.host", makeParameter("result.host", "localhost1"));
        paramters.put("result.port", makeParameter("result.port", "2001"));
        testEntityManager.persist(makeDriverEntity("KDLMax2", "KDLMax",
            DriverType.SOCKET, DriverStatus.WORK, paramters));

        paramters = new TreeMap<>();
        paramters.put("result.host", makeParameter("result.host", "localhost2"));
        paramters.put("result.port", makeParameter("result.port", "2003"));
        testEntityManager.persist(makeDriverEntity("CITM1", "CITM",
            DriverType.SOCKET, DriverStatus.WORK, paramters));

        Pageable pageable = PageRequest.of(0, 2);
        Page<DriverEntity> result = driverEntityRepository.findAll(pageable);

        assertThat(result).hasSize(2);
        assertThat(result.getContent().get(1))
            .hasFieldOrPropertyWithValue("id", 2L)
            .hasFieldOrPropertyWithValue("name", "KDLMax2")
            .hasFieldOrPropertyWithValue("code", "KDLMax")
            .hasFieldOrPropertyWithValue("type", DriverType.SOCKET)
            .hasFieldOrPropertyWithValue("status", DriverStatus.WORK)
            .hasFieldOrProperty("parameters");
        DriverParameter parameter = result.getContent().get(0)
            .getParameters().get("result.port");
        assertThat(parameter)
            .hasFieldOrPropertyWithValue("id", 2L)
            .hasFieldOrPropertyWithValue("name", "result.port")
            .hasFieldOrPropertyWithValue("value", "2000");
    }
}
