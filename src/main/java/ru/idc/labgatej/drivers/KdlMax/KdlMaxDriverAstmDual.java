package ru.idc.labgatej.drivers.KdlMax;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DriverContext;
import ru.idc.labgatej.base.IConfiguration;
import ru.idc.labgatej.base.SocketClientTransport;
import ru.idc.labgatej.base.TaskDualDriver;
import ru.idc.labgatej.base.protocols.ProtocolKDLMaksASTM;
import ru.idc.labgatej.model.KdlMax.AstmOrder;
import ru.idc.labgatej.model.KdlMax.MessageTerminator;
import ru.idc.labgatej.model.KdlMax.Packet;
import ru.idc.labgatej.model.KdlMax.Patient;
import ru.idc.labgatej.model.Order;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static ru.idc.labgatej.base.Codes.ENQ;
import static ru.idc.labgatej.base.Codes.EOT;
import static ru.idc.labgatej.base.Codes.STX;
import static ru.idc.labgatej.base.Consts.ERROR_TIMEOUT;

@Slf4j
public class KdlMaxDriverAstmDual extends TaskDualDriver<ProtocolKDLMaksASTM>
{
    private String deviceDriverCode;

    @Override
    protected List<Order> getOrders() throws SQLException {
        return dbManager4tasks.getDeviceQueryOrders(deviceDriverCode);
    }

    @Override
    protected String protocolMakeOrder(List<Order> orders) {
        return protocol.makeOrder(toPacket(orders));
    }

    @Override
    protected void markOrderAsProcessed(long taskId) {
        dbManager4tasks.markDeviceQueryOrderAsProcessed(taskId);
    }

    @Override
    protected void markOrderAsFailed(long taskId, String comment) {
        dbManager4tasks.markDeviceQueryOrderAsFailed(taskId, comment);
    }

    @Override
    public void init(
        DriverContext driverContext
    )
    {
        super.init(driverContext);

        IConfiguration config = driverContext.getConfig();

        this.protocol = new ProtocolKDLMaksASTM();
        this.protocol.setConfig(config);
        this.transport4tasks = new SocketClientTransport(
                config.getParamValue("kdlmax.server"),
                Integer.parseInt(config.getParamValue("kdlmax.port.task")));
        this.transport4tasks.init(2000);
        this.transport4results = new SocketClientTransport(
                config.getParamValue("kdlmax.server"),
                Integer.parseInt(config.getParamValue("kdlmax.port.result")));
        this.transport4results.init(10000);
        this.deviceDriverCode = config.getParamValue("code");
    }

    //TODO: повтор в коде, возможно сделать отдельно для АСТМ
    protected String receiveResults() throws IOException {
        StringBuilder sb = new StringBuilder();
        int res;
        do {
            res = transport4results.readInt(true);
            if (res == ERROR_TIMEOUT) {
                log.trace("ждём данных");
            } else if (res == ENQ) {
                log.debug("нам хотят что-то прислать");
                sb.setLength(0);
                transport4results.sendMessage("<ACK>");
            } else if (res == STX) {
                String msg = transport4results.readMessage();
                sb.append(msg);
                transport4results.sendMessage("<ACK>");
            } else if (res == EOT) {
                log.debug("мы зафиксировали конец передачи");
                System.out.println(sb.toString());
                return sb.toString();
            } else {
                log.error("ошибка протокола");
            }
        } while (res != ERROR_TIMEOUT);
        return null;
    }

    private Packet toPacket(List<Order> orders)
    {
        if (orders == null || orders.size() == 0)
        {
            return null;
        }

        Packet packet = new Packet();
        packet.setSenderNameId("PCR_line");
        packet.setReceiverId("PCR_line");
        packet.setProcessingId("P");
        packet.setMessageDateTime(new Date());
        Patient patient = new Patient();
        packet.getPatients().add(patient);
        patient.setSequence(1L);

        String mainBarcode = orders.get(0).getBarcode();

        long idx = 1L;
        AstmOrder astmOrder = new AstmOrder();
        patient.getAstmOrders().add(astmOrder);
        astmOrder.setSequence(idx);
        astmOrder.setSpecimenID(mainBarcode);
        StringBuilder universalTest = new StringBuilder();
        astmOrder.setUniversalTestID("^^^" + orders.get(0).getTestId());
        astmOrder.setOrderedDateTime(new Date());
        astmOrder.setSpecimenDescriptor("3");
        packet.setMessageTerminator(new MessageTerminator(1L,"N"));

        return packet;
    }

    @Override
    public void stop() {
    }
}
