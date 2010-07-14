package org.sunspotworld.game;

/**
 *
 * @author Lukas Elmer
 */
public class GameMove {

    public final double x, y;
    public final String client;

    public GameMove(double x, double y, String client) {
        this.x = x;
        this.y = y;
        this.client = client;
    }
}
