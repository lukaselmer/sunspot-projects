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
class Broadcaster implements Runnable {

    private SimpleHost host;
    private final int port;

    Broadcaster(SimpleHost host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        while (true) {
            try {
                DatagramConnection sendConn = (DatagramConnection) Connector.open("radiogram://broadcast:" + port);
                Datagram dgSend = sendConn.newDatagram(sendConn.getMaximumLength());
                dgSend.writeUTF("Host");
                dgSend.writeUTF(host.getHostAddress());
                sendConn.send(dgSend);
                sendConn.close();
                System.out.println("Broadcast packet sent...");
                Utils.sleep(1000);
            } catch (IOException ex) {
                Logger.getLogger(Broadcaster.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
