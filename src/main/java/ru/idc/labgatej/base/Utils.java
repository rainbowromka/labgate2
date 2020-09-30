package ru.idc.labgatej.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Long.toHexString;

public class Utils {

	//<STX>1H|\^&|||ASTM-Host|||||CIT||P||20120219111500<CR>P|1<CR>O|1|923501||^^^CL-S\^^^CREA|||||||A<CR>L|1|F<CR><ETX>80<CR><LF>
	public static String calcCRC8(String data) {
		//String data2 = "3O|1|200251||ALL|?|20120131205000|||||X||||S1||||||||||F" + Codes.CR + Codes.ETX;
		byte[] buf2 = data.getBytes();
		long sum = 0L;
		for(byte b : buf2) {
			sum = sum + b;
		}
		Pattern r = Pattern.compile("^(.*?)(\\w{2})$");
		Matcher m = r.matcher(toHexString(sum).toUpperCase());
		if (m.find( )) {
			return m.group(2);
		}
		//String data = "<STX>1H|\\^&|||ASTM-Host|||||CIT||P||20120219111500<CR>P|1<CR>O|1|923501||^^^CL-S\\^^^CREA|||||||A<CR>L|1|F";
		return "";
	}

	public static Byte letteToNumnber(char letter)
	{
		return (byte)(letter - 'A' + 1);
	}

}
