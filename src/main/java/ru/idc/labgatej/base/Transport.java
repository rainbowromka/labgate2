package ru.idc.labgatej.base;

import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.StopBits;

import java.io.IOException;
import java.net.Socket;

public interface Transport {
	void init(int timeout);
	void init(int timeout, int baudRate, DataBits dataBits, Parity parity, StopBits stopBits, FlowControl flowControl);
	void init(Socket socket,int timeout);
	void close();
	void sendMessage(String msg) throws IOException;
	int readInt() throws IOException;
	int readInt(boolean ignoreTimeout) throws IOException;
	String readMessage() throws IOException;
	boolean isReady();
}
