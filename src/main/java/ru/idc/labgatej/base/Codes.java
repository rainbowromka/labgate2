package ru.idc.labgatej.base;

public abstract class Codes {
	public static final char STX = (char) 2;
	public static final char ETX = (char) 3;
	public static final char EOT = (char) 4;
	public static final char ENQ = (char) 5;
	public static final char ACK = (char) 6;
	public static final char LF = (char) 10;
	public static final char RF = (char) 11;
	public static final char CR = (char) 13;
	public static final char NAK = (char) 15;
	public static final char FRM = (char) 16;
	public static final char ETB = (char) 17;
	public static final char DONERECV = (char) 18;
	public static final char DONETRANS = (char) 19;
	public static final char SOM = (char) 20;
	public static final char ETB_ = (char) 23;

	public static String makePrintable(String msg) {
		return msg.replace("" + STX, "<STX>")
			.replace("" + ETB, "<ETB>")
			.replace("" + ETB_, "<ETB_>")
			.replace("" + ETX, "<ETX>")
			.replace("" + EOT, "<EOT>")
			.replace("" + ENQ, "<ENQ>")
			.replace("" + ACK, "<ACK>")
			.replace("" + LF, "<LF>")
			.replace("" + RF, "<RF>")
			.replace("" + CR, "<CR>")
			.replace("" + SOM, "<SOM>")
			.replace("" + FRM, "<FRM>")
			.replace("" + DONERECV, "<DONERECV>")
			.replace("" + DONETRANS, "<DONETRANS>")
			.replace("" + NAK, "<NAK>");
	}

	public static String makeSendable(String msg) {
		return msg.replace("<STX>", "" + STX)
			.replace("<ETB>", "" + ETB)
			.replace("<ETB_>", "" + ETB_)
			.replace("<ETX>", "" + ETX)
			.replace("<EOT>", "" + EOT)
			.replace("<ENQ>", "" + ENQ)
			.replace("<ACK>", "" + ACK)
			.replace("<LF>", "" + LF)
			.replace("<RF>", "" + RF)
			.replace("<CR>", "" + CR)
			.replace("<SOM>", "" + SOM)
			.replace("<FRM>", "" + FRM)
			.replace("<DONERECV>", "" + DONERECV)
			.replace("<DONETRANS>", "" + DONETRANS)
			.replace("<NAK>", "" + NAK);
	}
}
