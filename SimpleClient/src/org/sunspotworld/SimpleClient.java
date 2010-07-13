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

        hostListener = new HostListener(this, 10, 11);
        hostListenerThread = new Thread(hostListener);
        hostListenerThread.start();

        LedsHelper.blink();
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
        leds[0].setRGB(0, 0, 100);
        leds[0].setOn();

        try {
            hostListenerThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void initLeds() {
    }

    public boolean connectedToHost() {
        return connected;
    }

    public void connectToHost(String host) {
        if (host != null && host.length() > 0) {
            currentHost = host;
            connected = true;
            System.out.println("Connection established with: " + host);
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
