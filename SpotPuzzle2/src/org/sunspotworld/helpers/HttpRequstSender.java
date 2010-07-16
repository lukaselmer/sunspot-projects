package org.sunspotworld.helpers;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Queue;
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author Lukas Elmer
 */
public class HttpRequstSender implements Runnable {

    private static final int INACTIVE = 0;
    private static final int CONNECTING = 1;
    private static final int COMPLETED = 2;
    private static final int IOERROR = 3;
    private static final int PROTOCOLERROR = 4;
    private static int POSTstatus = INACTIVE;
    private SimpleQueue requests = new SimpleQueue();
    private boolean enabled = true;
    private static String host = null;//"0014.4F01.0000.5B1B";
    private static HostFinder hostFinder = null;//"0014.4F01.0000.5B1B";
    private static Thread hostFinderThread = null;//"0014.4F01.0000.5B1B";

    public void run() {
        while (enabled) {
            while (!requests.isEmpty()) {
                String url = (String) requests.dequeue();
                doHttpRequst(url);
            }
            Utils.sleep(100);
        }
    }

    public void stop() {
        enabled = false;
    }

    void addRequest(String url) {
        requests.enqueue(url);
    }

    private void doHttpRequst(String url) {
        if (hostFinder == null) {
            hostFinder = new HostFinder();
            hostFinderThread = new Thread(hostFinder);
            hostFinderThread.start();
        }
        System.out.println("Transmitting URL '" + url + "'...");
        while (!transmit(url)) {
            System.out.println("Transmission of '" + url + "' failed! Trying again...");
        }
        System.out.println("Transmission of '" + url + "' successful!");
    }

    synchronized boolean transmit(String url) {
        boolean success = false;
        if (host == null) {
            Utils.sleep(10000);
            return false;
        }
        try {
            RadiogramConnection conn = (RadiogramConnection) Connector.open("radiogram://" + host + ":40" /*, Connector.READ_WRITE, true*/);
            Datagram dg = conn.newDatagram(conn.getMaximumLength());
            try {
                dg.writeUTF(url);
                conn.send(dg);
                conn.receive(dg);
                String answer = dg.readUTF();
                success = answer.equals("ok");
            } catch (Exception ex) {
                //ex.printStackTrace();
            } finally {
                conn.close();
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return success;
    }

    synchronized boolean transmit2(String url) {
        boolean worked = false;
        try {
            HttpConnection conn = null;
            //long starttime = 0;
            String resp = null;

            try {
                POSTstatus = CONNECTING;
                //starttime = System.currentTimeMillis();
                conn = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
                conn.setRequestMethod(HttpConnection.POST);
                resp = conn.getResponseMessage();
                //System.out.println("resp = " + resp);
                if (resp != null && resp.toLowerCase().equals("ok") || resp.toLowerCase().equals("found")) {
                    POSTstatus = COMPLETED;
                } else {
                    POSTstatus = PROTOCOLERROR;
                }
            } catch (Exception ex) {
                POSTstatus = IOERROR;
                //System.out.println("Error transmitting results!");
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
            if (POSTstatus == COMPLETED) {
                worked = true;
            }
            // else {
            //    System.out.println("Posting failed. Try again later...");
            //}
            //System.out.println("Total time to post " + (System.currentTimeMillis() - starttime) + " ms");
            //System.out.flush();
        } catch (IOException ex) {
        }
        return worked;
    }
}