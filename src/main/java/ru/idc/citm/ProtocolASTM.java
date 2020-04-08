package ru.idc.citm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProtocolASTM implements Protocol {
	private int frameIdx = 0;

	//<STX>1H|\^&|||ASTM-Host|||||CIT||P||20120219111500<CR>P|1<CR>O|1|923501||^^^CL-S\^^^CREA|||||||A<CR>L|1|F<CR><ETX>80<CR><LF>
	public String makeOrder(List<String> barcodes) {
		frameIdx++;
		if (frameIdx > 7) {
			frameIdx = 1;
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHms");
		String msg = frameIdx + "H|\\^&|||ASTM-Host|||||CIT||P||" + dtf.format(LocalDateTime.now()) +
			"<CR>P|1<CR>O|1|923501||^^^CHO2L\\^^^CER|||||||A<CR>L|1|F<CR><ETX>";
		msg = msg + Utils.calcCRC8(Codes.makeSendable(msg));

		return "<STX>" + msg  + "<CR><LF>";
	}
}
