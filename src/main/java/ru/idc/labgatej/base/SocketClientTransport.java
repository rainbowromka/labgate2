package ru.idc.labgatej.base;

import lombok.extern.slf4j.Slf4j;
import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.FlowControl;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.StopBits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

@Slf4j
public class SocketClientTransport
implements Transport
{
	private int port;
	private String host;
	private InputStream in;
	private Writer out;
	private Socket socket = null;
	private String inetAddress = "";

	public SocketClientTransport(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void init(int timeout, int baudRate, DataBits dataBits, Parity parity, StopBits stopBits, FlowControl flowControl) {
	}

	@Override
	public void init(int timeout) {
		try {
			log.info("Подключаемся к \n\t" +
				"(IP address " + host +
				", порт " + port + ")");
			InetAddress ipAddress;
			ipAddress = InetAddress.getByName(host);
			socket = new Socket(ipAddress, port);
			socket.setSoTimeout(timeout);
			socket.setKeepAlive(true);
			log.info("Соединение установлено");

			inetAddress = "["
				+ socket.getInetAddress() + ":" + socket.getPort() + "] ";

			// Получаем входной и выходной потоки
			// сокета для обмена сообщениями с сервером
			InputStream sin = socket.getInputStream();
			OutputStream sout = socket.getOutputStream();

			in = sin; //new BufferedReader(new InputStreamReader(sin)); //new DataInputStream(sin);
			out =  new BufferedWriter(new OutputStreamWriter(sout)); //new DataOutputStream(sout);
		} catch (Exception e) {
			log.error("", e);
			log.debug("Ждём 60 секунд...");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException interruptedException) {
				log.error("", interruptedException);
			}
		}
	}

	@Override
	public void init(
		Socket socket,
		int timeout)
	{
		try {
			log.info("Сокет уже подключен.");
			this.socket = socket;
			socket.setSoTimeout(timeout);
			socket.setKeepAlive(true);
			log.info("Соединение установлено.");
			inetAddress = "["
				+ socket.getInetAddress() + ":" + socket.getPort() + "] ";

			InputStream sin = socket.getInputStream();
			OutputStream sout = socket.getOutputStream();

			in = sin;
			out = new BufferedWriter(new OutputStreamWriter(sout));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			log.error("", e);
		}
	}

	@Override
	public boolean isReady() {
		return socket != null && socket.isConnected();
	}

	@Override
	public void sendMessage(String msg) throws IOException {
		out.write(Codes.makeSendable(msg));
		out.flush();
		log.debug(inetAddress + msg);
	}

	@Override
	public void sendInt(int data) throws IOException {
		out.write(data);
		out.flush();
		log.debug(String.valueOf(data));
	}

	@Override
	public int readInt() throws IOException {
		return readInt(false);
	}

	@Override
	public int readInt(boolean ignoreTimeout) throws IOException {
		try {
			int result = in.read();
			log.debug(inetAddress + Codes.makePrintable(
				"" + (char) Integer.parseInt(Integer.toHexString(result))));
			return result;
		} catch (SocketTimeoutException e) {
			if (!ignoreTimeout) throw e;
		} catch (NumberFormatException e2) {
			log.error("NumberFormatException", e2);
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
		log.debug(inetAddress + msg);
		return msg;
	}

	@Override
	public byte[] readData(int len) throws IOException {
		byte[] data = new byte[len];
		int readed = in.read(data, 0, len);
		if (readed != len) {
			throw new IOException(String.format("не удалось прочитать все данные! Ожидали %d, прочитали %d", len, readed));
		}
		return data;
	}
}
