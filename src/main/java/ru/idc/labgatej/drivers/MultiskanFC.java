package ru.idc.labgatej.drivers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.security.NoTypePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.drivers.common.SharedFolderDriver;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.multiskanfc.Session;
import ru.idc.labgatej.model.multiskanfc.Sessions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class MultiskanFC extends SharedFolderDriver {
    final Logger log = LoggerFactory.getLogger(MultiskanFC.class);

    @Override
    public List<PacketInfo> parseFile(Path file) throws IOException {
        XStream xstream = new XStream();
        xstream.ignoreUnknownElements();
        // 02.07.2020 11:57:33
        String dateFormat = "dd.MM.yyyy HH:mm:ss";
        String timeFormat = "HH:mm:ss";
        String[] acceptableFormats = {dateFormat};
        xstream.registerConverter(new DateConverter(dateFormat, acceptableFormats));

        xstream.addPermission(NoTypePermission.NONE);
        xstream.allowTypesByRegExp(new String[] { ".*" });
        xstream.processAnnotations(Sessions.class);
        String xml = readFileToString(file);
        Sessions convertedSessions = (Sessions) xstream.fromXML(xml);
        System.out.println(convertedSessions);

//        Sessions obj = new Sessions();
//        obj.setSession(new Session("aaa", new Date()));
//
//        String outputXml = xstream.toXML(obj);
//        System.out.println(outputXml);

//        SAXParserFactory spf = SAXParserFactory.newInstance();
//        spf.setNamespaceAware(true);
//        try {
//            SAXParser saxParser = spf.newSAXParser();
//
//            XMLReader xmlReader = saxParser.getXMLReader();
//            xmlReader.setContentHandler(new SaxXMLParser());
//
//            xmlReader.parse(file.getFileName().toAbsolutePath().toString());
//        } catch (IOException | SAXException | ParserConfigurationException e) {
//            log.error("Ошибка разбора файла " + file.getFileName(), e);
//        }

        return null;
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
