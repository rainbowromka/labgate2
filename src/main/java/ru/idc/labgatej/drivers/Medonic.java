package ru.idc.labgatej.drivers;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.drivers.common.SharedFolderDriver;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Medonic
extends SharedFolderDriver
{
	final Logger log = LoggerFactory.getLogger(Medonic.class);

	@Override
	public List<PacketInfo> parseFile(Path file)
	{
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

		return ImmutableList.of(packet);
	}
}
