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
/**
 *
 * @author Lukas Elmer
 */
public class ConnectionSender implements Runnable {

    private SimpleClient client;

    public ConnectionSender(SimpleClient client) {
        this.client = client;

    }

    public void run() {
        while (true) {
            if (client.connectedToHost()) {
                System.out.println("Sending connected message to " + client.getCurrentHost());
                NetworkUtils.sendMessageToAddress(client.getCurrentHost(), "connected", 42);
            }
            Utils.sleep(1000);
        }
    }
}
