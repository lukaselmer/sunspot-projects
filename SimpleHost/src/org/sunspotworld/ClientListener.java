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
            try {
                System.out.println("Receiving answer...");
                DatagramConnection recvConn = (DatagramConnection) Connector.open("radiogram://:11");
                Datagram dgReceive = recvConn.newDatagram(recvConn.getMaximumLength());
                recvConn.receive(dgReceive);
                System.out.println("Receiving packet...");
                String answer = dgReceive.readUTF();
                String clientAddress = dgReceive.readUTF();
                System.out.println("Answer: " + answer);
                System.out.println("ClientAddress: " + clientAddress);
                Utils.sleep(1000);
                recvConn.close();
                if (answer != null && answer.equals("Client")) {
                    System.out.println("Connection established with: " + clientAddress);
                    NetworkUtils.sendMessageToAddress();
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
