package ru.idc.labgatej.base;

import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;
import org.openmuc.jrxtx.StopBits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Rs232ClientTransport implements Transport {
	private static Logger logger = LoggerFactory.getLogger(Rs232ClientTransport.class);
	private String port;
	private InputStream in;
	private SerialPort serialPort;
	private Writer out;

	public Rs232ClientTransport(String port) {
		this.port = port;
	}

	@Override
	public void init(int timeout) {
	}

	@Override
	public void init(int timeout, int baudRate, DataBits dataBits, Parity parity, StopBits stopBits, FlowControl flowControl) {
		try {
			logger.info("Подключаемся к \n\t" +
				", порту " + port + ")");

			serialPort = SerialPortBuilder.newBuilder(port)
				.setBaudRate(baudRate)
				.setDataBits(dataBits)
				.setParity(parity)
				.setStopBits(stopBits)
				.setFlowControl(flowControl)
				.build();
			serialPort.setSerialPortTimeout(timeout);

			logger.info("Соединение установлено");

			// Получаем входной и выходной потоки
			in = serialPort.getInputStream();
			out =  new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void close() {
		try {
			if (serialPort != null) {
				serialPort.close();
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	@Override
	public boolean isReady() {
		return serialPort != null && !serialPort.isClosed();
	}

	@Override
	public void sendMessage(String msg) throws IOException {
		out.write(Codes.makeSendable(msg));
		out.flush();
		logger.debug(msg);
	}

	@Override
	public int readInt() throws IOException {
		return readInt(false);
	}

	@Override
	public int readInt(boolean ignoreTimeout) throws IOException {
		try {
			int result = in.read();
			logger.debug(Codes.makePrintable("" + (char) Integer.parseInt(Integer.toHexString(result))));
			return result;
		} catch (org.openmuc.jrxtx.SerialPortTimeoutException e) {
			if (!ignoreTimeout) throw e;
		}
		return -1;
	}

	@Override
	public String readMessage() throws IOException {
		StringBuilder sb = new StringBuilder();
		int m = -1;
		while (m != Codes.LF) {
			m = in.read();
			sb.append((char) m);
		}

		String msg = Codes.makePrintable(sb.toString());
		logger.debug(msg);
		return msg;
	}
}

