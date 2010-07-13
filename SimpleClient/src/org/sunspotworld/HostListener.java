package org.sunspotworld;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.*;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 *
 * @author Lukas Elmer
 */
public class HostListener implements Runnable {

    private SimpleClient client;
    private final int listeningPort;
    private final int answerPort;

    public HostListener(SimpleClient client, int listeningPort, int answerPort) {
        this.listeningPort = listeningPort;
        this.answerPort = answerPort;
    }

    public void run() {
        System.out.println("Listening for hosts...");
        while (true) {
            if (!client.connectedToHost()) {
                String[] ss = NetworkUtils.receiveMessagesFromBroadcast(2, listeningPort);
                String answer = ss[0], hostAddress = ss[1];
                Utils.sleep(200);
                if (answer != null && answer.equals("host") && hostAddress != null && hostAddress.length() > 0) {
                    NetworkUtils.sendMessageToAddress(hostAddress, "client", answerPort);
                    client.connectToHost(hostAddress);
                }
                Utils.sleep(1000);
            }
        }
    }
}
