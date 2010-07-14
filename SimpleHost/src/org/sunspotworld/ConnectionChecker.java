package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.TimeoutException;
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

/**
 *
 * @author Lukas Elmer
 */
public class ConnectionChecker implements Runnable {

    private final SimpleHost host;

    public ConnectionChecker(SimpleHost host) {
        this.host = host;
    }

    public void run() {
        while (true) {
            for (String client : host.getClients()) {
                System.out.println("Testing connection to " + client + "...");
                String[] ss = NetworkUtils.receiveMessagesFromAddress(client, 1, 42);
                if (ss != null && ss.equals("connected")) {
                    System.out.println("Connection to " + client + " ok!");
                } else {
                    System.out.println("Connection to " + client + " lost!");
                    host.removeClient(client);
                    break;
                }
            }
        }
    }
}
