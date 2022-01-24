package ru.idc.labgatej.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.protocols.Protocol;

import java.io.IOException;

/**
 * Базовая реализация драйвера, принимающего данные с прибора и отправляющего
 * данные на сервер.
 * TODO: по идее бы разделить функционал приема данных и отправки. Но по мере
 * надобности.
 *
 * @param <T> протокол прибора.
 */
@Slf4j
public abstract class ResultDriver<T extends Protocol>
implements IDriver
{
    protected Transport transport4results;
    protected DBManager dbManager4results;
    protected T protocol;

    @Override
    public void init(DriverContext driverContext) {
        this.dbManager4results = new DBManager();
        dbManager4results.init(driverContext.getConnectionPool());
    }

    protected abstract String receiveResults() throws IOException;

    @Override
    public void close() {
        if (transport4results != null) {
            transport4results.close();
        }
    }

}
