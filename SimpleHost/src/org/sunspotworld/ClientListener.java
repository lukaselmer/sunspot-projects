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
            String[] ss = NetworkUtils.receiveMessagesFromBroadcast(3, 41);
            if (ss != null) {
                String type = ss[0], hostAddress = ss[1], clientAddress = ss[2];
                System.out.println("Client found: " + clientAddress);
                if (type != null && type.equals("client_connect") && hostAddress.equals(host.getHostAddress())) {
                    String[] welcomeMessage = {"connected", "welcome to the server"};
                    NetworkUtils.sendMessagesToAddress(clientAddress, welcomeMessage, 42);
                    if (host.containsClient(clientAddress)) {
                        host.removeClient(clientAddress);
                    }
                    if (host.addClient(clientAddress)) {
                        System.out.println("Connection established with: " + clientAddress);
                    }
                }
            } else {
                //System.out.println("No client found");
            }

            //Utils.sleep(1000);
        }
    }
}
