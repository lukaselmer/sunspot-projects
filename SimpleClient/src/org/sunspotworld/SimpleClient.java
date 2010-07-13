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
class SimpleClient implements Runnable {

    private String ownAddress;
    private ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
    private StartApplication midlet;
    ISwitch sw1 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW1];
    ISwitch sw2 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW2];
    private HostListener hostListener;
    private Thread hostListenerThread;
    private boolean connected = false;
    private String currentHost = null;

    public SimpleClient(StartApplication midlet) {
        System.out.println("Starting client");
        new BootloaderListener().start();   // monitor the USB (if connected) and recognize commands from host
        ownAddress = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
        System.out.println("Own address is = " + ownAddress);
        this.midlet = midlet;

        initLeds();

        hostListener = new HostListener(this);
        hostListenerThread = new Thread(hostListener);
        hostListenerThread.start();

        LedsHelper.blink();

        try {
            leds[0].setRGB(0, 0, 100);
            leds[0].setOn();
            Utils.sleep(1000);

            while (!connected && sw1.isOpen()) {
                System.out.println("Listening...");
                DatagramConnection recvConn = (DatagramConnection) Connector.open("radiogram://:10");
                Datagram dgReceive = recvConn.newDatagram(recvConn.getMaximumLength());
                recvConn.receive(dgReceive);

                System.out.println("Receiving packet...");
                String recvFromAddress = dgReceive.getAddress();
                String answer = dgReceive.readUTF();
                String ownAddress = dgReceive.readUTF();
                System.out.println("Answer: " + answer);
                System.out.println("Address: " + recvFromAddress);
                System.out.println("ownAddress: " + ownAddress);
                Utils.sleep(1000);
                recvConn.close();
                if (answer != null && answer.equals("Host") && ownAddress != null && ownAddress.length() > 0) {
                    DatagramConnection sendConn = (DatagramConnection) Connector.open("radiogram://" + ownAddress + ":11");
                    Datagram dgSend = sendConn.newDatagram(sendConn.getMaximumLength());
                    dgSend.writeUTF("Client");
                    sendConn.send(dgSend);
                    System.out.println("Connection established with: " + ownAddress);
                    sendConn.close();
                    connected = true;
                    Utils.sleep(1000);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stopApp() {
        for (int i = 0; i < 8; i++) {
            leds[i].setRGB(100, 0, 0);
            leds[i].setOn();
            Utils.sleep(150);
        }
        for (int j = 0; j < 15; j++) {
            for (int i = 0; i < 8; i++) {
                leds[i].setRGB(0, 100, 0);
                leds[i].setOn(!leds[i].isOn());
            }
            Utils.sleep(50);
        }
        for (int i = 0; i < 8; i++) {
            leds[i].setRGB(0, 0, 100);
            leds[i].setOn();
        }
        for (int i = 0; i < 8; i++) {
            leds[i].setOff();
            Utils.sleep(150);
        }
        midlet.notifyDestroyed();
    }

    public void run() {
    }

    private void initLeds() {
    }

    public void connectToHost(String host) {
        if (host != null && host.length() > 0) {
            currentHost = host;
            connected = true;
        } else {
            currentHost = null;
            connected = false;
        }
    }

    public void disconnectFromHost() {
        if (connected) {
            connectToHost(null);
        }
    }

    void exit() {
        if (hostListenerThread.isAlive()) {
            hostListenerThread.interrupt();
        }
        disconnectFromHost();
        LedsHelper.sneake(LEDColor.CYAN);
        LedsHelper.blink();
        LedsHelper.sneake();
        midlet.notifyDestroyed();
    }
}
