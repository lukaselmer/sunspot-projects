package org.sunspotworld.game;

import java.util.Random;

public class Configuration {

    public static int MAXROWS = 144;
    public static int MAXCOLUMNS = MAXROWS;
    public static Random random = new Random();

    static int getStartXPos1() {
        return random.nextInt(MAXROWS);
    }

    static int getStartYPos1() {
        return random.nextInt(MAXROWS);
    }

    static int getStartXPos2() {
        return random.nextInt(MAXCOLUMNS);
    }

    static int getStartYPos2() {
        return random.nextInt(MAXCOLUMNS);
    }
}
