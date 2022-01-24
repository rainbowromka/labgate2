package ru.idc.labgatej.drivers.lazurite;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DriverContext;
import ru.idc.labgatej.base.IConfiguration;
import ru.idc.labgatej.base.IDriver;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class Lazurite
implements IDriver
{

    /**
     * Объект сервера.
     */
    ServerSocket serverSocket;

    /**
     * Номер порта, на котором сервер будет слушать порт.
     */
    private int port;


    /**
     * Контекст драйвера приложения.
     */
    DriverContext driverContext;

    /**
     * Признак того что драйвер работает. Перевод в false его останавливает.
     */
    AtomicBoolean running;

    @Override
    public void loop()
    throws IOException, InterruptedException, SQLException
    {
        serverSocket = new ServerSocket(port);
        while (running.get())
        {
            log.info("Слушаем порт: " + this.port);
            //TODO: надо в отдельном потоке сделать, как вариант, прерывание
            // работы метода accept. Правда надо понять, насколько это надо.
            new LazuriteClientHandler(
                driverContext, serverSocket.accept()
            ).start();
        }
    }

    @Override
    public void init(DriverContext driverContext)
    {
        this.driverContext = driverContext;

        this.port = Integer.parseInt(
            driverContext.getConfig().getParamValue("device.connection.port"));
        this.running = driverContext.getRunning();
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
