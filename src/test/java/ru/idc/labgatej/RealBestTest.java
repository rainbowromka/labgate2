package ru.idc.labgatej;

import com.fasterxml.uuid.Generators;
import org.junit.Test;
import org.junit.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class RealBestTest {

	@Test
	public void testString() {

		Date actual = new Date(new Date(120, 5, 4, 17, 37, 12).getTime() + 497);

		Date excepted = null;
		try {

			excepted = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_S")
				.parse("2020_6_4_17_37_12_497.txt");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Assert.assertEquals(actual, excepted);
	}
}
