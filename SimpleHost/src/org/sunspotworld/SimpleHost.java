package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.radio.BroadcastConnectionState;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;
import javax.rmi.CORBA.Util;

/**
 *
 * @author Lukas Elmer
 */
public class SimpleHost {

    private String hostAddress;
    private Broadcaster broadcaster;
    private Thread broadcasterThread;
    private Thread clientListenerThread;
    private ClientListener clientListener;
    private List<String> clients = new ArrayList<String>();

    public SimpleHost() {
        hostAddress = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
        System.out.println("Host address is = " + hostAddress);

        broadcaster = new Broadcaster(this, 10);
        broadcasterThread = new Thread(broadcaster);
        broadcasterThread.start();

        clientListener = new ClientListener(this);
        clientListenerThread = new Thread(clientListener);
        clientListenerThread.start();
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public boolean addClient(String client) {
        if (containsClient(client)) {
            return false;
        }
        return clients.add(client);
    }

    public boolean containsClient(String client) {
        return clients.contains(client);
    }

    public boolean removeClient(String client) {
        return clients.remove(client);
    }
}
