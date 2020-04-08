package ru.idc.citm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;

public class SocketClientTransport implements Transport {
	private int port;
	private String host;
	private BufferedReader in;
	private Writer out;
	private Socket socket = null;

	public SocketClientTransport(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void init() {
		try {
			System.out.println("Connecting to the server\n\t" +
				"(IP address " + host +
				", port " + port + ")");
			InetAddress ipAddress;
			ipAddress = InetAddress.getByName(host);
			socket = new Socket(ipAddress, port);
			socket.setSoTimeout(10000);
			socket.setKeepAlive(true);
			System.out.println("The connection is established.");

			System.out.println(
				"\tLocalPort = " +
					socket.getLocalPort() +
					"\n\tInetAddress.HostAddress = " +
					socket.getInetAddress()
						.getHostAddress() +
					"\n\tReceiveBufferSize (SO_RCVBUF) = "
					+ socket.getReceiveBufferSize());

			// Получаем входной и выходной потоки
			// сокета для обмена сообщениями с сервером
			InputStream sin = socket.getInputStream();
			OutputStream sout = socket.getOutputStream();

			in = new BufferedReader(new InputStreamReader(sin)); //new DataInputStream(sin);
			out = new BufferedWriter(new OutputStreamWriter(sout)); //new DataOutputStream(sout);
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
			e.printStackTrace();
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
		Utils.logMessage("D -> " + msg);
	}

	@Override
	public int readInt() throws IOException {
		int result = in.read();
		Utils.logMessage("D <- " + Codes.makePrintable(""+(char) Integer.parseInt(Integer.toHexString(result))));

		return result;
	}

	@Override
	public String readMessage() throws IOException {
		String msg = Codes.makePrintable(in.readLine());
		Utils.logMessage("D <- " + msg);
		return msg;

//		StringBuilder result = new StringBuilder();
//		byte[] readByteArray= IOUtils.toByteArray(in, "UTF-8");
//		for (byte b : readByteArray) {
//			result.append(b);
//		}
//		return Codes.makePrintable(result.toString());
	}
}
