package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.IAccelerometer3D;
import com.sun.spot.sensorboard.peripheral.ILightSensor;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ITemperatureInput;
import com.sun.spot.util.Debug;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import org.sunspotworld.helpers.URLBuilder;
import org.sunspotworld.helpers.HttpHelper;

/**
 *
 * @author Lukas Elmer
 */
public class SpotActivitySender implements Runnable {

    private String address = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
    ISwitch[] switches = EDemoBoard.getInstance().getSwitches();
    IAccelerometer3D acc = EDemoBoard.getInstance().getAccelerometer();
    ILightSensor light = EDemoBoard.getInstance().getLightSensor();
    ITemperatureInput temperature = EDemoBoard.getInstance().getADCTemperature();
    double lastRelativeAccel = 0;

    public void run() {
        while (true) {
            HttpHelper.addRequst(getUrlForSensors(), Math.abs(lastRelativeAccel) > 1.1);
            Utils.sleep(50);
        }
    }

    private String getUrlForSensors() {
        URLBuilder b = URLBuilder.build("spot_activity", "spot_activities.xml").add("address", address).
                add("time_in_milliseconds", System.currentTimeMillis()).add("sw1", switches[0].isOpen()).add("sw2", switches[1].isOpen());
        /*try {
        b.add("light", light.getAverageValue());
        } catch (IOException ex) {
        System.out.println("light");
        }
        try {
        b.add("tiltx", acc.getTiltX()).add("tilty", acc.getTiltY()).add("tiltz", acc.getTiltZ());
        } catch (IOException ex) {
        System.out.println("tilt");
        }*/
        try {
            lastRelativeAccel = acc.getRelativeAccel();
            b.add("rel_accel", lastRelativeAccel).add("rel_accelx", acc.getRelativeAccelX()).add("rel_accely", acc.getRelativeAccelY()).add("rel_accelz", acc.getRelativeAccelZ());
        } catch (IOException ex) {
            System.out.println("relaccel");
        }
        try {
            b.add("accel", acc.getAccel()).add("accelx", acc.getAccelX()).add("accely", acc.getAccelY()).add("accelz", acc.getAccelZ());
        } catch (IOException ex) {
            System.out.println("accel");
        }
        /*try {
        b.add("celsius", temperature.getCelsius());
        } catch (IOException ex) {
        System.out.println("celsius");
        }*/
        return b.toString();
    }
}
