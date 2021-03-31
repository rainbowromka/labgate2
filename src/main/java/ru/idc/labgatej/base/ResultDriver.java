package ru.idc.labgatej.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.protocols.Protocol;

import java.io.IOException;
import java.net.Socket;

import static ru.idc.labgatej.base.Codes.ENQ;
import static ru.idc.labgatej.base.Codes.EOT;
import static ru.idc.labgatej.base.Codes.STX;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

/**
 * Базовая реализация драйвера, принимающего данные с прибора и отправляющего
 * данные на сервер.
 * TODO: по идее бы разделить функционал приема данных и отправки. Но по мере надобности.
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
    public void init(ComboPooledDataSource connectionPool, Configuration config) {
        this.dbManager4results = new DBManager();
        dbManager4results.init(connectionPool);
//        log.trace("Инициализация второго подключения к БД...");
        transport4results = new SocketClientTransport(config.getParamValue("citm.server"), Integer.parseInt(config.getParamValue("citm.port.result")));
        transport4results.init(10000);
    }

    protected abstract String receiveResults() throws IOException;

    @Override
    public void close() {
        transport4results.close();
    }

}
