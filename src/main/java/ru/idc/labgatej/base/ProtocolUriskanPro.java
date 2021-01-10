package ru.idc.labgatej.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProtocolUriskanPro implements Protocol {
	private static Logger logger = LoggerFactory.getLogger(ProtocolUriskanPro.class);

	public String makeOrder(List<Order> orders) {
		return null;
	}

	@Override
	public PacketInfo parseMessage(String msg) {
		PacketInfo packetInfo = new PacketInfo();
		packetInfo.addResult(parseResults(msg));

		return packetInfo;
	}

	@Override
	public HeaderInfo parseHeader(String msg) {
		return null;
	}

	@Override
	public OrderInfo parseOrder(String msg) {
		return null;
	}

	@Override
	public List<ResultInfo> parseResults(String msg) {
		List<ResultInfo> results = new ArrayList<>();
		String s;
		final Pattern pattern = Pattern.compile(
			"^Operator :(.*?)\\r\\n" +
				"Date :(.*?)\\r\\n" +
				"ID_NO:(.*?)\\r\\n" +
				"Ward :(.*?)\\r\\n" +
				"Name :(.*?)\\r\\n"+
				"BLD\\s+[+-]+\\s+(.*?)\\r\\n" +
				"BIL\\s+[+-]+\\s+(.*?)\\r\\n" +
				"URO\\s+[+-]+\\s+(.*?)\\r\\n"+
				"KET\\s+[+-]+\\s+(.*?)\\r\\n" +
				"PRO\\s+[+-]+\\s+(.*?)\\r\\n" +
				"NIT\\s+[+-]+\\s+(.*?)\\r\\n" +
				"GLU\\s+[+-]+\\s+(.*?)\\r\\n" +
				"p.H(.*?)\\r\\n"+
				"S.G(.*?)\\r\\n" +
				"LEU\\s+[+-]+\\s+(.*?)\\r\\n" +
				"VTC\\s+[+-]+\\s+(.*?)\\r\\n" +
				"COL(.*?)\\r\\n$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

		final Pattern idPattern = Pattern.compile("^(.*?)-(.*?)$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(msg);
		Matcher idMatcher;

		String[] codes = {"BLD", "BIL", "URO", "KET",	"PRO", "NIT", "GLU", "p.H", "S.G", "LEU", "VTC", "COL"};
		ResultInfo result;
		if (matcher.find()) {
			Date date = null;
			try {
				date = new SimpleDateFormat("MMMM-dd-yyyy hh:mm:ss", Locale.US).parse(matcher.group(1).trim());
			} catch (ParseException e) {
				logger.error("", e);
			}

			idMatcher = idPattern.matcher(matcher.group(3).trim());
			String sampleId = null;
			if (idMatcher.find()) {
				sampleId = idMatcher.group(2).trim();
				if (sampleId.isEmpty()) {
					sampleId = idMatcher.group(1).trim();
				}
			}

			for (int i = 0; i < 11; i++) {
				result = new ResultInfo();
				result.setTest_completed(date);
				result.setResult(matcher.group(i + 6).trim());
				result.setTest_code(codes[i]);
				result.setSample_id(sampleId);
				result.setTest_type("SAMPLE");
				results.add(result);
			}
		} else {
			logger.error("Не смогли разобрать сообщение: " + msg);
		}
		return results;
	}
}
