package ru.idc.labgatej.drivers;


import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.drivers.common.SharedFolderDriver;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Драйвер для ручного импортирования данных с приборов. Данные записываются в
 * Excel, результаты импортируются в папку в формате CSV. Отправляет данные.
 */
public class CsvImporter
extends SharedFolderDriver
{
    final Logger log = LoggerFactory.getLogger(RealBest.class);

    @Override
    public List<PacketInfo> parseFile(Path file) throws IOException
    {
//        String line;
        String[] words;
//        Map<String, String> data = new HashMap<>();
        List<PacketInfo> packets = new ArrayList<>();

        if (!"csv".equals(
                FilenameUtils.getExtension(file.toString()).toLowerCase()))
        {
            return packets;
        }

        try (CSVReader sc = new CSVReader(new FileReader(file.toFile()), ',', '"'))
        {
            java.util.Date date = new Date();

            ResultInfo res;
            boolean isFirst = true;
            Integer barcodeIndex = null;
            Integer resultIndex = null;
            Integer maxIndex = null;

            while ((words = sc.readNext()) != null) {
                PacketInfo packet = new PacketInfo();

                if (isFirst) {
                    for (int q = 0; q < words.length; q++) {
                        if ("BARCODE".equals(words[q])) {
                            barcodeIndex = q;
                        } else
                        if ("RESULT".equals(words[q])) {
                            resultIndex = q;
                        }
                    }

                    if ((barcodeIndex == null) || (resultIndex == null)) break;

                    maxIndex = Math.max(barcodeIndex, resultIndex);
                    isFirst = false;

                    continue;
                }

                if (words.length <= maxIndex) continue;

                String id = words[barcodeIndex];

                if (id.isEmpty()) continue;

                String result = words[resultIndex];

                if ("+".equals(result))
                {
                    result = "ПОЛ.";
                } else
                if ("-".equals(result))
                {
                    result = "ОТР.";
                } else {
                    continue;
                }

                if (id != null) {
                    packet.setHeader(new HeaderInfo(id, false));
                    packet.setOrder(new OrderInfo(id.trim()));
                }

                res = new ResultInfo();
                packet.addResult(res);

                res.setDevice_name(deviceCode);
                if (date != null)
                    res.setTest_completed(date);
                res.setTest_type("SAMPLE");

                res.setResult(result);
                res.setTest_code("339");

                packets.add(packet);
            }
        }

        return packets;
    }
}
