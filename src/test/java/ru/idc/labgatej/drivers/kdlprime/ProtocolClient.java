package ru.idc.labgatej.drivers.kdlprime;

import lombok.extern.slf4j.Slf4j;
import ru.idc.labgatej.base.Codes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * Эмулируем работу прибора, отправляющего данные протокола.
 */
@Slf4j
public class ProtocolClient
{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(
            String ip,
            int port)
            throws IOException
    {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendPacket(
            List<String> msg)
            throws Exception
    {
        sendMessage("<ENQ>");
        int res = readInt(false);

        if (res == -1) {
            throw new Exception("Неудачная попытка отправки");
        }

        for (String line : msg) {
            if (res != Codes.ACK) break;
            sendMessage(line);
        }
        sendMessage("<EOT>");

//        out.println(msg);
//        String resp = in.readLine();
//        return resp;
    }

    private int readInt(
        boolean ignoreTimeOut)
    throws IOException
    {
        try {
            int result = in.read();
            log.info(Codes.makePrintable("<<< "+ (char) Integer.parseInt(
                Integer.toHexString(result))));
            return result;
        }
        catch (SocketException e)
        {
            if (!ignoreTimeOut) throw e;
        }
        return -1;
    }

    private void sendMessage(
        String msg)
    {
        out.write(Codes.makeSendable(msg));
        out.flush();
        log.info(">>> " + msg);
    }

    public void stopConnection()
        throws IOException
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        in.close();
        out.close();
        clientSocket.close();
    }
}
