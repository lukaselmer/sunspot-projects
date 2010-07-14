/*
 * SunSpotHostApplication.java
 *
 * Created on 13.07.2010 14:03:18;
 */
package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.NoRouteException;
import com.sun.spot.util.IEEEAddress;

import java.io.*;
import javax.microedition.io.*;
import javax.swing.JFrame;

public class SunSpotHostApplication {

    public void runGame() {
        System.out.println("Starting app...");
        GridPanel gridPanel = new GridPanel();
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(gridPanel);
        f.setSize(800, 800);
        f.setLocation(200, 200);
        f.setVisible(true);
        System.out.println("Done!");

        while (true) {
            try {
                System.out.println("Open host...");
                RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://:60");
                Radiogram rdg = (Radiogram) conn.newDatagram(conn.getMaximumLength());
                System.out.println("Done!");

                try {
                    System.out.println("Receiving connection");
                    conn.receive(rdg);
                    System.out.println(rdg.getAddress());
                    String s = rdg.readUTF();
                    System.out.println(s);
                    String accArr[] = s.split(",");

                    double x = Double.parseDouble(accArr[0]);
                    double y = Double.parseDouble(accArr[1]);

                    System.out.println("xtilt: " + x + ", ytilt: " + y);

                    int passx = 0, passy = 0;
                    if (x > 0.5) {
                        passx = 1;
                    }
                    if (x < -0.5) {
                        passx = -1;
                    }
                    if (y > 0.5) {
                        passy = 1;
                    }
                    if (y < -0.5) {
                        passy = -1;
                    }

                    if (rdg.getAddress().equals("0014.4F01.0000.5B1B")) {
                        gridPanel.playboard.move1(passx, passy);
                    } else if (rdg.getAddress().equals("0014.4F01.0000.5D94")) {
                        gridPanel.playboard.move2(passx, passy);
                    } else {
                        System.out.println("Other address: " + rdg.getAddress());
                    }

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

    public static void main(String[] args) {
        SunSpotHostApplication app = new SunSpotHostApplication();
        app.runGame();
    }
}
