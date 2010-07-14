package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.TimeoutException;
import com.sun.spot.util.Utils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private String client;
    private boolean active = true;

    public ConnectionSender(String client) {
        this.client = client;
    }

    public void run() {
        while (active) {
            NetworkUtils.sendMessageToAddress(client, "connected", 43);
            Utils.sleep(1000);
        }
    }

    void setInactive() {
        active = false;
    }
}
