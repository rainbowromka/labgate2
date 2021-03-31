package ru.idc.labgatej.drivers.KDLPrime;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.IDriver;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

/**
 * Драйвер обработки данных результатов анализа от FRT Manager.
 */
@Slf4j
public class KdlPrime implements IDriver
{

    /**
     * Конфигурация, мало ли что понадобится.
     */
    Configuration config;

    /**
     * Объект сервера.
     */
    ServerSocket serverSocket;

    /**
     * Номер порта, на котором сервер будет слушать порт.
     */
    private int port;

    /**
     * Пулл соединений к базе данных.
     */
    private ComboPooledDataSource connectionPool;

    @Override
    public void loop()
    throws IOException, InterruptedException, SQLException
    {
        serverSocket = new ServerSocket(port);
        while (true)
        {
            log.info("Слушаем порт: ");
            //TODO: надо в отдельном потоке сделать, как вариант, прерывание
            // работы метода accept. Правда надо понять, насколько это надо.
            new KDLPrimeClientHandler(serverSocket.accept(),
                connectionPool, config
            ).start();
        }
    }

    @Override
    public void init(
        ComboPooledDataSource connectionPool,
        Configuration config)
    {
        this.config = config;
        this.connectionPool = connectionPool;
        this.port = Integer.parseInt(
            config.getParamValue("kdlprime.port.result"));
    }

    @Override
    public void close() {
        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
