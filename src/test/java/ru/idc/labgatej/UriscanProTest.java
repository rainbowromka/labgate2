package ru.idc.labgatej;

import org.junit.Assert;
import org.junit.Test;
import ru.idc.labgatej.base.ProtocolUriskanPro;
import ru.idc.labgatej.model.ResultInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ru.idc.labgatej.base.Codes.makeSendable;

public class UriscanProTest {

	@Test
	public void testParseResult() {
		//старый прибор
		String s =
			"Operator : <CR><LF>" +
			"Date :2020-12-26 00:55:26<CR><LF>" +
			"ID_NO:0023-              <CR><LF>" +
			"Ward :                   <CR><LF>" +
			"Name :                   <CR><LF>" +
			"BLD    +     10   RBC/uL <CR><LF>" +
			"BIL    -    neg          <CR><LF>" +
			"URO    +-  norm          <CR><LF>" +
			"KET    -    neg          <CR><LF>" +
			"PRO    +     30   mg/dL  <CR><LF>" +
			"NIT    -    neg          <CR><LF>" +
			"GLU    -    neg          <CR><LF>" +
			"p.H         8.0          <CR><LF>" +
			"S.G       1.020          <CR><LF>" +
			"LEU    -    neg          <CR><LF>" +
			"VTC    -    neg          <CR><LF>" +
			"COL LT. Yellow           <CR><LF>";

		// новый прибор
//		String s =
//			"Operator : <CR><LF>" +
//			"Date :2020-12-26 01:45:27<CR><LF>" +
//			"ID_NO:0224-              <CR><LF>" +
//			"Ward :                   <CR><LF>" +
//			"Name :                   <CR><LF>" +
//			"BLD    +     10   RBC/uL <CR><LF>" +
//			"BIL    -    neg          <CR><LF>" +
//			"URO    +-  norm          <CR><LF>" +
//			"KET    -    neg          <CR><LF>" +
//			"PRO    -    neg          <CR><LF>" +
//			"NIT    -    neg          <CR><LF>" +
//			"GLU    -    neg          <CR><LF>" +
//			"p.H         6.0          <CR><LF>" +
//			"S.G       1.025          <CR><LF>" +
//			"LEU    -    neg          <CR><LF>" +
//			"VTC    -    neg          <CR><LF>" +
//			"COL LT. Yellow           <CR><LF>";
//


//			"Date :Oct-02-2020 16:16:37 <CR><LF>" +
//			"ID_NO:0083-0260055812      <CR><LF>" +
//			"Ward: <CR><LF>" +
//			"Name: <CR><LF>" +
//			"BLD    -    neg          <CR><LF>" +
//			"BIL    -    neg          <CR><LF>" +
//			"URO    +-  norm          <CR><LF>" +
//			"KET    -    neg          <CR><LF>" +
//			"PRO    -    neg          <CR><LF>" +
//			"NIT    -    neg          <CR><LF>" +
//			"GLU    -    neg          <CR><LF>" +
//			"p.H         5.0          <CR><LF>" +
//			"S.G     <=1.005          <CR><LF>" +
//			"LEU    -    neg          <CR><LF>" +
//			"VTC    -    neg          <CR><LF>" +
//			"COL !2.65;BK9            <CR><LF>" +
//			"CLA  <ETB_> '            <CR><LF>";

		ProtocolUriskanPro p = new ProtocolUriskanPro();
		List<ResultInfo> results = p.parseResults(makeSendable(s));


		Assert.assertEquals(results, results);
	}

}
