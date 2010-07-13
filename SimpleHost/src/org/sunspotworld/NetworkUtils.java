package org.sunspotworld;

import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

/**
 *
 * @author Lukas Elmer
 */
public final class NetworkUtils {

    public static final int DEFAULT_PORT = 10;
    public static final String BROADCAST_ADDRESS = "broadcast";

    private NetworkUtils() {
    }

    public static boolean sendMessageToBroadcast(String message) {
        String[] messages = {message};
        return sendMessagesToAddress(BROADCAST_ADDRESS, messages, DEFAULT_PORT);
    }

    public static boolean sendMessageToBroadcast(String message, int port) {
        String[] messages = {message};
        return sendMessagesToAddress(BROADCAST_ADDRESS, messages, port);
    }

    public static boolean sendMessagesToBroadcast(String[] messages) {
        return sendMessagesToAddress(BROADCAST_ADDRESS, messages, DEFAULT_PORT);
    }

    public static boolean sendMessagesToBroadcast(String[] messages, int port) {
        return sendMessagesToAddress(BROADCAST_ADDRESS, messages, port);
    }

    public static boolean sendMessageToAddress(String targetIEEEAddress, String message) {
        String[] messages = {message};
        return sendMessagesToAddress(targetIEEEAddress, messages);
    }

    public static boolean sendMessageToAddress(String targetIEEEAddress, String message, int port) {
        String[] messages = {message};
        return sendMessagesToAddress(targetIEEEAddress, messages, port);
    }

    public static boolean sendMessagesToAddress(String targetIEEEAddress, String[] messages) {
        return sendMessagesToAddress(targetIEEEAddress, messages, DEFAULT_PORT);
    }

    public static boolean sendMessagesToAddress(String targetIEEEAddress, String[] messages, int port) {
        try {
            DatagramConnection conn = (DatagramConnection) Connector.open("radiogram://" + targetIEEEAddress + ":" + port);
            try {
                Datagram dg = conn.newDatagram(conn.getMaximumLength());
                for (int i = 0; i < messages.length; i++) {
                    dg.writeUTF(messages[i]);
                }
                conn.send(dg);
            } catch (IOException ex) {
            }
            conn.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static String[] receiveMessagesFromBroadcast(int lines, int port) {
        return receiveMessagesFromAddress("", lines, port);
    }

    public static String[] receiveMessagesFromAddress(String targetIEEEAddress, int lines, int port) {
        try {
            DatagramConnection recvConn = (DatagramConnection) Connector.open("radiogram://" + targetIEEEAddress + ":" + port);
            Datagram dgReceive = recvConn.newDatagram(recvConn.getMaximumLength());
            recvConn.receive(dgReceive);
            String[] answers = new String[lines];
            for (int i = 0; i < answers.length; i++) {
                answers[i] = dgReceive.readUTF();
            }
            recvConn.close();
            return answers;
        } catch (IOException ex) {
            return null;
        }
    }
}
