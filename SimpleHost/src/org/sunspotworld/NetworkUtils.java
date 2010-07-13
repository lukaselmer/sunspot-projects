package org.sunspotworld;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

/**
 *
 * @author Lukas Elmer
 */
public class NetworkUtils {

    private NetworkUtils() {
    }

    public static boolean sendMessageToAddress(String targetIEEEAddress, String message) {
        return sendMessageToAddress(targetIEEEAddress, message, 10);
    }

    public static boolean sendMessageToAddress(String targetIEEEAddress, String message, int port) {
        try {
            DatagramConnection conn = (DatagramConnection) Connector.open("radiogram://" + targetIEEEAddress + ":10");
            try {
                Datagram dg = conn.newDatagram(conn.getMaximumLength());
                dg.writeUTF(message);
                conn.send(dg);
            } catch (IOException ex) {
            }
            conn.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(NetworkUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
