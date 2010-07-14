package org.sunspotworld;

import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;

/**
 *
 * @author Lukas Elmer
 */
class ShowSolutionListener implements Runnable {

    private ISwitch sw1 = EDemoBoard.getInstance().getSwitches()[EDemoBoard.SW1];
    private final StartApplication midlet;

    public ShowSolutionListener(StartApplication midlet) {
        this.midlet = midlet;
    }

    public void run() {
        while (true) {
            if (sw1.isClosed()) {
                midlet.pauseApp();
                LedsHelper.setTempColors(midlet.reference, 1000);
                midlet.resumeApp();
            }
        }
    }
}
