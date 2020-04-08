package ru.idc.citm;

public abstract class Codes {
	public static final char STX = (char) 2;
	public static final char ETX = (char) 3;
	public static final char EOT = (char) 4;
	public static final char ENQ = (char) 5;
	public static final char ACK = (char) 6;
	public static final char LF = (char) 10;
	public static final char CR = (char) 13;
	public static final char NAK = (char) 15;

	public static String makePrintable(String msg) {
		return msg.replace("" + STX, "<STX>")
			.replace("" + ETX, "<ETX>")
			.replace("" + EOT, "<EOT>")
			.replace("" + ENQ, "<ENQ>")
			.replace("" + ACK, "<ACK>")
			.replace("" + LF, "<LF>")
			.replace("" + CR, "<CR>")
			.replace("" + NAK, "<NAK>");
	}

	public static String makeSendable(String msg) {
		return msg.replace("<STX>", "" + STX)
			.replace("<ETX>", "" + ETX)
			.replace("<EOT>", "" + EOT)
			.replace("<ENQ>", "" + ENQ)
			.replace("<ACK>", "" + ACK)
			.replace("<LF>", "" + LF)
			.replace("<CR>", "" + CR)
			.replace("<NAK>", "" + NAK);
	}
}
