package org.sunspotworld.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author Jay Mahadeokar
 */
public class GridPanel extends JPanel {

    public PlayBoard playboard = new PlayBoard();
    double xInc, yInc;
    final int ROWS = Configuration.MAXROWS,
            COLUMNS = Configuration.MAXCOLUMNS,
            DRAW = 0,
            FILL = 1,
            PAD = 20;

    public GridPanel() {
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double w = getWidth();
        double h = getHeight();
        xInc = (w - 2 * PAD) / COLUMNS;
        yInc = (h - 2 * PAD) / ROWS;
        // row lines
        double x1 = PAD, y1 = PAD, x2 = w - PAD, y2 = h - PAD;
        /*for (int j = 0; j <= ROWS; j++) {
        g2.draw(new Line2D.Double(x1, y1, x2, y1));
        y1 += yInc;
        }*/
        // col lines
        y1 = PAD;
        /*for (int j = 0; j <= COLUMNS; j++) {
        g2.draw(new Line2D.Double(x1, y1, x1, y2));
        x1 += xInc;
        }*/

        x1 = PAD + playboard.xPos1 * xInc + 1;
        y1 = PAD + playboard.yPos1 * yInc + 1;
        g2.setPaint(Color.RED);
        g2.fill(new Ellipse2D.Double(x1 - 10, y1 - 10, xInc + 15, yInc + 15));
        g2.setPaint(Color.ORANGE);
        g2.fill(new Ellipse2D.Double(x1 - 5, y1 - 5, xInc + 5, yInc + 5));

        x1 = PAD + playboard.xPos2 * xInc + 1;
        y1 = PAD + playboard.yPos2 * yInc + 1;
        g2.setPaint(Color.GREEN);
        g2.fill(new Ellipse2D.Double(x1 - 10, y1 - 10, xInc + 15, yInc + 15));
        g2.setPaint(Color.CYAN);
        g2.fill(new Ellipse2D.Double(x1 - 5, y1 - 5, xInc + 5, yInc + 5));
    }

    public void check() {
        if (playboard.check()) {
            JOptionPane.showMessageDialog(this, "Gefangen! Nochmal!?");
        }
    }
}
