package ru.idc.labgatej.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.protocols.Protocol;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.PacketInfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static ru.idc.labgatej.base.Codes.ENQ;
import static ru.idc.labgatej.base.Codes.EOT;
import static ru.idc.labgatej.base.Codes.STX;
import static ru.idc.labgatej.base.Codes.makeSendable;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

/**
 * Базовая реализация драйвера, отправляющего данные на сервер, получающего
 * данные исследования с прибора и отправляющего резальтаты исследований в ЛИС.
 *
 * @param <T> протокол прибора.
 */
@Slf4j
public abstract class TaskDualDriver<T extends Protocol>
extends ResultDriver<T>
implements ITaskDriver
{
    protected Transport transport4tasks;
    protected DBManager dbManager4tasks;

    @Override
    public void init(ComboPooledDataSource connectionPool, Configuration config)
    {
        super.init(connectionPool, config);

        this.dbManager4tasks = new DBManager();
        log.trace("Инициализация второго подключения к БД...");
        dbManager4tasks.init(connectionPool);
    }

    @Override
    public void sendTasks() throws IOException, SQLException {
        String msg;
        List<Order> orders;
        boolean hasErrors = false;
        long taskId;
        int cnt = 0;
        int res;
        do {
            orders = getOrders();
            msg = protocolMakeOrder(orders);
            if (!msg.isEmpty()) {
                taskId = orders.get(0).getTaskId();
                transport4tasks.sendMessage("<ENQ>");
                res = transport4tasks.readInt();
                if (res == Codes.ACK) {
                    log.debug("нас готовы слушать");
                    transport4tasks.sendMessage(msg);
                    res = transport4tasks.readInt();
                    if (res == Codes.ACK) {
                        log.debug("Наше сообщение приняли");
                        // нужно помечать задание как обработанное в БД
                        markOrderAsProcessed(taskId);
                    } else if (res == Codes.NAK) {
                        log.debug("Наше сообщение не понравилось почему-то");
                        hasErrors = true;
                        markOrderAsFailed(taskId, "Наше сообщение не понравилось почему-то");
                    }	else {
                        log.error("ошибка протокола");
                        hasErrors = true;
                        markOrderAsFailed(taskId, "ошибка протокола");
                    }
                    transport4tasks.sendMessage("<EOT>");
                } else if (res == Codes.NAK) {
                    log.debug("нас не готовы слушать, ждём 10 секунд");
                    // надо подождать не меньше 10 секунд
                    hasErrors = true;
                } else {
                    if (res == Codes.ENQ) {
                        log.error("нас перебили");
                    } else {
                        log.error("ошибка протокола");
                    }
                    hasErrors = true;
                }
                cnt++;
            }
        } while (!hasErrors && !msg.isEmpty() && cnt < 5);
    }

    protected abstract void markOrderAsFailed(long taskId, String comment);

    protected abstract void registerAliquots(List<Order> orders);

    protected abstract void markOrderAsProcessed(long taskId);

    protected abstract String protocolMakeOrder(List<Order> orders);

    protected abstract List<Order> getOrders() throws SQLException;

    @Override
    public void loop() throws IOException, InterruptedException, SQLException {
        if (!transport4tasks.isReady()) return;
        if (!transport4results.isReady()) return;

        Runnable task = () -> {
            try {
                while (! Thread.currentThread().isInterrupted()) {
                    sendTasks();
                }
            } catch (Exception e) {
                log.error("Ошибка в потоке отправки заданий", e);
                Thread.currentThread().interrupt();
            }
        };

        Thread thread = new Thread(task);
        thread.start();

        String msg = null;
        while (true) {
            if (!thread.isAlive()) {
                throw new IOException();
            }

            msg = receiveResults();

            if (msg != null && !msg.isEmpty()) {
                PacketInfo packetInfo = protocol.parseMessage(makeSendable(msg));
                dbManager4results.saveResults(packetInfo, true);
            }
            Thread.sleep(500);
        }
    }

    @Override
    public void close() {
        if (transport4tasks != null) {
            transport4tasks.close();
        }

        super.close();
    }

    // для отладки
    private String makeResults() {

        return "1H|\\^&|6347||^^^|||||||P||20200517130941<CR><ETX>B6<CR><LF>" +
                "2P|1||?||^||||||||||||||||||20200517122737||<CR><ETX>43<CR><LF>" +
                "3O|1|20005480||ALL|?|20200517122737|||||X||||1||||||||||F<CR><ETX>20<CR><LF>" +
                "4R|1|^^^108^1^^^COBAS_CCEE^^^^^^|3.890|mmol/l||N||F||&S&SYSTEM^System||20200517080941|COBAS_CCEE<CR><ETX>65<CR><LF>" +
                "5L|1|N<CR><ETX>08<CR><LF>";

//		return "1H|\\^&|5653||^^^|||||||P||20200516145049<CR><ETX>B9<CR><LF>" +
//					 "2P|1||?||^||||||||||||||||||20200516131727||<CR><ETX>41<CR><LF>" +
//					 "3O|1|20005317||ALL|?|20200516131727|||||X||||1||||||||||F<CR><ETX>1D<CR><LF>" +
//					 "4R|1|^^^44^1^^^COBAS_CCEE^^^^^^|49.000|umol/l||N||C||&S&SYSTEM^System||20200516092429|COBAS_CCEE<CR><ETX>65<CR><LF>" +
//					 "5L|1|N<CR><ETX>08<CR><LF>";
    }
}
