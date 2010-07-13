package org.sunspotworld;

/**
 *
 * @author Lukas Elmer
 */
public class HostListener implements Runnable {

    private SimpleClient client;

    public HostListener(SimpleClient client) {
        this.client = client;
    }

    public void run() {
    }
}
