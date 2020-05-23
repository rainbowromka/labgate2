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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.idc.labgatej.base.Codes.*;

public class ProtocolASTM implements Protocol {
	private static Logger logger = LoggerFactory.getLogger(ProtocolASTM.class);
	// 240 символов на сообщение в одном фрейме

	//<STX>1H|\^&|||ASTM-Host|||||CIT||P||20120219111500<CR>P|1<CR>O|1|923501||^^^CL-S\^^^CREA|||||||A<CR>L|1|F<CR><ETX>80<CR><LF>
	public String makeOrder(List<Order> orders) {
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
			msg.append("^^^").append(order.getTestId()).append("\\");
		}
		msg.append("|||||||A<CR>"); // A - Action Code
		idx++;

		long devInst = -1;
		// добавляем задания на аликвоты
		for (Order order : orders) {
			if (order.getIsAliquot()) {
				if (order.getDeviceInstanceId() != devInst) {
					devInst = order.getDeviceInstanceId();
					if (idx != 2) {
						// закрываем предыдущую O-запись
						msg.append("|||||||A<CR>");
					}
					order.setAliquotBarcode(mainBarcode + "." + (idx - 1));
					msg.append("O|").append(idx).append("|").append(order.getAliquotBarcode())
						.append("|").append(mainBarcode).append("|");
					idx++;
				}
				msg.append("^^^").append(order.getTestId()).append("\\");
			}
		}
		if (devInst != -1) {
			// закрываем последнюю O-запись
			msg.append("|||||||A<CR>");
		}

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
	public PacketInfo parseMessage(String msg) {
		PacketInfo packetInfo = new PacketInfo();
		while (msg.indexOf(""+ ETB_) > 0) {
			msg = msg.replace(msg.substring(msg.indexOf(""+ ETB_), msg.indexOf(""+ ETB_) + 6), "");
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
						packetInfo.addResult(parseResult(line));
						break;
				}
			}
		}
		return packetInfo;
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
	public ResultInfo parseResult(String msg) {
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
			result.setResult(matcher.group(2));
			result.setNormal_range_flag(matcher.group(5));
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
						logger.error("", e);
					}
				}
			}

			result.setUnits(matcher.group(3));
			s = matcher.group(11);

			try {
				Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(s);
				result.setTest_completed(date);
			} catch (ParseException e) {
				logger.error("", e);
			}
		}
		return result;
	}
}
