package ru.idc.labgatej.drivers.DNATechnologyDriver;

import com.fasterxml.uuid.Generators;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.Utils;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrCell;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrCreatePlate;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrPackage;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrTest;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders.InquryOrders;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders.RootOrders;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders.SampleOrder;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders.ServiceOrder;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Inquiry;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Result;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Root;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Sample;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Service;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack.BatchTrack;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack.FullPlateTrack;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.fullPlateTrack.SampleTrack;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.rack.SymphonyRackOld;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.symphony.rack.SymphonyTestTube;
import ru.idc.labgatej.drivers.common.SharedFolderDriver;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

public class DNATechnplogyDriver
extends SharedFolderDriver {

    private static final String DNK_TEST_COVID19 = "COV";
    final Logger log = LoggerFactory.getLogger(DNATechnplogyDriver.class);

    /**
     * Путь к результатам от Symphony сформированных вручную сканером штрихкода.
     */
    Path symphonyDirHand;

    /**
     * Путь к результатам от Symphony полученных напрямую от прибора.
     */
    Path symphonyDir;

    /**
     * Путь к папке, куда необходимо помещать задания для ДНК Технологии
     */
    Path ordersDir;

    /**
     * Путь к обработанным файлам Symhony, после обработки файлов, обработанных
     * вручную сканером штрихкода.
     */
    Path dirSymphonyHandProcessed;

    /**
     * Путь к обработанным файлам Symhony, после обработки файлов, полученных
     * напрямую с прибора.
     */
    Path dirSymphonyProcessed;


    @Override
    public void init(DBManager dbManager, Configuration config) {
        super.init(dbManager, config);
        symphonyDirHand = Paths.get(config.getParamValue("simphonyDirHand"));
        dirSymphonyHandProcessed = symphonyDirHand.resolve("processedFiles");
        symphonyDir = Paths.get(config.getParamValue("simphonyDir"));
        dirSymphonyProcessed = symphonyDir.resolve("processedFiles");
        try {
            if (!Files.exists(dirSymphonyHandProcessed)) {
                Files.createDirectory(dirSymphonyHandProcessed);
            }
            if (!Files.exists(dirSymphonyProcessed)) {
                Files.createDirectory(dirSymphonyProcessed);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ordersDir = Paths.get(config.getParamValue("ordersDir"));
    }

    protected void scanFiles(
            Path directory,
            Consumer<Path> consumer)
            throws IOException
    {
        // сам драйвер проверяет, есть ли уже выполненые задания амплификатором.
        super.scanFiles(directory, consumer);
//        super.scanFiles(symphonyDirHand, this::processSymphonyHandFile);
        super.scanFiles(symphonyDir, this::processSymphonyFile);
    }

    @Override
    public List<PacketInfo> parseFile(Path file) {

        List<PacketInfo> packets = new ArrayList<>();

        File f = new File(file.toFile().getAbsolutePath());
        JAXBContext jaxbContext;
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

    private void processSymphonyFile(
        Path file)
    {
        FullPlateTrack fullPlateTrack = null;
        try {
            if ((fullPlateTrack = parseSymphonyFile(file)) != null ) {
                makeDNKTechnologyOrdersFromFullPlateTrack(fullPlateTrack);
            }
            Files.move(file, Paths.get(dirSymphonyProcessed.toFile().getPath() + "/" + file.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println(file.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает XML файл задания для программы realtime_PCR(ДНК "Технологии")
     *
     * @param fullPlateTrack результат раскапки пробирок в приборе Symphony.
     */
    private void makeDNKTechnologyOrdersFromFullPlateTrack(
        FullPlateTrack fullPlateTrack)
    {
        List<BatchTrack> batchTracks = fullPlateTrack.getBatchTrack();
        if (batchTracks == null) return;

        RealTimePcrPackage root = new RealTimePcrPackage();
        RealTimePcrCreatePlate plate = new RealTimePcrCreatePlate();
        root.setRealTimePcrCreatePlate(plate);

        for (BatchTrack batchTrack: batchTracks)
        {
            if (batchTrack == null) continue;
            List<SampleTrack> sampleTracks = batchTrack.getSampleTracks();
            if (sampleTracks == null) continue;

            plate.setPlate(fullPlateTrack.getPlateID());
            plate.setSize_x(fullPlateTrack.getNofCols());
            plate.setSize_y(fullPlateTrack.getNofRows());
            List<RealTimePcrCell> cells = new ArrayList<>();
            plate.setRealTimePcrCells(cells);

            for (SampleTrack sampleTrack: sampleTracks)
            {
//                if (sampleTrack == null) continue;
                String[] samplePosition = sampleTrack.getSampleOutputPos()
                    .split(":");
                RealTimePcrCell cell = new RealTimePcrCell();
                cells.add(cell);

                cell.setName(sampleTrack.getSampleCode());
                cell.setX(samplePosition[1]);
                cell.setY(Utils.letteToNumnber(samplePosition[0]
                    .toCharArray()[0]).toString());

                List<RealTimePcrTest> tests = new ArrayList<>();
                cell.setRealTimePcrTests(tests);
                tests.add(new RealTimePcrTest("SARS2,SARS_RNA-IC\\Коронавирусы подобные SARS-CoV", null, null));
                tests.add(new RealTimePcrTest("SARS2,SARS_RNA-IC\\ВК", null, null));
                tests.add(new RealTimePcrTest("SARS2,SARS_RNA-IC\\Коронавирус SARS-CoV-2, ген E", null, null));
                tests.add(new RealTimePcrTest("SARS2,SARS_RNA-IC\\Коронавирус SARS-CoV-2, ген N", null, null));
            }
        }

        JAXBContext jaxbContext;


        String filename = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS")
                .format(new Date()) + "_"
                + Generators.timeBasedGenerator().generate().toString()
                + ".xml";

        File file = new File(ordersDir.toString(), filename);

        try (FileOutputStream os = new FileOutputStream(file)){
            StreamResult sr = new StreamResult(os);
            jaxbContext = JAXBContext.newInstance(RealTimePcrPackage.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "windows-1251");
            marshaller.marshal(root, sr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private FullPlateTrack parseSymphonyFile(
        Path file)
    {
        FullPlateTrack fullPlateTrack = null
                ;
        File f = new File(file.toFile().getAbsolutePath());
        JAXBContext jaxbContext;
        try
        {
            jaxbContext = JAXBContext.newInstance(FullPlateTrack.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            fullPlateTrack = (FullPlateTrack) jaxbUnmarshaller.unmarshal(f);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return fullPlateTrack;
    }

    private void processSymphonyHandFile(
        Path file)
    {
        String line;
        String[] words;
        SymphonyRackOld rack = new SymphonyRackOld();

        try (
                Scanner sc = new Scanner(file.toFile(),"UTF-8")
        ) {

            List<SymphonyTestTube> listTubes = new ArrayList<>();
            rack.setTestTubes(listTubes);

            while (sc.hasNextLine()) {
                line = sc.nextLine();
                words = line.split("[\\t\\s]");
                if (words.length >= 2) {
                    listTubes.add(new SymphonyTestTube(words[0], words[1]));
                }
            }

            makeDNKTechnologyOrdersFromHand(rack);

            Files.move(file, Paths.get(dirSymphonyHandProcessed.toFile().getPath() + "/" + file.getFileName()),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (FileNotFoundException e) {
            log.error("Ошибка открытия файла", e);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Нельзя переместить файл", e);
        }
    }

    private void makeDNKTechnologyOrdersFromHand(
        SymphonyRackOld rack)
    {
        RootOrders root = new RootOrders();
        List<InquryOrders> inquries = new ArrayList<>();
        root.setInquryOrders(inquries);

        for (SymphonyTestTube tube: rack.getTestTubes()){
            InquryOrders inqury = new InquryOrders();
            inquries.add(inqury);

            UUID id = Generators.timeBasedGenerator().generate();
            inqury.setId(id.toString());

            SampleOrder sample = new SampleOrder();
            inqury.setSampleOrder(sample);
            sample.setId(id.toString());
            sample.setCode(tube.getId());
            sample.setMatID("21");

            ServiceOrder service = new ServiceOrder();
            sample.setServiceOrders(ImmutableList.of(service));
            String serviceId;
            switch (tube.getTestCode()) {
                case "222": serviceId = "13";
                default: serviceId = DNK_TEST_COVID19;
            }
            service.setId(serviceId);
        }

        JAXBContext jaxbContext;

        String filename = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS")
            .format(new Date()) + ".xml";

        File file = new File(ordersDir.toString(), filename);

        try (FileOutputStream os = new FileOutputStream(file)){
            jaxbContext = JAXBContext.newInstance(RootOrders.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(root, new StreamResult(os));
        } catch (JAXBException | IOException e) {
            log.error("", e);
        }
    }
}


