package ru.idc.labgatej.base;

import lombok.extern.slf4j.Slf4j;
import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;
import org.openmuc.jrxtx.StopBits;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

@Slf4j
public class Rs232ClientTransport implements Transport
{
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
	public void init(Socket socket, int timeout) {
	}

	@Override
	public void init(int timeout, int baudRate, DataBits dataBits, Parity parity, StopBits stopBits, FlowControl flowControl) {
		try {
			log.info("Подключаемся к \n\t" +
				", порту " + port + ")");

			serialPort = SerialPortBuilder.newBuilder(port)
				.setBaudRate(baudRate)
				.setDataBits(dataBits)
				.setParity(parity)
				.setStopBits(stopBits)
				.setFlowControl(flowControl)
				.build();
			serialPort.setSerialPortTimeout(timeout);

			log.info("Соединение установлено");

			// Получаем входной и выходной потоки
			in = serialPort.getInputStream();
			out =  new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@Override
	public void close() {
		try {
			if (serialPort != null) {
				serialPort.close();
			}
		} catch (IOException e) {
			log.error("", e);
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
		log.debug(msg);
	}

	@Override
	public int readInt() throws IOException {
		return readInt(false);
	}

	@Override
	public int readInt(boolean ignoreTimeout) throws IOException {
		try {
			int result = in.read();
			log.debug(Codes.makePrintable("" + (char) Integer.parseInt(Integer.toHexString(result))));
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
		log.debug(msg);
		return msg;
	}
}

