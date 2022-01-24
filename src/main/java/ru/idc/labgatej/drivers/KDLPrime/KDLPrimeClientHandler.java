package ru.idc.labgatej.drivers.KDLPrime;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.IConfiguration;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Обработчик входящего соединения с прибором.
 */
@Slf4j
public class KDLPrimeClientHandler extends Thread
{
    private final String inetAddress;
    private IConfiguration config;
    private ComboPooledDataSource connectionPool;
    private Socket socket;

    /**
     * Создание обработчика входящего соединения с прибором.
     *
     * @param socket
     *        Сокет.
     * @param connectionPool
     *        Пул соединения с базой данных.
     * @param config
     *        Конфигурация программы.
     */
    public KDLPrimeClientHandler(
            Socket socket,
            ComboPooledDataSource connectionPool,
            IConfiguration config)
    {
        this.connectionPool = connectionPool;
        this.socket = socket;
        this.config = config;
        this.inetAddress = socket.getInetAddress() + ":" + socket.getPort();
    }

    /**
     * Обрабатываем соединение с прибором.
     */
    @SneakyThrows
    public void run()
    {
        log.info("[{}] Обрабатываем входящее соединение: ", inetAddress);
        KDLPrimeDriver driver = new KDLPrimeDriver();
        driver.init(connectionPool, config, socket);
        driver.loop();
    }
}
