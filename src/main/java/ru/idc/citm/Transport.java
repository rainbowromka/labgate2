package ru.idc.citm;

import java.io.IOException;

public interface Transport {
	void init();
	void close();
	void sendMessage(String msg) throws IOException;
	int readInt() throws IOException;
	int readInt(boolean ignoreTimeout) throws IOException;
	String readMessage() throws IOException;
	boolean isReady();
}
