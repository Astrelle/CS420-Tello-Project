package tello;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel showing a red dot that smoothly moves to target coordinates.
 */
public class movementMap extends JPanel {

    private static final int sizeX = 500;
    private static final int sizeY = 500;

    private int x = sizeX / 2;
    private int y = sizeY / 2;
    private int movex = x;
    private int movey = y;

    private Timer animationtimer;

    /**
     * Initializes panel size and starts animation timer.
     */
    public movementMap() {
        setPreferredSize(new Dimension(sizeX, sizeY));
        animationtimer = new Timer(30, e -> stepanimation());
        animationtimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillOval(x - 5, y - 5, 10, 10);
        //System.out.println("building map");
    }

    /**
     * Moves the dot up to 2px toward target each tick; repaints if moved.
     */
    private void stepanimation() {
        int speed = 2;
        boolean move = false;

        if (x < movex) {
            x = Math.min(x + speed, movex);
            move = true;
        } else if (x > movex) {
            x = Math.max(x - speed, movex);
            move = true;
        }

        if (y < movey) {
            y = Math.min(y + speed, movey);
            move = true;
        } else if (y > movey) {
            y = Math.max(y - speed, movey);
            move = true;
        }

        if (move) {
            repaint();
        }
    }
    
    // Move target by 20px, clamped to panel bounds
    public void moveUp() {
        movey = Math.max(0, movey - 20);
    }

    public void moveDown() {
        movey = Math.min(sizeY, movey + 20);
    }

    public void moveLeft() {
        movex = Math.max(0, movex - 20);
    }

    public void moveRight() {
        movex = Math.min(sizeX, movex + 20);
    }
}
