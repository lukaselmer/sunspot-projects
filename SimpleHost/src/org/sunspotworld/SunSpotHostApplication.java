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

    private SimpleHost host;

    /**
     * Print out our radio address.
     */
    public void run() {
        host = new SimpleHost();
        host.run();
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
