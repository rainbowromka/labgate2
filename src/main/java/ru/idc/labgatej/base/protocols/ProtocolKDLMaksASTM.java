package ru.idc.labgatej.base.protocols;

import lombok.Setter;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.Utils;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.KdlMax.Packet;
import ru.idc.labgatej.model.KdlMax.AstmOrder;
import ru.idc.labgatej.model.KdlMax.TestResult;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.util.ArrayList;
import java.util.List;

import static ru.idc.labgatej.base.Codes.CR;
import static ru.idc.labgatej.base.Codes.ETB;
import static ru.idc.labgatej.base.Codes.ETX;
import static ru.idc.labgatej.base.Codes.LF;
import static ru.idc.labgatej.base.Codes.STX;
import static ru.idc.labgatej.base.Codes.makePrintable;

@Setter
public class ProtocolKDLMaksASTM implements Protocol<Packet>
{
    Configuration config;

    @Override
    public String makeOrder(Packet packet) {
        final int maxSize = 6900;
        int frameIdx = 0;

        String packetASTM = packet == null ? "" : packet.toASTM();

        if (!packetASTM.isEmpty()) {

//        //разбиваем на пакеты
            String bigMsg = Codes.makeSendable(packetASTM);
            List<String> frames = new ArrayList<>();
            String s;
            while (bigMsg.length() > 0) {
                frameIdx++;
                if (frameIdx > 7) {
                    frameIdx = 0;
                }
                if (bigMsg.length() >= maxSize) {
                    s = bigMsg.substring(0, maxSize);
                    bigMsg = bigMsg.substring(maxSize);
                } else {
                    s = bigMsg;
                    bigMsg = "";
                }

                if (bigMsg.length() > 0) {
                    s = frameIdx + s + ETB;
                } else {
                    s = frameIdx + s + ETX;
                }
                // добавляем контрольную сумму
                s = s + Utils.calcCRC8(s);

                frames.add(STX + s + CR + LF);
            }

            return makePrintable(String.join("", frames));
        }
        else
        {
            return "";
        }
    }

    @Override
    public List<PacketInfo> parseMessage(String msg) {
        List<PacketInfo> result = new ArrayList<>();
        PacketInfo packetInfo = new PacketInfo();
        result.add(packetInfo);

        try
        {
            Packet packet = Packet.parseAstmPacket(msg);
            packetInfo.setHeader(new HeaderInfo("", "Q".equalsIgnoreCase(packet.getProcessingId())));
            if (packet.getPatients().size() > 0
                && packet.getPatients().get(0).getAstmOrders().size()>0)
            {
                AstmOrder astmOrder = packet.getPatients().get(0).getAstmOrders().get(0);
                OrderInfo orderInfo = new OrderInfo(astmOrder.getSpecimenID());
                packetInfo.setOrder(orderInfo);

                if (astmOrder.getTestResults().size()>0) {
                    for (TestResult testResult: astmOrder.getTestResults())
                    {
                        ResultInfo resultInfo = new ResultInfo();
                        resultInfo.setResult(testResult.getValue());

                        String[] universalTestID = testResult
                            .getUniversalTestId().split("\\^");
                        if (universalTestID.length >= 4) {
                            resultInfo.setTest_code(universalTestID[3]);
                        }
                        resultInfo.setTest_type("SAMPLE");
                        // TODO: 26.02.2021 Возможно взять код из файла конфигурации.
                        resultInfo.setDevice_name(testResult.getInstrumentId());
                        resultInfo.setUnits(testResult.getUnits());

                        if (testResult.getTestStarted() != null)
                        {
                            resultInfo.setTest_started(testResult
                                .getTestStarted());
                        }
                        if (testResult.getTestCompleted() != null)
                        {
                            resultInfo.setTest_completed(testResult
                                .getTestCompleted());
                        }

                        packetInfo.addResult(resultInfo);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public HeaderInfo parseHeader(String msg) {
        return null;
    }

    @Override
    public OrderInfo parseOrder(String msg) {
        return null;
    }

    @Override
    public List<ResultInfo> parseResults(String msg) {
        return null;
    }
}
