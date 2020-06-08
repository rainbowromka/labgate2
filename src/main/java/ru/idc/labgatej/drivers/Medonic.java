package ru.idc.labgatej.drivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.IDriver;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.nio.file.FileVisitResult.*;

public class Medonic implements IDriver {
	final Logger log = LoggerFactory.getLogger(Medonic.class);

	private DBManager dbManager;
	private Path dir2scan;
	private Path dirProcessed;
	private String deviceCode;

	@Override
	public void init(DBManager dbManager, Configuration config) {
		dir2scan = Paths.get(config.getParamValue("dir2scan"));
		dirProcessed = dir2scan.resolve("processedFiles");
		deviceCode = config.getParamValue("code");
		try {
			if (!Files.exists(dirProcessed)) {
				Files.createDirectory(dirProcessed);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loop() throws IOException, InterruptedException {
		while (true) {
			scanFiles(dir2scan);
			Thread.sleep(30000);
		}
	}

	private void scanFiles(Path dir2scan) throws IOException {
		Files.walkFileTree(dir2scan, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir2scan.equals(dir)) {
					return CONTINUE;
				} else {
					return SKIP_SUBTREE;
				}
			}

			@Override
			public FileVisitResult visitFile(Path file,	BasicFileAttributes attr) {
				try {
					processFile(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return CONTINUE;
			}
		});
	}

	private void processFile (Path file) throws IOException {
		PacketInfo packetInfo = parseFile(file);
		if (packetInfo != null) {
			dbManager.saveResults(packetInfo, false);
//			Files.move(file, Paths.get(dirProcessed.toFile().getPath() + "\\" + file.getFileName()),
//				StandardCopyOption.REPLACE_EXISTING);
//			System.out.println(file.toAbsolutePath());
		} else {
			log.error("Ошибка разбора файла " + file.getFileName());
		}
	}

	private PacketInfo parseFile(Path file) throws FileNotFoundException {
		String line;
		String[] words;
		Map<String, String> data = new HashMap<>();
		PacketInfo packet = new PacketInfo();

		try (Scanner sc = new Scanner(file.toFile()))
		{
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				words = line.split("\\s");
				//System.out.println(Arrays.toString(line.split("\\s")));
				if (words.length == 2) {
					data.put(words[0].trim().toUpperCase(), words[1].trim());
				}
			}
		}

		String[] codes = {"RBC", "MCV", "HCT", "MCH", "MCHC", "RDWR", "RDWA", "PLT", "MPV", "PCT", "PDW", "LPCR", "HGB", "WBC",
			"LA", "MA", "GA", "LR", "MR", "GR"};

		if (data.get("ID") != null) {
			packet.setHeader(new HeaderInfo(data.get("ID"), false));
			packet.setOrder(new OrderInfo(data.get("ID").trim()));
		}
		ResultInfo res;
		java.util.Date date = null;
		if (data.get("DATE") != null) {
			try {
				date = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(data.get("DATE"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String testType = "SAMPLE";
		if (data.get("SORC") != null) {
			if (!data.get("SORC").equals("0")) {
				testType = "CONTROL";
			}
		}

		for (String code: codes) {
			if (data.get(code) != null) {
				res = new ResultInfo();
				packet.addResult(res);
				res.setDevice_name(deviceCode);
				if (date != null) {
					res.setTest_completed(date);
				}
				res.setTest_type(testType);
				res.setSample_type("SE");

				res.setResult(data.get(code));
				res.setTest_code(code);
			}
		}

		return packet;
	}

	@Override
	public void close() {

	}
}
