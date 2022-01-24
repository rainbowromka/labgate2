package ru.idc.labgatej.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;

/**
 * Объект пула соединения с базой данных. Получает из файла конфигурации
 * config.properties данные соединения с базой данных. Используется в случае,
 * если драйвер запущен как обычное Java приложением.
 */
public class AppPooledDataSource
implements IGetPooledDataSource
{
    /**
     * Пул соединений с базой данных.
     */
    private ComboPooledDataSource cpds = new ComboPooledDataSource();

    /**
     * Объект конфигурации.
     */
    Configuration config;

    /**
     * Создает объект, содержащий пул соединения с базой данных.
     *
     * @param config
     *        конфигурация.
     * @throws PropertyVetoException
     *         генерируемое исключение.
     */
    public AppPooledDataSource(
        Configuration config)
    throws PropertyVetoException
    {
        this.config = config;

        cpds.setDriverClass("org.postgresql.Driver");
        cpds.setJdbcUrl(config.getParamValue("db.url"));
        cpds.setUser(config.getParamValue("db.user"));
        cpds.setPassword(config.getParamValue("db.password"));
    }

    @Override
    public ComboPooledDataSource getCpds()
    {
        return cpds;
    }
}
