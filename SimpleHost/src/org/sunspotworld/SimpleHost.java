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
import org.sunspotworld.game.GamesListener;

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
    private ArrayList<Game> games = new ArrayList<Game>();
    private ArrayList<Thread> gameThreads = new ArrayList<Thread>();
    private final GamesListener gameListener;
    private final Thread gameListenerThread;
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

        gameListener = new GamesListener(this);
        gameListenerThread = new Thread(gameListener);
        gameListenerThread.start();


//        game = new Game();
//        gameThread = new Thread(game);
//        gameThread.start();

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

        if (gamesContains(client)) {
            sendColorToClient(client, getGameByClient(client).getColor(client));
        } else {
            Game openGame = getOpenGame();
            if (openGame == null) {
                openGame = new Game(client);
                Thread gameThread = new Thread(openGame);
                gameThread.start();
                games.add(openGame);
                gameThreads.add(gameThread);
            } else {
                openGame.addClient(client);
            }
            sendColorToClient(client, openGame.getColor(client));
        }
        return clients.add(client);
    }

    public boolean containsClient(String client) {
        return clients.contains(client);
    }

    public boolean removeClient(String client) {
//        connectionSenders.remove(client).setInactive();
//        connectionSenderThreads.remove(client);
        boolean clientExisted = clients.remove(client);

        if (clientExisted) {
            Game g = getGameByClient(client);
            if (g != null) {
                g.removeClient(client);
                if (!g.hasClient()) {
                    int index = games.indexOf(g);
                    games.remove(g);
                    gameThreads.remove(index);
                }
            }
        }
        return clientExisted;
    }

    public void run() {
        while (true) {
            System.out.println("");
            System.out.println("---");
            System.out.println("" + clients.size() + " clients connected, " + games.size() + " games started.");
            for (int i = 0; i < clients.size(); i++) {
                String client = clients.get(i);
                System.out.println("Client " + i + ": " + client);
            }
            System.out.println("---");
            System.out.println("");
            Utils.sleep(5000);
        }
    }

    private void sendColorToClient(String client, String color) {
        String[] ss = {"set_color", color};
        while (!NetworkUtils.sendMessagesToAddress(client, ss, 44));
        String[] ss2 = {"set_game_port", "61"};
        while (!NetworkUtils.sendMessagesToAddress(client, ss2, 44));
    }

    public Game getGameByClient(String client) {
        for (Game game : games) {
            if (game.hasClient(client)) {
                return game;
            }
        }
        return null;
    }

    private boolean gamesContains(String client) {
        return getGameByClient(client) != null;
    }

    private Game getOpenGame() {
        for (Game game : games) {
            if (game.open()) {
                return game;
            }
        }
        return null;
    }

    public ArrayList<Game> getGames() {
        return games;
    }
}
