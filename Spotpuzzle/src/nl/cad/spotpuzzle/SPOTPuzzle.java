package nl.cad.spotpuzzle;

import com.sun.spot.peripheral.Spot;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.ISwitch;
import com.sun.spot.sensorboard.peripheral.ITriColorLED;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.*;


import java.io.IOException;
import java.util.Random;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * A motion controlled Puzzle for the SunSPOT (Green SDK)
 * The Goal is to (re-)create a rainbow of LEDs starting with the red led on the left.
 * There are four moves : shift left and shift right by tilting the spot to the left or right.
 * The two other moves are tilt forward and backward and swap the 4 middle leds in a certain way.  
 * ----------------------------------------------------------------------------------------------
 * (C) 2007 E.Hooijmeijer / www.ctrl-alt-dev.nl
 * This software is published under the GNU Public Licence v2 or better (www.gnu.org)
 * For the development blog visit Eriks SunSPOT Adventures at http://joce.nljug.org
 * @author E.Hooijmeijer
 */
public class SPOTPuzzle extends MIDlet {

    /** index of the X axis */
    private static final int X = 0;
    /** index of the Y axis */
    private static final int Y = 1;
    /** index of the Z axis */
    private static final int Z = 2;
    /** index of the red component of the led */
    private static final int RED = 0;
    /** index of the green component of the led */
    private static final int GREEN = 1;
    /** index of the blue component of the led */
    private static final int BLUE = 2;
    /** minimal value for the accelerometer */
    private static final double LIMIT = 0.2;
    /** indicates no player action */
    private static final int ACTION_NONE = 0;
    /** indicates the player wants to shift the leds to the left */
    private static final int ACTION_SHIFTLEFT = 1;
    /** indicates the player wants to shift the leds to the right */
    private static final int ACTION_SHIFTRIGHT = 2;
    /** indicates the player has tilted the spot up */
    private static final int ACTION_UP = 5;
    /** indicates the player has tilted the spot down */
    private static final int ACTION_DOWN = 6;
    //
    // Hardware
    //
    private IAccelerometer3D accel = EDemoBoard.getInstance().getAccelerometer();
    private IScalarInput[] axis = new IScalarInput[]{accel.getXAxis(), accel.getYAxis(), accel.getZAxis()};
    private ITriColorLED[] leds = EDemoBoard.getInstance().getLEDs();
    /** holds the current state of the puzzle */
    private int[][] puzzle = new int[leds.length][3];
    /** holds the solved state of the puzzle */
    private int[][] reference = new int[leds.length][3];
    private double[] zeroOffset = {465.5, 465.5, 465.5};     // default zero offset for raw accelerator value
    private double[] sensitivity = {186.2, 186.2, 186.2};    // default conversion factor from raw accelerator value to G's
    private double[] movement;

    private double[] getMovement(double[] d) {
        if (d == null) {
            d = new double[3];
        }
        for (int t = 0; t < 3; t++) {
            try {
                d[t] = (axis[t].getValue() - zeroOffset[t]) / sensitivity[t];
            } catch (IOException ex) {
                d[t] = 0;
            }
        }
        return d;
    }

    /**
     * Calibrates the game by waiting until the spot does not
     * move anymore (assuming its on a horizontal position).
     */
    private void determineZeroPoint() {
        double delta;
        double[] d = new double[3];
        double[] last = new double[3];
        boolean stable = false;
        // Indicate calibrating with a red led.
        leds[0].setRGB(255, 0, 0);
        leds[0].setOn();
        do {
            try {
                for (int t = 0; t < 3; t++) {
                    d[t] = axis[t].getValue();
                }
                delta = 0;
                for (int t = 0; t < 3; t++) {
                    delta = delta + Math.abs(d[t] - last[t]);
                }
                stable = delta < 0.01;
                for (int t = 0; t < 3; t++) {
                    last[t] = d[t];
                }
                Utils.sleep(100);
            } catch (IOException ex) {
                stable = false;
            }
        } while (!stable);
        //
        leds[0].setOff();
        // Store the new offset
        zeroOffset = d;
    }

    /**
     * sets up the puzzle by storing the rainbow color values in the
     * given array.
     */
    private void setupPuzzle(int[][] p) {
        p[0] = new int[]{255, 0, 0};
        p[1] = new int[]{128, 64, 0};
        p[2] = new int[]{64, 128, 0};
        p[3] = new int[]{0, 255, 0};
        p[4] = new int[]{0, 128, 64};
        p[5] = new int[]{0, 64, 128};
        p[6] = new int[]{0, 0, 255};
        p[7] = new int[]{128, 0, 128};
        updateLeds();
    }

    /**
     * Shuffles the puzzle by performing a series of random moves.
     * These are the same moves the player can make, so the puzzle
     * is solvable.
     */
    private void randomize() {
        Random r = new Random(System.currentTimeMillis());
        for (int t = 0; t < 16; t++) {
            switch (Math.abs(r.nextInt()) % 4) {
                case 0:
                    doShiftLeft();
                    break;
                case 1:
                    doUp();
                    break;
                case 2:
                    doDown();
                    break;
                case 3:
                    doShiftRight();
                    break;
            }
            updateLeds();
        }
    }

    /**
     * Translates the position of the Spot into a move for the player.
     */
    private int getPlayerAction() {
        movement = getMovement(movement);
        //
        boolean xHalt = Math.abs(movement[X]) < LIMIT;
        boolean yHalt = Math.abs(movement[Y]) < LIMIT;
        boolean zHalt = Math.abs(movement[Z]) < LIMIT;
        //
        boolean left = movement[X] < -LIMIT && zHalt && yHalt;
        boolean right = movement[X] > LIMIT && zHalt && yHalt;
        boolean up = movement[Y] < -LIMIT && zHalt && xHalt;
        boolean down = movement[Y] > LIMIT && zHalt && xHalt;
        //
        if (left) {
            return ACTION_SHIFTLEFT;
        }
        if (right) {
            return ACTION_SHIFTRIGHT;
        }
        if (up) {
            return ACTION_UP;
        }
        if (down) {
            return ACTION_DOWN;
        }
        //
        return ACTION_NONE;
    }

    /**
     * Main game loop, get the player action, perform it, update the screen, 
     * check if the player has solved the puzzle. Repeat until infinity. 
     */
    private void playGame() {
        int lastAction = ACTION_NONE;
        while (true) {
            int action = getPlayerAction();
            switch (action) {
                case ACTION_NONE:
                    break;
                case ACTION_SHIFTLEFT:
                    doShiftLeft();
                    break;
                case ACTION_SHIFTRIGHT:
                    doShiftRight();
                    break;
            }
            if (action != lastAction) {
                switch (action) {
                    case ACTION_UP:
                        doUp();
                        break;
                    case ACTION_DOWN:
                        doDown();
                        break;
                }
            }
            updateLeds();
            if (isSolved()) {
                blink(5);
                randomize();
            }
            Utils.sleep(250);
            lastAction = action;
        }
    }

    /**
     * Compares the current state of the puzzle with the refence state.
     * @return true if the puzzle is solved. 
     */
    private boolean isSolved() {
        for (int t = 0; t < puzzle.length; t++) {
            for (int y = 0; y < 3; y++) {
                if (puzzle[t][y] != reference[t][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * performs the up move, swaps the middle for leds.
     */
    public void doUp() {
        swap(2, 4);
        swap(3, 5);
    }

    /**
     * performs the down move, swaps the middle for leds in a slightly
     * different way.
     */
    public void doDown() {
        swap(3, 4);
        swap(2, 5);
    }

    /**
     * swaps two puzzle positions.
     */
    protected void swap(int s, int d) {
        int[] tmp = puzzle[s];
        puzzle[s] = puzzle[d];
        puzzle[d] = tmp;
    }

    /**
     * shifts the leds to the left 
     */
    private void doShiftLeft() {
        int[] tmp = puzzle[0];
        for (int t = 0; t < puzzle.length - 1; t++) {
            puzzle[t] = puzzle[t + 1];
        }
        puzzle[puzzle.length - 1] = tmp;
    }

    /**
     * shifts the leds to the right 
     */
    private void doShiftRight() {
        int[] tmp = puzzle[puzzle.length - 1];
        for (int t = puzzle.length - 1; t > 0; t--) {
            puzzle[t] = puzzle[t - 1];
        }
        puzzle[0] = tmp;
    }

    /**
     * updates the leds and waits a short while. 
     */
    private void updateLeds() {
        for (int t = 0; t < puzzle.length; t++) {
            leds[t].setOn();
            leds[t].setRGB(puzzle[t][0], puzzle[t][1], puzzle[t][2]);
        }
        Utils.sleep(50);
    }

    /**
     * blinks the leds for the specified number of times.
     */
    private void blink(int nr) {
        for (int t = 0; t < nr; t++) {
            for (int y = 0; y < leds.length; y++) {
                leds[y].setOn();
            }
            Utils.sleep(600);
            for (int y = 0; y < leds.length; y++) {
                leds[y].setOff();
            }
            Utils.sleep(400);
        }
    }

    /**
     * MIDlet call to start the application.
     */
    protected void startApp() throws MIDletStateChangeException {
        determineZeroPoint();
        setupPuzzle(puzzle);
        setupPuzzle(reference);
        blink(5);
        randomize();
        playGame();
    }

    protected void pauseApp() {
        // This will never be called by the Squawk VM
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        // Only called if startApp throws any exception other than MIDletStateChangeException
    }
}
