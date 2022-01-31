package ru.idc.labgatej.drivers.KDLPrime;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.DriverContext;
import ru.idc.labgatej.base.IConfiguration;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.base.ISendClientMessages;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Драйвер обработки данных результатов анализа от FRT Manager.
 */
@Slf4j
public class KdlPrime implements IDriver
{
    /**
     * Конфигурация, мало ли что понадобится.
     */
    IConfiguration config;

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

    /**
     * Признак того что драйвер работает. Перевод в false его останавливает.
     */
    AtomicBoolean running;

    /**
     * Сервис отправки сообщений клиенту.
     */
    private ISendClientMessages sendClientMessages;

    @Override
    public void loop()
    throws IOException, InterruptedException, SQLException
    {
        serverSocket = new ServerSocket(port);
        if (sendClientMessages != null)
            sendClientMessages.sendDriverIsRunning(config);
        try
        {
            while (running.get()) {
                log.info("Слушаем порт: ");
                //TODO: надо в отдельном потоке сделать, как вариант, прерывание
                // работы метода accept. Правда надо понять, насколько это надо.
                new KDLPrimeClientHandler(serverSocket.accept(),
                        connectionPool, config
                ).start();
            }
        }
        finally
        {
            if (sendClientMessages != null)
                sendClientMessages.sendDriverIsStopped(config);
        }
    }

    @Override
    public void init(
        DriverContext driverContext)
    {
        this.config = driverContext.getConfig();
        this.connectionPool = driverContext.getConnectionPool();
        this.port = Integer.parseInt(
            config.getParamValue("kdlprime.port.result"));
        this.running = driverContext.getRunning();
        this.sendClientMessages = driverContext.getSendClientMessages();
    }

    @Override
    public void close() {
        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            log.error("Ошибка при закрытии серверного сокета", e);
        }
    }

    @Override
    public void stop()
    {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
