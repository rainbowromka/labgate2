package ru.idc.labgatej.base.protocols;

import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.Utils;
import ru.idc.labgatej.model.ArchiveFlag;
import ru.idc.labgatej.model.ArchiveInfo;
import ru.idc.labgatej.model.HeaderInfo;
import ru.idc.labgatej.model.ManufacturerRecord;
import ru.idc.labgatej.model.Order;
import ru.idc.labgatej.model.OrderInfo;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.idc.labgatej.base.Codes.*;

@Slf4j
public class ProtocolCITM_ASTM implements Protocol<List<Order>> {
	// 240 символов на сообщение в одном фрейме


	private String getTestPostfix(String material) {
		if (material == null) return "";
		switch (material.toUpperCase()) {
			case "UR": return "_U";

			default: return "";
		}
	}

	//<STX>1H|\^&|||ASTM-Host|||||CIT||P||20120219111500<CR>P|1<CR>O|1|923501||^^^CL-S\^^^CREA|||||||A<CR>L|1|F<CR><ETX>80<CR><LF>
	public String makeOrder(List<Order> orders) {
		// флаг что это Отказ
		boolean isAbort = orders.stream().anyMatch(o -> o.getTaskType() == 1);
		final int maxSize = 6900;
		int frameIdx = 0;
		if (orders.isEmpty()) return "";

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHms");
		StringBuilder msg = new StringBuilder("H|\\^&|||ASTM-Host|||||CIT||P||" + dtf.format(LocalDateTime.now()) + "<CR>");
		// информация о пациенте
		msg.append("P|1<CR>");
		//Order order = orders.get(0);
		//P|1|923610|4637463G72||Andrews^Pamela||19930101|F||||||||||Diabetes|||||20000218102000||||||||||Urol
		//msg.append("P|1|").append(order.getCartnum()).append("|||").append(order.getFam()).append("^") .append("<CR>");
		// заказанные тесты
		int idx = 1;
		// Для первичного образца нужно отправить все-все тесты
		String mainBarcode = orders.get(0).getBarcode();
		msg.append("O|").append(idx).append("|").append(mainBarcode).append("||");
		for (Order order : orders) {
			if (order.getIsAliquot() && order.isManualAliquot()) {
				continue;
			}
			msg.append("^^^").append(order.getTestId()).append(getTestPostfix(order.getMaterial())).append("\\");
		}
		if (isAbort) {
			msg.append("|||||||C<CR>"); // C - Action Code = Cancel
		} else {
			msg.append("|||||||A<CR>"); // A - Action Code = Add
		}
		idx++;

// задания на аликвоты
		//long devInst = -1;
		long routeId = -1;
		// добавляем задания на аликвоты
		if (!isAbort) {
			for (Order order : orders) {
				if (order.getIsAliquot() && !order.isManualAliquot()) {
					if (order.getRouteId() != routeId) {
						routeId = order.getRouteId();
						if (idx != 2) {
							// закрываем предыдущую O-запись
							msg.append("|||||||A<CR>");
						}
						order.setAliquotBarcode(mainBarcode + "." + (idx - 1));
						msg.append("O|").append(idx).append("|").append(order.getAliquotBarcode())
							.append("|").append(mainBarcode).append("|");
						idx++;
					}
					msg.append("^^^").append(order.getTestId()).append(getTestPostfix(order.getMaterial())).append("\\");
				}
			}
		}
		if (routeId != -1) {
			// закрываем последнюю O-запись
			msg.append("|||||||A<CR>");
		}
// закончились задания на аликвоты

		// терминатор
		msg.append("L|1|F<CR>");

		String bigMsg = Codes.makeSendable(msg.toString());
		List<String> frames = new ArrayList<>();
		String s;
		while (bigMsg.length() > 0) {
			frameIdx++;
			if (frameIdx > 7) {
				frameIdx = 0;
			}
			if (bigMsg.length() >= maxSize) {
				s = bigMsg.substring(0, maxSize);
				bigMsg = bigMsg.substring(maxSize);
			} else {
				s = bigMsg;
				bigMsg = "";
			}

			if (bigMsg.length() > 0) {
				s = frameIdx + s + ETB;
			} else {
				s = frameIdx + s + ETX;
			}
			// добавляем контрольную сумму
			s = s + Utils.calcCRC8(s);

			frames.add(STX + s + CR + LF);
		}

		return makePrintable(String.join("", frames));

		// Action Code
//		C - Cancel request for the battery or tests named. It is not possible to cancel a test if it has a result and the
//		check box Host configuration > Host interface > [host] > Parameters > Retain results on sample removal is checked.
//
//		A - Add the requested tests or batteries to the existing sample with the patient and sample (specimen) identifiers
//		and date-time given in this record. If test already exists, cobas IT middleware does nothing.
//
//		N - New sample. If a sample with the same sample ID exists, cobas IT middleware deletes the previous tests,
//		and adds the new tests to the sample.
	}

	@Override
	public List<PacketInfo> parseMessage(String msg) {
		List<PacketInfo> result = new ArrayList<>();
		PacketInfo packetInfo = new PacketInfo();
		result.add(packetInfo);
		int idx;
		while (msg.indexOf(""+ ETB_) > 0) {
			idx = msg.indexOf(""+ ETB_) + 6;
			if (idx > msg.length()) {
				idx = msg.length() - 1;
			}
			msg = msg.replace(msg.substring(msg.indexOf(""+ ETB_), idx), "");
		}

		String[] lines = msg.replace(""+ ETB_, "").replace("\r\n", "\r").split("\r");
		final Pattern pattern = Pattern.compile("^\\d?(.)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher matcher;

		for (String line : lines) {
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				switch (matcher.group(1)) {
					case "H":
						packetInfo.setHeader(parseHeader(line));
						break;

					case "O":
						packetInfo.setOrder(parseOrder(line));
						break;

					case "R":
						packetInfo.addResult(parseResults(line));
						break;

					case "M":
						packetInfo.addManufacturerRecord(parseManufacturerRecord(line));
				}
			}
		}
		return result;
	}

	@Override
	public HeaderInfo parseHeader(String msg) {
		HeaderInfo result = null;
		final Pattern pattern = Pattern.compile("^\\dH\\|.{3}\\|(\\d*)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(msg);
		// 1H|\^&|26973||^^^|||||||P||20200413110453<CR><ETX>E4<CR><LF>
		// 1H|\^&|28189||^^^|||||||Q||20200417122246<CR><ETX>ED<CR><LF>
		if (matcher.find()) {
			result = new HeaderInfo(matcher.group(1), "Q".equalsIgnoreCase(matcher.group(10)));
		}
		return result;
	}

	// 3O|1|20005317||ALL|?|20200516131727|||||X||||1||||||||||F<CR><ETX>1D<CR><LF>
	@Override
	public OrderInfo parseOrder(String msg) {
		OrderInfo result = null;
		final Pattern pattern = Pattern.compile("^O\\|(\\d*)\\|(.*?)\\|",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(msg);
		if (matcher.find()) {
			result = new OrderInfo((matcher.group(2)));
		}
		return result;
	}

	@Override
	public List<ResultInfo> parseResults(String msg) {
		ResultInfo result = null;
		String s;
		final Pattern pattern = Pattern.compile("^R\\|\\d+\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		final Pattern utiPattern = Pattern.compile("^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(msg);
		Matcher utiMatcher;
		// R|1|^^^BE(B)||mmol/L||C||||||20150819144937 \n"
		if (matcher.find()) {
			result = new ResultInfo();
			result.setResult(matcher.group(2).trim());
			String outofrange = null;
			if (result.getResult().contains("<")) {
				outofrange = "<";
				result.setResult(result.getResult().replace("<", "").trim());
			} else if (result.getResult().contains(">")) {
				outofrange = ">";
				result.setResult(result.getResult().replace(">", "").trim());
			}
			if (!matcher.group(5).isEmpty()) {
				result.setNormal_range_flag(matcher.group(5));
			}
			if (outofrange != null) {
				result.setNormal_range_flag(outofrange);
			}
			result.setTest_type("SAMPLE");
			result.setResult_status(matcher.group(7));
			result.setDevice_name(matcher.group(12));
			s = matcher.group(1); // group - 2
			utiMatcher = utiPattern.matcher(s);
			if (utiMatcher.find()) {
				result.setTest_code(utiMatcher.group(4));
				if (!utiMatcher.group(5).isEmpty()) {
					try {
						result.setDilution_factor(Double.valueOf(utiMatcher.group(5)));
					} catch (NumberFormatException e) {
						log.error("", e);
					}
				}
			}

			result.setUnits(matcher.group(3));
			s = matcher.group(11);

			try {
				Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(s);
				result.setTest_completed(date);
			} catch (ParseException e) {
				log.error("", e);
			}
		}
		return Collections.singletonList(result);
	}

	public ManufacturerRecord parseManufacturerRecord(String msg) {
		final Pattern m1Pattern = Pattern.compile("^M\\|\\d+\\|(.*?)\\|(.*?)\\|(.*?)$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		final Pattern eventTypePattern = Pattern.compile("^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)\\^(.*?)$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		final Pattern m2Pattern = Pattern.compile("^M\\|\\d+\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)$",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

		String s;

		Matcher matcher = m2Pattern.matcher(msg);
		if (matcher.find() && matcher.group(11) != null && matcher.group(12) != null) {
			return new ArchiveInfo(matcher.group(11).trim(), Integer.parseInt(matcher.group(12).trim()));
		} else {
			matcher = m1Pattern.matcher(msg);
			if (matcher.find()) {
				s = matcher.group(2);
				matcher = eventTypePattern.matcher(s);
				if (matcher.find()) {
					s = matcher.group(7);
					if ("ARCHIV".equals(s)) {
						return new ArchiveFlag();
					}
				}
			}
		}

		return null;
	}
}
