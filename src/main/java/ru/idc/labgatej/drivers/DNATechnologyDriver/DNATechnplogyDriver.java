package ru.idc.labgatej.drivers.DNATechnologyDriver;

import com.fasterxml.uuid.Generators;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrCell;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrCreatePlate;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrData;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrPackage;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrPlate;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrSample;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.RealTimePcr.RealTimePcrTest;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders.InquryOrders;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders.RootOrders;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders.SampleOrder;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.orders.ServiceOrder;
import ru.idc.labgatej.drivers.DNATechnologyDriver.entities.results.Result;
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

    final Logger log = LoggerFactory.getLogger(DNATechnplogyDriver.class);

    private static final String DNK_TEST_COVID19 = "COV";

    private static final String REAL_TIME_PCR = "RealTimePCR";
    private static final String BIORAD_CFX_95 = "BioRadCFX95";

    /**
     * Коды тестов в ЛИС.
     */
    private static final String SARS_EQUAL_TEST_CODE = "SARS_EQUAL";
    private static final String SARS_BK_TEST_CODE = "SARS_BK";
    private static final String SARS_GENE_E_TEST_CODE = "SARS_GENE_E";
    private static final String SARS_GENE_N_TEST_CODE = "SARS_GENE_N";
    /**
     * Идентификаторы тестов на приборе.
     */
    private static final String SARS_EQUAL
        = "SARS2,SARS_RNA-IC\\Коронавирусы подобные SARS-CoV";
    private static final String SARS_BK = "SARS2,SARS_RNA-IC\\ВК";
    private static final String SARS_GENE_E
        = "SARS2,SARS_RNA-IC\\Коронавирус SARS-CoV-2, ген E";
    private static final String SARS_GENE_N
        = "SARS2,SARS_RNA-IC\\Коронавирус SARS-CoV-2, ген N";

//    /**
//     * Путь к результатам от Symphony сформированных вручную сканером штрихкода.
//     */
//    Path symphonyDirHand;

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
//        symphonyDirHand = Paths.get(config.getParamValue("simphonyDirHand"));
//        dirSymphonyHandProcessed = symphonyDirHand.resolve("processedFiles");
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
            jaxbContext = JAXBContext.newInstance(RealTimePcrPackage.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            RealTimePcrPackage root =
                (RealTimePcrPackage) jaxbUnmarshaller.unmarshal(f);

            System.out.println("Good");

            RealTimePcrData data;
            RealTimePcrPlate plate;
            List<RealTimePcrCell> cells;

            if ((root == null) || ((data = root.getRealTimePcrData()) == null)
                || ((plate = data.getRealTimePcrPlates()) == null)
                || ((cells = plate.getRealTimePcrCells()) == null))
            {
                return null;
            }

            for (RealTimePcrCell cell : cells) {

                List<RealTimePcrTest> tests;

                if ((cells == null) || (tests = cell.getRealTimePcrTests()) == null)
                {
                    continue;
                }

                Boolean isSarsEquals = null;
//                Boolean isSarsBK = null;
                Boolean isSarsGeneE = null;
                Boolean isSarsGeneN = null;
                Date date = new Date();

                for (RealTimePcrTest test: tests)
                {
                    String testId = test.getId();
                    String value = test.getValue();
                    Boolean rst = null;

                    if (value == null) {
                        rst = null;
                    }

                    if ("+".equals(value))
                    {
                        rst = true;
                    }

                    // один тест почему-то определяется по знаку минус.
                    if ("-".equals(value))
                    {
                        rst = false;
                    }

                    // а остальные по знаку тире.
                    if ("–".equals(value))
                    {
                        rst = false;
                    }

                    if (rst == null) continue;

                    boolean isRealTimePcr = REAL_TIME_PCR.equals(deviceCode);

                    if (SARS_EQUAL.equals(testId))
                    {
                        isSarsEquals = rst;
                        if (isRealTimePcr) {
                            packets.add(createPacket(value, date,
                                cell.getName(), deviceCode,
                                SARS_EQUAL_TEST_CODE));
                        }
                    }
//                    else if (SARS_BK.equals(testId))
//                    {
//                        isSarsBK = rst;
//                        if (isRealTimePcr) {
//                            packets.add(createPacket(value, date,
//                                cell.getName(), deviceCode,
//                                SARS_BK_TEST_CODE));
//                        }
//                    }
                    else if (SARS_GENE_E.equals(testId))
                    {
                        isSarsGeneE = rst;
                        if (isRealTimePcr) {
                            packets.add(createPacket(value, date,
                                cell.getName(), deviceCode,
                                SARS_GENE_E_TEST_CODE));
                        }
                    }
                    else if (SARS_GENE_N.equals(testId))
                    {
                        isSarsGeneN = rst;
                        if (isRealTimePcr) {
                            packets.add(createPacket(value, date,
                                cell.getName(), deviceCode,
                                SARS_GENE_N_TEST_CODE));
                        }
                    }
                }

                if (BIORAD_CFX_95.equals(deviceCode)) {

                    if ((isSarsEquals == null) || (isSarsGeneE == null)
                            || (isSarsGeneN == null) /*|| (isSarsBK == null)*/) {
                        continue;
                    }

                    String rst = "ОТР.";

                    if (isSarsGeneE && isSarsGeneN) {
                        rst = "ПОЛ.";
                    }

                    String id = cell.getName();

                    PacketInfo packet = new PacketInfo();

                    if (id != null) {
                        packet.setHeader(new HeaderInfo(id, false));
                        packet.setOrder(new OrderInfo(id.trim()));
                    }

                    ResultInfo res = new ResultInfo();
                    packet.addResult(res);

                    res.setDevice_name(deviceCode);

                    res.setTest_completed(date);
                    res.setTest_type("SAMPLE");

                    res.setResult(rst);
                    res.setTest_code("339");

                    packets.add(createPacket(rst, date, cell.getName(), deviceCode, "339"));
                }
            }
        } catch (JAXBException e)
        {
            e.printStackTrace();
        }

        return packets;
    }

    private PacketInfo createPacket(
        String result,
        Date completed,
        String id,
        String deviceCode,
        String testCode)
    {
        Date date = new Date();

        PacketInfo packet = new PacketInfo();

        if (id != null ) {
            packet.setHeader(new HeaderInfo(id, false));
            packet.setOrder(new OrderInfo(id.trim()));
        }

        ResultInfo res = new ResultInfo();
        packet.addResult(res);

        res.setDevice_name(deviceCode);

        res.setTest_completed(date);
        res.setTest_type("SAMPLE");

        res.setResult(result);
        res.setTest_code(testCode);

        return packet;
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

        int noCols = 12;
        int noRows = 6;

        RealTimePcrPackage root = new RealTimePcrPackage();
        RealTimePcrCreatePlate plate = new RealTimePcrCreatePlate();
        root.setRealTimePcrCreatePlate(plate);
        List<RealTimePcrSample> realTimePcrSamples = new ArrayList<>();
        root.setSamples(realTimePcrSamples);

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

            sampleTracks.sort((o1, o2) -> {
                if ((o1 == null) || (o2 == null)) {
                    return 0;
                }

                String pos1 = o1.getSampleOutputPos();
                String pos2 = o2.getSampleOutputPos();

                if ((pos1 == null) || (pos2 == null)) {
                    return 0;
                }

                String[] posXY1 = pos1.split(":");
                String[] posXY2 = pos2.split(":");

                if ((posXY1.length != 2) || (posXY2.length != 2)) {
                    return 0;
                }

                int result = posXY1[1].compareTo(posXY2[1]);

                if (result == 0) {
                    return posXY1[0].compareTo(posXY2[0]);
                }

                return result;
            });

            int curX = 1;
            int curY = 1;
            for (SampleTrack sampleTrack: sampleTracks)
            {
                RealTimePcrCell cell = new RealTimePcrCell();
                cells.add(cell);

                String barCode = sampleTrack.getSampleCode();

                cell.setName(barCode);

                cell.setX(Integer.toString(curX));
                cell.setY(Integer.toString(curY));

                if (++curY > noRows) {
                    curY = 1;
                    if (++curX > noCols) break;
                }

                List<RealTimePcrTest> tests = new ArrayList<>();
                cell.setRealTimePcrTests(tests);
                tests.add(buildRealTimePcrTest("SARS2,SARS_RNA-IC\\Коронавирусы подобные SARS-CoV"));
                tests.add(buildRealTimePcrTest("SARS2,SARS_RNA-IC\\ВК"));
                tests.add(buildRealTimePcrTest("SARS2,SARS_RNA-IC\\Коронавирус SARS-CoV-2, ген E"));
                tests.add(buildRealTimePcrTest("SARS2,SARS_RNA-IC\\Коронавирус SARS-CoV-2, ген N"));

                RealTimePcrSample realTimePcrSample = new RealTimePcrSample();
                realTimePcrSample.setName(barCode);
                realTimePcrSample.setDisplayName(barCode);
                realTimePcrSample.setPatient("");

                realTimePcrSamples.add(realTimePcrSample);
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

    private RealTimePcrTest buildRealTimePcrTest(String id)
    {
        RealTimePcrTest realTimePcrTest = new RealTimePcrTest();
        realTimePcrTest.setId(id);
        return realTimePcrTest;
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


