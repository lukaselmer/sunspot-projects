package org.sunspotworld;

import com.sun.spot.flashmanagement.FlashFile;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.sensorboard.io.IScalarInput;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;
import com.sun.spot.sensorboard.peripheral.LEDColor;
import com.sun.spot.util.*;


import java.io.IOException;
import java.util.Random;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * A motion controlled Puzzle for the SunSPOT (Green SDK)
 * The Goal is to (re-)create a rainbow of LEDs starting with the red led on the left.
 * There are four moves : shift left and shift right by tilting the spot to the left or right.
 * The two other moves are tilt forward and backward and swap the 4 middle leds in a certain way.
 * ----------------------------------------------------------------------------------------------
 * (C) 2007 E.Hooijmeijer / www.ctrl-alt-dev.nl
 * This software is published under the GNU Public Licence v2 or better (www.gnu.org)
 * For the development blog visit Eriks SunSPOT Adventures at http://joce.nljug.org
 * @author E.Hooijmeijer
 */
public class StartApplication extends MIDlet {

    private SpotActivitySender spotActivitySender;
    private Thread spotActivitySenderThread;

    /**
     * MIDlet call to start the application.
     */
    protected void startApp() throws MIDletStateChangeException {
        new BootloaderListener().start();   // monitor the USB (if connected) and recognize commands from host
        spotActivitySender = new SpotActivitySender();
        spotActivitySenderThread = new Thread(spotActivitySender);
        spotActivitySenderThread.start();
    }

    public void exit() {
        notifyDestroyed();
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
    }
}
