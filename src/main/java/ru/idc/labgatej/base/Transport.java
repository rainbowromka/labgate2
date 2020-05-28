package ru.idc.labgatej.base;

import java.io.IOException;

public interface Transport {
	void init(int timeout);
	void close();
	void sendMessage(String msg) throws IOException;
	int readInt() throws IOException;
	int readInt(boolean ignoreTimeout) throws IOException;
	String readMessage() throws IOException;
	boolean isReady();
}
