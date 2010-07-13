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
            System.out.println("Hello, world");
            new BootloaderListener().start();   // monitor the USB (if connected) and recognize commands from host

            long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));

            ISwitch sw1 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW1];
            leds[0].setRGB(0, 0, 100);
            leds[0].setOn();
            Utils.sleep(1000);

            while (!connected && sw1.isOpen()) {
                System.out.println("Opening listener...");
                DatagramConnection recvConn = (DatagramConnection) Connector.open("radiogram://:10");
                Datagram dgReceive = recvConn.newDatagram(recvConn.getMaximumLength());
                recvConn.receive(dgReceive);
                System.out.println("Receiving packet...");
                String answer = dgReceive.readUTF();
                System.out.println("Answer: " + answer);
                Utils.sleep(1000);
                if (answer != null && answer.equals("")) {
                    connected = true;
                    DatagramConnection sendConn = (DatagramConnection) Connector.open("radiogram://broadcast:11");
                    Datagram dgSend = sendConn.newDatagram(sendConn.getMaximumLength());
                    dgSend.writeUTF("Client");
                    sendConn.send(dgSend);
                    System.out.println("Connection established with: " + answer);
                }
            }

            leds[0].setRGB(100, 0, 0);
            leds[0].setOn();
            Utils.sleep(1000);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        notifyDestroyed();
    }

    protected void pauseApp() {
        // This is not currently called by the Squawk VM
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
