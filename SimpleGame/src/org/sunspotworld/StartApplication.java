/*
 * StartApplication.java
 *
 * Created on 13.07.2010 13:56:26;
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
import com.sun.spot.peripheral.NoRouteException;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;
import com.sun.spot.sensorboard.peripheral.Switch;
import com.sun.spot.util.*;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class StartApplication extends MIDlet {

    private ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
    private ISwitch sw1 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW1];
    private ISwitch sw2 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW2];

    protected void startApp() throws MIDletStateChangeException {
        EDemoBoard demoboard = EDemoBoard.getInstance();
        IAccelerometer3D acc = demoboard.getAccelerometer();
        long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
        while (sw1.isOpen()) {
            try {
                System.out.println("Connecting and sending movements...");
                RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://0014.4F01.0000.5B1B:60");
                System.out.println("Done 1");
                Radiogram rdg = (Radiogram) conn.newDatagram(conn.getMaximumLength());
                System.out.println("Done 2");
                try {
                    System.out.println("Sending signal...");
                    rdg.writeUTF("" + demoboard.getAccelerometer().getTiltX() + "," + demoboard.getAccelerometer().getTiltY());
                    conn.send(rdg);
                    System.out.println("Done!");
                    Utils.sleep(100);
                } catch (NoRouteException e) {
                } finally {
                    System.out.println("Closing connection");
                    conn.close();
                }
            } catch (IOException e) {
                System.out.println("IO Exception");
            }
        }
        leds[0].setRGB(100, 0, 0);
        leds[0].setOn();
        Utils.sleep(1000);
        notifyDestroyed();
    }

    protected void pauseApp() {
        // This will never be called by the Squawk VM
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        // Only called if startApp throws any exception other than MIDletStateChangeException
    }
}
