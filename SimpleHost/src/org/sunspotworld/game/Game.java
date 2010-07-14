package org.sunspotworld.game;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.NoRouteException;
import javax.microedition.io.Connector;
import javax.swing.JFrame;

/**
 *
 * @author Lukas Elmer
 */
public class Game implements Runnable {

    private GridPanel gridPanel;
    private JFrame f;

    public Game() {
        gridPanel = new GridPanel();
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(gridPanel);
        f.setSize(800, 800);
        f.setLocation(200, 200);
        f.setVisible(true);
    }

    public void run() {
        while (true) {
            try {
                RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://broadcast:60");
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
                System.out.println("Exception: " + e.getStackTrace());
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

        if (true || address.equals("0014.4F01.0000.5B1B")) {
            gridPanel.playboard.move1(passx, passy);
        } else if (address.equals("0014.4F01.0000.5D94")) {
            gridPanel.playboard.move2(passx, passy);
        } else {
            System.out.println("Other address: " + address);
        }
        if (Math.abs(x) > movementPerDraw || Math.abs(y) > movementPerDraw) {
            move(x, y, address);
        }
    }
}
