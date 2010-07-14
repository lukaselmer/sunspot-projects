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
    private final int listeningBroadcastPort;
    private final int listeningPort;
    private final int answerPort;

    public HostListener(SimpleClient client, int listeningBroadcastPort, int answerPort, int listeningPort) {
        this.client = client;
        this.listeningBroadcastPort = listeningBroadcastPort;
        this.listeningPort = listeningPort;
        this.answerPort = answerPort;
    }

    public void run() {
        while (true) {
            if (!client.connectedToHost()) {
                String[] ss = NetworkUtils.receiveMessagesFromBroadcast(2, listeningBroadcastPort);
                if (ss != null) {
                    String answer = ss[0], hostAddress = ss[1];
                    if (answer != null && answer.equals("host")) {
                        String[] msgs = {"client_connect", hostAddress, client.getOwnAddress()};
                        NetworkUtils.sendMessagesToAddress(hostAddress, msgs, answerPort);
                        String[] server_response = NetworkUtils.receiveMessagesFromAddress(hostAddress, 2, listeningPort);
                        if (server_response != null) {
                            if (server_response[0].equals("connected")) {
                                client.connectToHost(hostAddress, server_response[1]);
                            }
                        }
                    }
                }
            } else {
                String[] server_response = NetworkUtils.receiveMessagesFromAddress(client.getCurrentHost(), 2, 44);
                if (server_response != null) {
                    if (server_response[0].equals("connected")) {
                        NetworkUtils.sendMessageToAddress(client.getCurrentHost(), "connected", 45);
                    } else if (server_response[0].equals("set_color")) {
                        if (server_response[1].equals("red")) {
                            LedsHelper.setColor(2, LEDColor.RED);
                        } else if (server_response[1].equals("green")) {
                            LedsHelper.setColor(2, LEDColor.GREEN);
                        }
                        LedsHelper.setOn(2);
                        NetworkUtils.sendMessageToAddress(client.getCurrentHost(), "ok", 45);
                    } else {
                        System.out.println("UNEXPECTED SERVER COMMAND:");
                        System.out.println(server_response[0]);
                        System.out.println(server_response[1]);
                    }
                }
            }
//            } else {
//                System.out.println("Checking connection...");
//                String[] ss = NetworkUtils.receiveMessagesFromAddress(client.getCurrentHost(), 1, 43);
//                if (ss != null && ss[0].equals("connected")) {
//                    System.out.println("Connection ok!");
//                } else {
//                    client.disconnectFromHost();
//                    System.out.println("Disconnected!");
//                }
//                Utils.sleep(300);
//            }
        }
    }
}
