package ru.idc.labgatej.drivers;

import lombok.SneakyThrows;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import ru.idc.labgatej.base.protocols.ProtocolKDLMaksASTM;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.KdlMax.Packet;
import ru.idc.labgatej.model.KdlMax.MessageTerminator;
import ru.idc.labgatej.model.KdlMax.AstmOrder;
import ru.idc.labgatej.model.KdlMax.Patient;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

public class KdlMaksTest {
    @SneakyThrows
    @Test
    public void Test_Packet_to_ASTM ()
    {
        String expected = "H|\\^&||PCR_line|PCR_line|||||PCR_line||P||" +
            "20160311032101<CR>" +
            "P|1||||||||||||||||||||||||||||||||<CR>" +
            "O|1|s1||^^^Ca||20160311032101|20160311||||||||3|" +
            "ASTM Emulator||||ILS Testers||||||||||<CR>" +
            "L|1|N<CR>";

        Packet packet = new Packet();
        Patient patient = new Patient();
        MessageTerminator messageTerminator = new MessageTerminator();
        packet.setMessageTerminator(messageTerminator);

        packet.setAccessPassword("PCR_line");
        packet.setSenderNameId("PCR_line");
        packet.setReceiverId("PCR_line");
        packet.setProcessingId("P");
        packet.setMessageDateTime(new SimpleDateFormat("yyyyMMddHHmmss")
            .parse("20160311032101"));


        packet.getPatients().add(patient);
        patient.setSequence(1L);

        AstmOrder astmOrder = new AstmOrder();
        patient.getAstmOrders().add(astmOrder);
        astmOrder.setSequence(1L);
        astmOrder.setSpecimenID("s1");
        astmOrder.setUniversalTestID("^^^Ca");
        astmOrder.setOrderedDateTime(new SimpleDateFormat("yyyyMMddHHmmss")
            .parse("20160311032101"));
        astmOrder.setSpecimenCollectionDateTime(
            new SimpleDateFormat("yyyyMMddHHmmss").parse("20160311000000"));
        astmOrder.setSpecimenDescriptor("3");
        astmOrder.setOrderingPhysician("ASTM Emulator");
        astmOrder.setLabFieldNo1("ILS Testers");

        messageTerminator.setSequence(1L);
        messageTerminator.setTerminationCode("N");

        assertEquals(expected,
            packet.toASTM());
    }

    @Test
    public void Test_ASTM_to_PACKET ()
    {
        ProtocolKDLMaksASTM protocol = new ProtocolKDLMaksASTM();

        PacketInfo actualPacket = protocol.parseMessage(
            "H|\\^&||PCR_line|PCR_line|||||PCR_line||P||20160318040923\r" +
            "P|1||||^^||||||||||||||||||||||||||||\r" +
            "O|1|s1||^^^HPVgen||20160318040831|00010101||||||||||||21.11.2015^20160521|||||||||||\r" +
            "R|1|^^^HPVgen^HPV16|2|||||F|||||\r" +
            "R|2|^^^HPVgen^HPV31|1|||||F|||||\r" +
            "R|3|^^^HPVgen^HPV18|2|||||F|||||\r" +
            "R|4|^^^HPVgen^HPV39|2|||||F|||||\r" +
            "R|5|^^^HPVgen^HPV45|2|||||F|||||\r" +
            "R|6|^^^HPVgen^HPV59|2|||||F|||||\r" +
            "R|7|^^^HPVgen^HPV33|2|||||F|||||\r" +
            "R|8|^^^HPVgen^HPV35|2|||||F|||||\r" +
            "R|9|^^^HPVgen^HPV56|2|||||F|||||\r" +
            "R|10|^^^HPVgen^HPV58|1|||||F|||||\r" +
            "R|11|^^^HPVgen^HPV52|2|||||F|||||\r" +
            "R|12|^^^HPVgen^HPV51|2|||||F|||||\r" +
            "L|1|N\r");

        PacketInfo expected = new PacketInfo();
        HeaderInfo headerInfo = new HeaderInfo("", false);
        expected.setHeader(headerInfo);
        OrderInfo orderInfo = new OrderInfo("s1");
        expected.setOrder(orderInfo);

        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "1", ""));
        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "1", ""));
        expected.addResult(newResult("HPVgen", "2", ""));
        expected.addResult(newResult("HPVgen", "2", ""));

        ReflectionAssert.assertReflectionEquals(expected, actualPacket);
    }

    private ResultInfo newResult(String testCode, String result, String units)
    {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setTest_type("SAMPLE");
        resultInfo.setTest_code(testCode);
        resultInfo.setResult(result);
        resultInfo.setUnits(units);
        return resultInfo;
    }

}