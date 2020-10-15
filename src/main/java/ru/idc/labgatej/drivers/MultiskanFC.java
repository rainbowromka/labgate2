package ru.idc.labgatej.drivers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.security.NoTypePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.drivers.common.SharedFolderDriver;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;
import ru.idc.labgatej.model.multiskanfc.Evaluation;
import ru.idc.labgatej.model.multiskanfc.Measure;
import ru.idc.labgatej.model.multiskanfc.Measures;
import ru.idc.labgatej.model.multiskanfc.Plate;
import ru.idc.labgatej.model.multiskanfc.Sample;
import ru.idc.labgatej.model.multiskanfc.Session;
import ru.idc.labgatej.model.multiskanfc.Well;
import ru.idc.labgatej.model.multiskanfc.Wells;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MultiskanFC extends SharedFolderDriver {
    final Logger log = LoggerFactory.getLogger(MultiskanFC.class);

    @Override
    public List<PacketInfo> parseFile(Path file) throws IOException {
        List<PacketInfo> packets = new ArrayList<>();

        XStream xstream = new XStream();
        xstream.ignoreUnknownElements();
        // 02.07.2020 11:57:33
        String dateFormat = "dd.MM.yyyy HH:mm:ss";
        String timeFormat = "HH:mm:ss";
        String[] acceptableFormats = {dateFormat};
        xstream.registerConverter(new DateConverter(dateFormat, acceptableFormats));

        xstream.addPermission(NoTypePermission.NONE);
        xstream.allowTypesByRegExp(new String[] { ".*" });
        xstream.processAnnotations(Session.class);
        String xml = readFileToString(file);
        Session session = (Session) xstream.fromXML(xml);
        Plate plate;
        String test;
        Sample sample;
        String sampleId;
        PacketInfo packet;
        ResultInfo res;

        for(Measure measure: Optional.ofNullable(session.getMeasures()).map(Measures::getMeasures).orElse(null)) {
            test = measure.getAnalyte();
            plate = measure.getPlate();
            for (Well well: Optional.ofNullable(plate.getWells()).map(Wells::getWells).orElse(null)) {
                sample = well.getSample();
                if (sample == null) continue;

                sampleId = sample.getBarcode() != null
                  ? sample.getBarcode().trim()
                  : sample.getName().trim();

                packet = new PacketInfo();
                packets.add(packet);
                packet.setHeader(new HeaderInfo(sampleId, false));
                packet.setOrder(new OrderInfo(sampleId));

                res = new ResultInfo();
                packet.addResult(res);

                res.setDevice_name(deviceCode);
                res.setSample_id(sampleId);
                res.setTest_completed(measure.getTime());
                res.setTest_type("SAMPLE");
                res.setResult(sample.getOpticalDencity().getText().replace(",", "."));
                res.setTest_code(test);
                res.setComment(Optional.ofNullable(sample.getEvaluation()).map(Evaluation::getText).orElse(null));
            }
        }

        return packets;
    }

    private static String readFileToString(Path file) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(file, StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }

        return contentBuilder.toString();
    }
}
