package org.sunspotworld.game;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.NoRouteException;
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

    public String addClient(String client) {
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

    public boolean hasClient() {
        return client1 != null || client2 != null;
    }

    public void setClient1(String client1) {
        this.client1 = client1;
    }

    public void setClient2(String client2) {
        this.client2 = client2;
    }

    public void run() {
        while (hasClient()) {
            try {
                RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://:61");
                Radiogram rdg = (Radiogram) conn.newDatagram(conn.getMaximumLength());

                try {
                    conn.receive(rdg);
                    String s = rdg.readUTF();
                    String accArr[] = s.split(",");

                    double x = Double.parseDouble(accArr[0]);
                    double y = Double.parseDouble(accArr[1]);
                    //double x = rdg.readDouble(), y = rdg.readDouble();

                    System.out.println("xtilt: " + x + ", ytilt: " + y);

                    move(x, y * (-1.0), rdg.getAddress());

                    gridPanel.repaint();
                    gridPanel.check();
                } catch (NoRouteException e) {
                } finally {
                    conn.close();
                }
            } catch (Exception e) {
                System.out.println("Exception in Game.java: ");
                e.printStackTrace();
            }
        }
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
