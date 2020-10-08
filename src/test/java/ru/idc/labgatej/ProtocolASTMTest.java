package ru.idc.labgatej;

import org.junit.Test;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.ProtocolASTM;
import ru.idc.labgatej.model.PacketInfo;
import ru.idc.labgatej.model.ResultInfo;

import java.util.List;

public class ProtocolASTMTest {

	@Test
	public void testString() {
		ProtocolASTM astm = new ProtocolASTM();
		String t = "R|1|^^^174^1^^^COBAS_8000^^^^^^|< 0.01|ng/ml||N||F||&S&SYSTEM^System||20200527090724|COBAS_8000";
		List<ResultInfo> r = astm.parseResults(t);

		System.out.println(r);
	}

	@Test
	public void testParseMessage() {
		ProtocolASTM astm = new ProtocolASTM();
//		String msg = Codes.makeSendable("1H|\\^&|26973||^^^|||||||P||20200413110453<CR><ETX>E4<CR><LF>" +
//			"2P|1||PID_1||Surname^Name||19700101|M|||||||||||||||20200413095339||0||||||||00001<CR><ETX>B0<CR><LF>" +
//			"3O|1|99900001||ALL|R|20200413095339|||||X||||1||||||||||F<CR><ETX>3E<CR><LF>" +
//			"4R|1|^^^0030^^^^COBAS_CCEE^^^^^^|73.000|Вµmol/L||||F||&S&SYSTEM^||       20200515051512|COBAS_CCEE<CR><ETX>F3<CR><LF>" +
// 	     "R|1|^^^240^1^^^COBAS_CCEE^^^^^^|0.592 |uIU/ml ||N||F||&S&SYSTEM^System||202005200705258|COBAS_CCEE<CR>"+
//			"5L|1|N<CR><ETX>03<CR><LF>");

//		String msg = "1H|\\^&|10713||^^^|||||||P||20200520100128<CR>" +
//			"P|1||?||^||||||||||||||||||20200520081727|||||||||<CR>" +
//			"O|1|20004840|^1^1|^^^^^^^COBAS_P612|||||||X|||20200520100128|1|||||||||COBAS_P612|P<CR>" +
//			"M|1|EQU^RO^^1.0|COBAS_P612^^^cobas p612^LAB1^SAMPLEEVENT^SORT|20\u0017D6<CR><LF>2200520100128<CR>" +
//			"M|2|SAC^RO^^1.0|||20004840|||1|20200520100128|U||1|1||||{cobas p612} {P612.CEE} {0001 (1)}<CR>" +
//			"L|1|N<CR>" +
//			"<ETX>21<CR><LF>";
//
//		String msg = "1H|\\^&|11091||^^^|||||||P||20200520120557<CR>" +
//			"P|1||?||^||||||||||||||||||20200520104352||<CR>"+
//			"O|1|200420028642|200420028642|ALL|?|20200520104352|||||X||||1||||||||||F<CR>"+
//			"R|1|^^^240^1^^^COBAS_CCEE^^^^^^|0.592|uIU/ml||N||F||&S&SYSTEM^System||202005200705<ETB_>17<CR><LF>258|COBAS_CCEE<CR>"+
//			"L|1|N<CR><ETX>D2<CR><LF>";
		//                "1H|\\^&|11091||^^^|||||||P||20200520120557<CR>"
		String msg = "1H|\\^&|148231||^^^|||||||P||20200604162522<CR>" +
			"P|1||?||^||||||||||||||||||20200603131406||<CR>" +
			"O|1|1290178502|1290178502|ALL|?|20200603131406|||||X||||1||||||||||F<CR>" +
			"R|1|^^^218^1^^^COBAS_8000^^^^^^|375.000|uUI/mL||N||F||&S&SYSTEM^System||2020060416253<ETB_>D1<CR><LF>";
		PacketInfo p = astm.parseMessage(Codes.makeSendable(msg));
//
//		Configuration config;
//		config = new Configuration();
//		DBManager dbManager = new DBManager();
//		dbManager.init(config);

//		System.out.println(dbManager.genNewAliquotBarcode());
//		dbManager.saveResults(p, true);

		System.out.print(p.toString());
	}
}
