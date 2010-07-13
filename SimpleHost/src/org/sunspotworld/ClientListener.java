package org.sunspotworld;

import com.sun.spot.util.Utils;
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
class ClientListener implements Runnable {

    SimpleHost host;

    public ClientListener(SimpleHost host) {
        this.host = host;
    }

    public void run() {
        while (true) {
            String[] answers = NetworkUtils.receiveMessagesFromBroadcast(2, 11);
            String type = answers[0], clientAddress = answers[1];
            if (type != null && type.equals("client")) {
                NetworkUtils.sendMessageToAddress(clientAddress, "connected");
                if (host.addClient(clientAddress)) {
                    System.out.println("Connection established with: " + clientAddress);
                }
            }
            Utils.sleep(1000);
        }
    }
}
