package org.sunspotworld;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author Lukas Elmer
 */
class ResultTransmitter {

    private static final int INACTIVE = 0;
    private static final int CONNECTING = 1;
    private static final int COMPLETED = 2;
    private static final int IOERROR = 3;
    private static final int PROTOCOLERROR = 4;
    private static int GETstatus = INACTIVE;
    private static int POSTstatus = INACTIVE;

    public ResultTransmitter() {
    }

    synchronized public void transmit(int swapTimes, int cycleTimes, int gameTimes) {
        try {
            HttpConnection conn = null;
            OutputStream out = null;
            InputStream in = null;
            long starttime = 0;
            String resp = null;

            System.out.println("Posting: <" + "blub" + ">");
            try {
                POSTstatus = CONNECTING;
                starttime = System.currentTimeMillis();
                //conn = (HttpConnection) Connector.open("http://127.0.0.1/puzzle_games/");
                conn = (HttpConnection) Connector.open("http://elmermx.ch/");
                conn.setRequestMethod(HttpConnection.POST);
                conn.setRequestProperty("Connection", "close");
                //conn.setRequestProperty("Authorization", "Basic " + "Noauth");

                out = conn.openOutputStream();
                out.write(("swapTimes=" + swapTimes + "\n").getBytes());
                out.write(("cycleTimes=" + cycleTimes + "\n").getBytes());
                out.flush();

                in = conn.openInputStream();
                resp = conn.getResponseMessage();
                if (resp.equals("OK")) {
                    POSTstatus = COMPLETED;
                } else {
                    POSTstatus = PROTOCOLERROR;
                }
            } catch (IOException ex) {
                POSTstatus = IOERROR;
                System.out.println("Error transmitting results!");
                ex.printStackTrace();
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }

            if (POSTstatus != COMPLETED) {
                System.out.println("Posting failed: " + resp);
            }
            System.out.println("Total time to post "
                    + "(including connection set up): "
                    + (System.currentTimeMillis() - starttime) + " ms");
            System.out.flush();
        } catch (IOException ex) {
        }
    }
}
