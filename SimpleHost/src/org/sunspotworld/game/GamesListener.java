package org.sunspotworld.game;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.NoRouteException;
import com.sun.spot.util.Utils;
import javax.microedition.io.Connector;
import org.sunspotworld.SimpleHost;
import org.sunspotworld.game.Game;

/**
 *
 * @author Lukas Elmer
 */
public class GamesListener implements Runnable {

    private SimpleHost host;

    public GamesListener(SimpleHost host) {
        this.host = host;
    }

    public void run() {
        while (true) {
            if (host.getGames().size() > 0) {
                try {
                    RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://:61");
                    try {
                        Radiogram rdg = (Radiogram) conn.newDatagram(conn.getMaximumLength());
                        conn.receive(rdg);
                        String s = rdg.readUTF();
                        String accArr[] = s.split(",");

                        double x = Double.parseDouble(accArr[0]);
                        double y = Double.parseDouble(accArr[1]);
                        //double x = rdg.readDouble(), y = rdg.readDouble();

                        String client = rdg.getAddress();
                        Game g = host.getGameByClient(client);
                        if (g == null) {
                            System.out.println("CLIENT IS IN NO GAME!!! " + client);
                            System.out.println("CLIENT IS IN NO GAME!!! " + client);
                            System.out.println("CLIENT IS IN NO GAME!!! " + client);
                            System.out.println("xtilt: " + x + ", ytilt: " + y);
                            System.out.println("CLIENT IS IN NO GAME!!! " + client);
                            System.out.println("CLIENT IS IN NO GAME!!! " + client);
                            System.out.println("CLIENT IS IN NO GAME!!! " + client);
                        } else {
                            //System.out.println("Adding movement to game: " + x + ", " + y + ", " + client);
                            g.addMovement(x, y, client);
                        }
                    } catch (NoRouteException e) {
                    } finally {
                        conn.close();
                    }
                } catch (Exception e) {
                    System.out.println("Exception in Game.java: ");
                    e.printStackTrace();
                }
            } else {
                Utils.sleep(300);
            }
        }
    }
}
