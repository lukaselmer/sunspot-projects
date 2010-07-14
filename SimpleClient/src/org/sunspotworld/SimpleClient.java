package org.sunspotworld;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.NoRouteException;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;
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
//    ISwitch sw2 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW2];
    private HostListener hostListener;
    private Thread hostListenerThread;
    private boolean connected = false;
    private String currentHost = null;
    private ExitListener exitListener;
    private Thread exitListenerThread;
    private IAccelerometer3D iAccelerometer3D = EDemoBoard.getInstance().getAccelerometer();

//    private ConnectionSender connectionSender;
//    private Thread connectionSenderThread;
    public SimpleClient(StartApplication midlet) {
        System.out.println("Starting client");
        new BootloaderListener().start();   // monitor the USB (if connected) and recognize commands from host
        ownAddress = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
        System.out.println("Own address is = " + ownAddress);
        this.midlet = midlet;

        initLeds();

        LedsHelper.blink();

//        hostListener = new HostListener(this, 40, 41);
//        hostListenerThread = new Thread(hostListener);
//        hostListenerThread.start();

        exitListener = new ExitListener(this);
        exitListenerThread = new Thread(exitListener);
        exitListenerThread.start();

//        connectionSender = new ConnectionSender(this);
//        connectionSenderThread = new Thread(connectionSender);
//        connectionSenderThread.start();

    }

    public void stopApp() {
        ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
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
        leds[0].setRGB(50, 50, 50);
        leds[0].setOn();
        try {
            while (sw1.isOpen()) {
                if (!connectedToHost()) {
                    connectToHost();
                } else {
                    System.out.println("Sending acc message...");
                    if (!sendAccMessage()) {
                        System.out.println("Error!");
                        disconnectFromHost();
                    } else {
                        System.out.println("Acc message ok!");
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("HEAVY EXCEPTION IN SIMPLE CLIENT THREAD:");
            ex.printStackTrace();
        }
        leds[0].setRGB(100, 0, 0);
        leds[0].setOn();
        exit();
    }

    public String getOwnAddress() {
        return ownAddress;
    }

    private void initLeds() {
    }

    public String getCurrentHost() {
        return currentHost;
    }

    public boolean connectedToHost() {
        return connected;
    }

    public void connectToHost(String host, String additionalInfo) {
        if (host != null && host.length() > 0) {
            currentHost = host;
            connected = true;
            System.out.println("Connection established with: " + host);
            System.out.println("Additional info: " + additionalInfo);
            leds[1].setColor(LEDColor.GREEN);
            leds[1].setOn();
        } else {
            System.out.println("Connection lost!");
            currentHost = null;
            connected = false;
            leds[1].setOff();
        }
    }

    public void disconnectFromHost() {
        if (connected) {
            connectToHost(null, null);
        }
    }

    void exit() {
        if (exitListenerThread != null && exitListenerThread.isAlive()) {
            exitListenerThread.interrupt();
        }
        disconnectFromHost();
        LedsHelper.sneake(LEDColor.CYAN);
        LedsHelper.blink();
        LedsHelper.sneake();
        midlet.notifyDestroyed();
    }

    private boolean connectToHost() {
        if (!connectedToHost()) {
            HostListener h = new HostListener(this, 40, 41, 42);
            h.run();
            System.out.println("CONNECTION TO HOST ESTABLISHED");
            return true;
        }
        return false;
    }

    private boolean sendAccMessage() {
        String message;
        try {
            message = "" + iAccelerometer3D.getTiltX() + "," + iAccelerometer3D.getTiltY();
        } catch (IOException ex) {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println("Accelerometer problem: ");
            ex.printStackTrace();
            return false;
        }
        return NetworkUtils.sendMessageToAddress(currentHost, message, 60);
    }
}
