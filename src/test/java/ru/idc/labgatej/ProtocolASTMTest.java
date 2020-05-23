package ru.idc.labgatej;

import org.junit.Test;
import ru.idc.labgatej.base.Codes;
import ru.idc.labgatej.base.Configuration;
import ru.idc.labgatej.base.DBManager;
import ru.idc.labgatej.base.ProtocolASTM;
import ru.idc.labgatej.model.PacketInfo;

public class ProtocolASTMTest {
	@Test
	public void testString() {
		String t = "0517" +
			"258|COBAS_CCEE" +
			"L|1|N";

		System.out.println(t.replace(""+ (char) 23, "F"));
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
		String msg = "1H|\\^&|22206||^^^|||||||P||20200523123312<CR>" +
			"P|1||?||^||||||||||||||||||20200520144850||<CR>" +
			"O|1|0930113342|0930113342|ALL|?|20200520144850|||||X||||1||||||||||F<CR>" +
			"R|1|^^^1403^1^^^COBAS_CEE^^^^^^|3.080|||N||F||&S&SYSTEM^System||20200522162339|COBAS_C<ETB_>1B<CR><LF>" +
			"2EE<CR>L|1|N<CR><ETX>9C<CR><LF>";
		PacketInfo p = astm.parseMessage(Codes.makeSendable(msg));
//
		Configuration config;
		config = new Configuration();
		DBManager dbManager = new DBManager();
		dbManager.init(config);

		System.out.println(dbManager.genNewAliquotBarcode());
//		dbManager.saveResults(p, true);

		System.out.print(p.toString());
	}
}



//
//
//		<STX>
//			1H|\^&|||ASTM-Host|||||CIT||P||20200516123813<CR>
//			P|1<CR>O|1|20014697||^^^545|||||||A<CR>
//		    O|2|20014697||^^^1034|||||||A<CR>
//		    O|3|20014697||^^^1025|||||||A<CR>
//		    O|4|20014697||^^^1026|||||||A<CR>
//		    O|5|20014697||^^^566|||||||A<CR>
//		    O|6|20014697||^^^1028|||||||A<CR>
//		    O|7|20014697|
//	    <ETB>DE<CR><LF>
//	    <STX>2|^^^534|||||||A<CR>
//		    O|8|20014697||^^^1029|||||||A<CR>
//		    O|9|20014697||^^^1030|||||||A<CR>
//		    O|10|20014697||^^^1031|||||||A<CR>
//		    O|11|20014697||^^^1032|||||||A<CR>
//		    O|12|20014697||^^^1162|||||||A<CR>
//		    O|13|20014697||^^^1163|||||||A<CR>
//		    O|14|20014697||^^^1164|||||||A<CR>
//		    O|15|2001
//		<ETB>0B<CR><LF>
//	    <STX>34697||^^^1165|||||||A<CR>
//		    O|16|20014697||^^^600|||||||A<CR>
//		    O|17|20014697||^^^1166|||||||A<CR>
//		    O|18|20014697||^^^1167|||||||A<CR>
//		    O|19|20014697||^^^1045|||||||A<CR>
//		    O|20|20014697||^^^1044|||||||A<CR>
//		    O|21|20014697||^^^1192|||||||A<CR>
//		    O|22|20014697||^^^1193|||||||A<CR>
//		    O|
//	    <ETB>3E<CR><LF>
//	    <STX>423|20014697||^^^565|||||||A<CR>
//		    O|24|20014697||^^^566|||||||A<CR>
//		    O|25|20014697||^^^567|||||||A<CR>
//		    O|26|20014697||^^^1040|||||||A<CR>
//		    O|27|20014697||^^^1036|||||||A<CR>
//		    O|28|20014697||^^^1170|||||||A<CR>
//		    O|29|20014697||^^^1037|||||||A<CR>
//		    O|30|20014697||^^^1169||||||
//		<ETB>02<CR><LF>
//	    <STX>5|A<CR>
//			O|31|20014697||^^^1038|||||||A<CR>
//		    O|32|20014697||^^^1041|||||||A<CR>
//		    O|33|20014697||^^^1039|||||||A<CR>
//		    O|34|20014697||^^^1171|||||||A<CR>
//		    O|35|20014697||^^^1194|||||||A<CR>
//		    O|36|20014697||^^^1195|||||||A<CR>
//		    O|37|20014697||^^^1196|||||||A<CR>
//		    O|38|20014697||^^^11
//		<ETB>D5<CR><LF>
//	    <STX>697|||||||A<CR>
//		    O|39|20014697||^^^1198|||||||A<CR>
//		    O|40|20014697||^^^1199|||||||A<CR>
//		    O|41|20014697||^^^1200|||||||A<CR>
//		    O|42|20014697||^^^1203|||||||A<CR>
//		    O|43|20014697||^^^1204|||||||A<CR>L|1|F<CR>
//	    <ETX>64<CR><LF>
