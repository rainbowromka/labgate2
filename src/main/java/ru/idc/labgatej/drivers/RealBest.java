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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RealBest
extends SharedFolderDriver {

	final Logger log = LoggerFactory.getLogger(RealBest.class);

	@Override
	public List<PacketInfo> parseFile(Path file) throws FileNotFoundException
	{
		String line;
		String[] words;
		Map<String, String> data = new HashMap<>();
		List<PacketInfo> packets = new ArrayList<>();

		try (Scanner sc = new Scanner(file.toFile()))
		{
			java.util.Date date = null;
			try {
				date = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S")
					.parse(file.getFileName().toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			ResultInfo res;

			while (sc.hasNextLine()) {
				PacketInfo packet = new PacketInfo();

				line = sc.nextLine();
				words = line.split("\\s+");
				//System.out.println(Arrays.toString(line.split("\\s")));
				if (words.length == 3) {

					String id = words[0];

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

					res.setResult(words[2]);
					res.setTest_code("5580");
				}
				packets.add(packet);
			}
		}

		return packets;
	}
}
