package org.sunspotworld.game;

public class PlayBoard {

    public int xPos1 = 5, yPos1 = 5;
    public int xPos2 = 4, yPos2 = 4;

    public PlayBoard() {
        check();
    }

    public void move1(int x, int y) {
        if (x == 1) {
            if (x + xPos1 < Configuration.MAXCOLUMNS) {
                xPos1++;
            }
        }
        if (x == -1) {
            if (xPos1 > 0) {
                xPos1--;
            }
        }
        if (y == 1) {
            if (y + yPos1 < Configuration.MAXROWS) {
                yPos1++;
            }
        }
        if (y == -1) {
            if (yPos1 > 0) {
                yPos1--;
            }
        }

        //System.out.println("x1: " + x + " y1: " + y);
    }

    public void move2(int x, int y) {
        if (x == 1) {
            if (x + xPos2 < Configuration.MAXCOLUMNS) {
                xPos2++;
            }
        }
        if (x == -1) {
            if (xPos2 > 0) {
                xPos2--;
            }
        }
        if (y == 1) {
            if (y + yPos2 < Configuration.MAXROWS) {
                yPos2++;
            }
        }
        if (y == -1) {
            if (yPos2 > 0) {
                yPos2--;
            }
        }
    }

    public boolean check() {
        if ((Math.abs(xPos1 - xPos2) <= 3) && (Math.abs(yPos1 - yPos2) <= 3)) {
            xPos1 = Configuration.getStartXPos1();
            yPos1 = Configuration.getStartYPos1();
            xPos2 = Configuration.getStartXPos2();
            yPos2 = Configuration.getStartYPos2();
            check();
            return true;
        } else {
            return false;
        }
    }
}
