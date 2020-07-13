package ru.idc.labgatej.drivers.DNATechnologyDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Inquiry;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Result;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Root;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Sample;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Service;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.SymphonyRack;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.SymphonyTestTube;
import ru.idc.labgatej.drivers.RealBest;
import ru.idc.labgatej.drivers.common.SharedFolderDriver;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DNATechnplogyDriver
extends SharedFolderDriver {

    final Logger log = LoggerFactory.getLogger(DNATechnplogyDriver.class);

    /**
     * Путь к результатам от Symphony
     */
    Path symphonyDir;

    /**
     * Путь к папке, куда необходимо помещать задания для ДНК Технологии
     */
    Path ordersDir;

    /**
     * Путь к обработанным файлам Symhony, после обработки файлов,
     */
    Path dirSymphonyProcessed;


    @Override
    public void init(DBManager dbManager, Configuration config) {
        super.init(dbManager, config);
        symphonyDir = Paths.get(config.getParamValue("simphonyDir"));
        dirSymphonyProcessed = symphonyDir.resolve("processedFiles");
        ordersDir = Paths.get(config.getParamValue("ordersDir"));
    }

    @Override
    public List<PacketInfo> parseFile(Path file) {

        final Logger log = LoggerFactory.getLogger(RealBest.class);

        String line;
        String[] words;
        Map<String, String> data = new HashMap<>();
        List<PacketInfo> packets = new ArrayList<>();

        File f = new File(file.toFile().getAbsolutePath());
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(Root.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Root root = (Root) jaxbUnmarshaller.unmarshal(f);

            for (Inquiry inquiry : root.getInquiries()) {
                if ((inquiry != null) && inquiry.getSample() != null) {
                    Sample sample = inquiry.getSample();

                    if (sample != null) {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
                                    .parse(sample.getSamplingDate());
                        } catch (ParseException e) {
                            log.error("Дата не парсится: ", e);
                        }

                        String id = sample.getCode();

                        PacketInfo packet = new PacketInfo();

                        if (id != null) {
                            packet.setHeader(new HeaderInfo(id, false));
                            packet.setOrder(new OrderInfo(id.trim()));
                        }

                        appendResultsToPacket(packet, sample);
                        packets.add(packet);
                    }
                }
            }


        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return packets;

    }

    private void appendResultsToPacket(
        PacketInfo packet,
        Sample sample)
    {
        Service service = sample.getService();

        if (service != null) {
            for (Result result : sample.getService().getResults()) {
                ResultInfo res = new ResultInfo();
                packet.addResult(res);
                res.setDevice_name(deviceCode);

                //yyyy-MM-dd'T'hh:mm:ss.S
                if (result.getDoneTime() != null) {
                    try {
                        res.setTest_completed(
                                new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S")
                                        .parse(result.getDoneTime()));
                    } catch (ParseException e) {
                        log.error("Дата не читвается: ", e);
                    }
                }

                res.setTest_type("SAMPLE");
                res.setResult(result.getValue());

                if (service.getCode() != null) {
                    res.setTest_code(service.getCode());
                }
            }
        }
    }

    protected void scanFiles(
        Path directory,
        Consumer<Path> consumer)
    throws IOException
    {
        // сам драйвер проверяет, есть ли уже выполненые задания амплификатором.
        super.scanFiles(directory, consumer);
        super.scanFiles(symphonyDir, this::processSymphonyFile);
    }

    private void processSymphonyFile (
        Path file)
    {
        SymphonyRack rack = new SymphonyRack();
        try {
            for (SymphonyTestTube testTube : rack.getTestTubes()) {
                if (testTube != null) {
                    .... здесь мы формируем задание для нашей пробирки
                } else {
                    log.error("Ошибка разбора файла " + file.getFileName());
                }
            }

            Files.move(file, Paths.get(dirSymphonyProcessed.toFile().getPath() + "/" + file.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println(file.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<PacketInfo> parseSimphonyFile(
        Path file)
    {


    }
}


