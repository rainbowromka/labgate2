package ru.idc.labgatej;

import org.junit.Test;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.ProtocolASTM;


public class ProtocolASTMTest {

	@Test
	public void testParseMessage() {
		ProtocolASTM astm = new ProtocolASTM();
		String msg = Codes.makeSendable("1H|\\^&|26973||^^^|||||||P||20200413110453<CR><ETX>E4<CR><LF>" +
			"2P|1||PID_1||Surname^Name||19700101|M|||||||||||||||20200413095339||0||||||||00001<CR><ETX>B0<CR><LF>" +
			"3O|1|99900001||ALL|R|20200413095339|||||X||||1||||||||||F<CR><ETX>3E<CR><LF>" +
			"4R|1|^^^0030^^^^COBAS_CCEE^^^^^^|73.000|Вµmol/L||||F||&S&SYSTEM^||20200515051512|COBAS_CCEE<CR><ETX>F3<CR><LF>" +
			"5L|1|N<CR><ETX>03<CR><LF>");

//		String msg = Codes.makeSendable("1H|\\^&|26973||^^^|||||||P||20200413110453<CR><ETX>E4<CR><LF>" +
//			"2P|1||PID_1||Surname^Name||19700101|M|||||||||||||||20200413095339||0||||||||00001<CR><ETX>B0<CR><LF>" +
//			"3O|1|99900001||ALL|R|20200413095339|||||X||||1||||||||||F<CR><ETX>3E<CR><LF>" +
//			"4R|1|^^^ALB2^^^^^^^|100.000|||N||F||^ROCHE||20200413110103|<CR><ETX>24<CR><LF>" +
//			"5C|1|L|R^E|G<CR><ETX>31<CR><LF>" +
//			"6R|2|^^^ALTL^^^^^^^|12.000|||N||F||^ROCHE||20200413110103|<CR><ETX>25<CR><LF>" +
//			"7C|1|L|R^E|G<CR><ETX>33<CR><LF>" +
//			"0R|3|^^^AMYL2^^^^^^^|56.000|||N||F||^ROCHE||20200413110103|<CR><ETX>60<CR><LF>" +
//			"1C|1|L|R^E|G<CR><ETX>2D<CR><LF>" +
//			"2R|4|^^^BILD2^^^^^^^|78.000|||N||F||^ROCHE||20200413110103|<CR><ETX>4F<CR><LF>" +
//			"3C|1|L|R^E|G<CR><ETX>2F<CR><LF>" +
//			"4R|5|^^^BILT2^^^^^^^|135.000|||N||F||^ROCHE||20200413110103|<CR><ETX>8C<CR><LF>" +
//			"5C|1|L|R^E|G<CR><ETX>31<CR><LF>" +
//			"6R|6|^^^CHO2L^^^^^^^|66.000|||N||F||^ROCHE||20200413110103|<CR><ETX>5D<CR><LF>" +
//			"7C|1|L|R^E|G<CR><ETX>33<CR><LF>" +
//			"0R|7|^^^TSH^^^^^^^|89.000|||N||F||^ROCHE||20200413110103|<CR><ETX>F4<CR><LF>" +
//			"1C|1|L|R^E|G<CR><ETX>2D<CR><LF>" +
//			"2L|1|N<CR><ETX>05<CR><LF>");

		//msg = "4R|1|^^^Na^1^^^10101^^^|124.800|||N||F||&S&SYSTEM^System||20200416151944|10101<CR><ETX>A4<CR><LF>";
		astm.parseMessage(Codes.makeSendable(msg));
	}
}
