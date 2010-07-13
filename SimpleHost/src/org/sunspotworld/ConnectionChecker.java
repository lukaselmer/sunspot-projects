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
                boolean connected = false;
                try {
                    RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://" + client + ":" + 12, Connector.READ_WRITE, true);
                    conn.setTimeout(550);
                    Datagram dg = conn.newDatagram(conn.getMaximumLength());
                    dg.writeUTF("connected");
                    conn.send(dg);
                    Utils.sleep(50);
                    conn.receive(dg);
                    connected = dg.readUTF().equals("connected");
                    conn.close();
                } catch (IOException ex) {
                    connected = false;
                    ex.printStackTrace();
                }
                if (!connected) {
                    System.out.println("Connection to " + client + " lost!");
                    host.removeClient(client);
                } else {
                    System.out.println("Connection to " + client + " ok!");
                }
            }
            Utils.sleep(300);
        }
    }
}
