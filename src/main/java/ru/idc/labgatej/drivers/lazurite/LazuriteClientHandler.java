package ru.idc.labgatej.drivers.lazurite;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DriverContext;
import ru.idc.labgatej.base.DriverSocketContext;
import ru.idc.labgatej.base.IConfiguration;

import java.net.Socket;

/**
 * Обработчик входящего соединения с прибором.
 */
@Slf4j
public class LazuriteClientHandler extends Thread
{
    private final String inetAddress;

    DriverSocketContext driverSocketContext;

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
    public LazuriteClientHandler(
        DriverContext driverContext,
        Socket socket)
    {
        this.inetAddress = socket.getInetAddress() + ":" + socket.getPort();
        this.driverSocketContext = new DriverSocketContext(driverContext, socket);
    }

    /**
     * Обрабатываем соединение с прибором.
     */
    @SneakyThrows
    public void run()
    {
        log.info("[{}] Обрабатываем входящее соединение: ", inetAddress);
        LazuriteDriver driver = new LazuriteDriver();
        driver.init(driverSocketContext);
        driver.loop();
    }
}
