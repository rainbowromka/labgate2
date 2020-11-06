package ru.idc.labgatej.drivers;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RealBestTest {

    @Test
    public void test() throws ParseException {
        Path file = Paths.get("src/test/java/ru/idc/labgatej/testData/" +
                "realBest/2020_9_30_17_28_00_002.txt");

        List<PacketInfo> expected = ImmutableList.of(
                createPacket("20200930ROM",
                        new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S")
                                .parse(file.getFileName().toString())
                        ,"SAMPLE","ПОЛ.","5580"),
                createPacket("20200931ROM",
                        new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S")
                                .parse(file.getFileName().toString())
                        ,"SAMPLE","ОТР.","339")
        );

        RealBest realBest = new RealBest();
        List<PacketInfo> packets = realBest.parseFile(file);

        Assert.assertEquals(packets.size(), 2L);
        ReflectionAssert.assertReflectionEquals(expected, packets);
    }

    private PacketInfo createPacket(
        String barcode,
        Date completed,
        String testType,
        String result,
        String testCode)
    {
        PacketInfo packet = new PacketInfo();
        packet.setHeader(new HeaderInfo(barcode, false));
        packet.setOrder(new OrderInfo(barcode.trim()));
        ResultInfo res = new ResultInfo();
        packet.addResult(res);
        res.setTest_completed(completed);
        res.setTest_type(testType);
        res.setResult(result);
        res.setTest_code(testCode);
        return packet;
    };

}
