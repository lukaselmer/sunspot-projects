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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;
import javax.rmi.CORBA.Util;
import org.sunspotworld.game.Game;

/**
 *
 * @author Lukas Elmer
 */
public class SimpleHost implements Runnable {

    private String hostAddress;
    private Broadcaster broadcaster;
    private Thread broadcasterThread;
    private Thread clientListenerThread;
    private ClientListener clientListener;
    private List<String> clients = new ArrayList<String>();
    private final Game game;
    private final Thread gameThread;
//    private Map<String, ConnectionSender> connectionSenders = new HashMap<String, ConnectionSender>();
//    private Map<String, Thread> connectionSenderThreads = new HashMap<String, Thread>();
//    private ConnectionChecker connectionChecker;
//    private Thread connectionCheckerThread;

    public SimpleHost() {
        hostAddress = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
        System.out.println("Host address is = " + hostAddress);

        broadcaster = new Broadcaster(this, 40);
        broadcasterThread = new Thread(broadcaster);
        broadcasterThread.start();

        clientListener = new ClientListener(this);
        clientListenerThread = new Thread(clientListener);
        clientListenerThread.start();

        game = new Game();
        gameThread = new Thread(game);
        gameThread.start();

//        connectionChecker = new ConnectionChecker(this);
//        connectionCheckerThread = new Thread(connectionChecker);
//        connectionCheckerThread.start();
    }

    public List<String> getClients() {
        return clients;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public boolean addClient(String client) {
        if (containsClient(client)) {
            return false;
        }
//        if (!connectionSenders.containsKey(client) && !connectionSenderThreads.containsKey(client)) {
//            ConnectionSender connectionSender = new ConnectionSender(client);
//            Thread connectionSenderThread = new Thread(connectionSender);
//            connectionSenderThread.start();
//            connectionSenders.put(client, connectionSender);
//            connectionSenderThreads.put(client, connectionSenderThread);
//        }
        return clients.add(client);
    }

    public boolean containsClient(String client) {
        return clients.contains(client);
    }

    public boolean removeClient(String client) {
//        connectionSenders.remove(client).setInactive();
//        connectionSenderThreads.remove(client);
        return clients.remove(client);
    }

    public void run() {
        while (true) {
            System.out.println("");
            System.out.println("---");
            System.out.println("" + clients.size() + " clients connected.");
            for (int i = 0; i < clients.size(); i++) {
                String client = clients.get(i);
                System.out.println("Client " + i + ": " + client);
            }
            System.out.println("---");
            System.out.println("");
            Utils.sleep(2000);
        }
    }
}
