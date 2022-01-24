package ru.idc.labgatej.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Контекст драйвера, содержащего переменные в рамках конкретного соединения
 * с устройством.
 *
 * @author Roman Perminov.
 */
@AllArgsConstructor
@Data
public class DriverSocketContext
extends DriverContext
{
    /**
     * Объект соединения.
     */
    Socket socket;

    public DriverSocketContext(DriverContext driverContext, Socket socket) {
        super(driverContext.getConnectionPool(), driverContext.getConfig(), driverContext.getRunning());
        this.socket = socket;
    }
}
