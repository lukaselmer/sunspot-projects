package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;
import javax.rmi.CORBA.Util;

/**
 *
 * @author Lukas Elmer
 */
public class SimpleHost {

    private boolean connected = false;

    public SimpleHost() {
        long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));

        try {
            while (!connected) {
                DatagramConnection sendConn = (DatagramConnection) Connector.open("radiogram://broadcast:10");
                Datagram dgSend = sendConn.newDatagram(sendConn.getMaximumLength());
                dgSend.writeUTF("Host");
                dgSend.writeUTF(IEEEAddress.toDottedHex(ourAddr));
                sendConn.send(dgSend);
                System.out.println("Packet sent...");
                Utils.sleep(1000);
                sendConn.close();

                System.out.println("Receiving answer...");
                DatagramConnection recvConn = (DatagramConnection) Connector.open("radiogram://:11");
                Datagram dgReceive = recvConn.newDatagram(recvConn.getMaximumLength());
                recvConn.receive(dgReceive);
                System.out.println("Receiving packet...");
                String answer = dgReceive.readUTF();
                System.out.println("Answer: " + answer);
                Utils.sleep(1000);
                recvConn.close();
                if (answer != null && answer.equals("")) {
                    connected = true;
                    System.out.println("Connection established with: " + answer);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(SunSpotHostApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
