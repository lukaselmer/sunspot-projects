/*
 * StartApplication.java
 *
 * Created on 13.07.2010 18:24:24;
 */
package org.sunspotworld;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.*;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class StartApplication extends MIDlet {

    private ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
    private boolean connected = false;

    protected void startApp() throws MIDletStateChangeException {
        try {
            System.out.println("Starting client");
            new BootloaderListener().start();   // monitor the USB (if connected) and recognize commands from host

            long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));

            ISwitch sw1 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW1];
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
                String hostAddress = dgReceive.readUTF();
                System.out.println("Answer: " + answer);
                System.out.println("Address: " + recvFromAddress);
                System.out.println("HostAddress: " + hostAddress);
                Utils.sleep(1000);
                recvConn.close();
                if (answer != null && answer.equals("Host") && hostAddress != null && hostAddress.length() > 0) {
                    DatagramConnection sendConn = (DatagramConnection) Connector.open("radiogram://" + hostAddress + ":11");
                    Datagram dgSend = sendConn.newDatagram(sendConn.getMaximumLength());
                    dgSend.writeUTF("Client");
                    sendConn.send(dgSend);
                    System.out.println("Connection established with: " + hostAddress);
                    sendConn.close();
                    connected = true;
                    Utils.sleep(1000);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        stopApp();
    }

    protected void pauseApp() {
        // This is not currently called by the Squawk VM
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
        notifyDestroyed();
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * I.e. if startApp throws any exception other than MIDletStateChangeException,
     * if the isolate running the MIDlet is killed with Isolate.exit(), or
     * if VM.stopVM() is called.
     * 
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true when this method is called, the MIDlet must
     *    cleanup and release all resources. If false the MIDlet may throw
     *    MIDletStateChangeException  to indicate it does not want to be destroyed
     *    at this time.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        for (int i = 0; i < 8; i++) {
            leds[i].setOff();
        }
    }
}
