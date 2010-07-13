/*
 * SunSpotHostApplication.java
 *
 * Created on 13.07.2010 18:14:20;
 */
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
 * Sample Sun SPOT host application
 */
public class SunSpotHostApplication {

    private boolean connected = false;

    /**
     * Print out our radio address.
     */
    public void run() {
        long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
        Logger logger = Logger.getLogger(SunSpotHostApplication.class.getName());

        try {
            while (!connected) {
                DatagramConnection sendConn = (DatagramConnection) Connector.open("radiogram://broadcast:10");
                Datagram dgSend = sendConn.newDatagram(sendConn.getMaximumLength());
                dgSend.writeUTF("Host");
                sendConn.send(dgSend);
                logger.log(Level.ALL, "Packet sent...");
                Utils.sleep(1000);

                DatagramConnection recvConn = (DatagramConnection) Connector.open("radiogram://:11");
                Datagram dgReceive = recvConn.newDatagram(recvConn.getMaximumLength());
                recvConn.receive(dgReceive);
                logger.log(Level.ALL, "Receiving packet...");
                String answer = dgReceive.readUTF();
                logger.log(Level.ALL, "Answer: " + answer);
                Utils.sleep(1000);
                if (answer != null && answer.equals("")) {
                    connected = true;
                    logger.log(Level.ALL, "Connection established with: " + answer);
                }
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }

    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) {
        SunSpotHostApplication app = new SunSpotHostApplication();
        app.run();
    }
}
