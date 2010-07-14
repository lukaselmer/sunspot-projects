package org.sunspotworld.game;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.NoRouteException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import javax.microedition.io.Connector;
import javax.swing.JFrame;
import org.sunspotworld.NetworkUtils;

/**
 *
 * @author Lukas Elmer
 */
public class Game implements Runnable {

    private GridPanel gridPanel;
    private JFrame f;
    private String client1, client2;
    private Queue<GameMove> movements = new PriorityBlockingQueue<GameMove>();

    public Game(String client) {
        addClient(client);
        gridPanel = new GridPanel();
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        f.getContentPane().add(gridPanel);
        f.setSize(800, 800);
        f.setLocation(200, 200);
        f.setVisible(true);
    }

    public final String addClient(String client) {
        if (Configuration.random.nextBoolean()) {
            this.client1 = client;
            return getColor(client);
        } else {
            this.client2 = client;
            return getColor(client);
        }
    }

    public String getColor(String client) {
        if (client.equals(client1)) {
            return "red";
        } else if (client.equals(client2)) {
            return "green";
        } else {
            return null;
        }
    }

    public void removeClient(String client) {
        if (client.equals(client1)) {
            client1 = null;
        } else if (client.equals(client2)) {
            client2 = null;
        }
    }

    public boolean hasClient(String client) {
        return client.equals(client1) || client.equals(client2);
    }

    public boolean open() {
        return client1 == null || client2 == null;
    }

    public boolean hasClient() {
        return client1 != null || client2 != null;
    }

    public void setClient1(String client1) {
        this.client1 = client1;
    }

    public void setClient2(String client2) {
        this.client2 = client2;
    }

    public void addMovement(double x, double y, String client) {
        movements.add(new GameMove(x, y, client));
    }

    public void run() {
        while (hasClient()) {
            while (!movements.isEmpty()) {
                GameMove m = movements.poll();
                if (m != null) {
                    move(m.x, m.y * (-1.0), m.client);
                }
            }
            gridPanel.repaint();
            gridPanel.check();
        }
        f.dispose();
        f = null;
    }

    private void move(double x, double y, String address) {
        int passx = 0, passy = 0;
        double movementPerDraw = 0.2;
        if (x > movementPerDraw) {
            passx = 1;
            x -= movementPerDraw;
        }
        if (x < -movementPerDraw) {
            passx = -1;
            x += movementPerDraw;
        }
        if (y > movementPerDraw) {
            passy = 1;
            y -= movementPerDraw;
        }
        if (y < -movementPerDraw) {
            passy = -1;
            y += movementPerDraw;
        }

        if (address.equals(client1)) {
            gridPanel.playboard.move1(passx, passy);
        } else if (address.equals(client2)) {
            gridPanel.playboard.move2(passx, passy);
        } else {
            System.out.println("Other address: " + address);
            System.out.println("XXXXXXXXXXXXXXXXXXXXXX");
        }
        if (Math.abs(x) > movementPerDraw || Math.abs(y) > movementPerDraw) {
            move(x, y, address);
        }
    }
}
