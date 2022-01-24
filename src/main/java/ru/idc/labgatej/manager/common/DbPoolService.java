package ru.idc.labgatej.manager.common;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;
import ru.idc.labgatej.base.IGetPooledDataSource;

import javax.annotation.PostConstruct;
import java.beans.PropertyVetoException;

/**
 * Объект пула соединения с базой данных. Получает из файла конфигурации
 * config.properties данные соединения с базой данных. Используется в случае,
 * если драйвер запущен как обычное Java приложением.
 *
 * @author Roman Perminov
 */
@Component
@ApplicationScope
public class DbPoolService
implements IGetPooledDataSource
{
    /**
     * HOST для подключения к базе данных.
     */
    @Value("${krypton.db.url}")
    private String jdbcUrl;

    /**
     * Имя пользователя для подключения к базе данных.
     */
    @Value("${krypton.db.user}")
    private String dbUser;

    /**
     * Пароль для получения к базе данных.
     */
    @Value("${krypton.db.password}")
    private String dbPassword;

    /**
     * Пул доступа к базе данных KRYPTON.
     */
    private ComboPooledDataSource cpds = new ComboPooledDataSource();

    /**
     * Подготавливаем пул доступа к базе данных KRYPTON.
     *
     * @throws PropertyVetoException
     *         генерируемое исключение.
     */
    @PostConstruct
    private void perpareDbPool()
    throws PropertyVetoException
    {
        cpds.setDriverClass("org.postgresql.Driver");
        cpds.setJdbcUrl(jdbcUrl);
        cpds.setUser(dbUser);
        cpds.setPassword(dbPassword);
    }

    @Override
    public ComboPooledDataSource getCpds() {
        return cpds;
    }
}
