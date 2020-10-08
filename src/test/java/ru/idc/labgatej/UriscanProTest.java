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
		String s =
			"Date :Oct-02-2020 16:16:37 <CR><LF>" +
			"ID_NO:0083-0260055812      <CR><LF>" +
			"Ward: <CR><LF>" +
			"Name: <CR><LF>" +
			"BLD    -    neg          <CR><LF>" +
			"BIL    -    neg          <CR><LF>" +
			"URO    +-  norm          <CR><LF>" +
			"KET    -    neg          <CR><LF>" +
			"PRO    -    neg          <CR><LF>" +
			"NIT    -    neg          <CR><LF>" +
			"GLU    -    neg          <CR><LF>" +
			"p.H         5.0          <CR><LF>" +
			"S.G     <=1.005          <CR><LF>" +
			"LEU    -    neg          <CR><LF>" +
			"VTC    -    neg          <CR><LF>" +
			"COL !2.65;BK9            <CR><LF>" +
			"CLA  <ETB_> '            <CR><LF>";

		ProtocolUriskanPro p = new ProtocolUriskanPro();
		List<ResultInfo> results = p.parseResults(makeSendable(s));


		Assert.assertEquals(results, results);
	}

}
