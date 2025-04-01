package tello;

import javax.swing.*;
import java.awt.*;

public class movementMap extends JPanel {

    int sizeX = 500;
    int sizeY = 500;

    private int x = sizeX/2; // Default starting X position (center of the panel)
    private int y = sizeY/2; // Default starting Y position (center of the panel)

    public movementMap() {
        setPreferredSize(new Dimension(500, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw the red dot at the current coordinates
        g.setColor(Color.RED);
        g.fillOval(x - 5, y - 5, 10, 10); // Draw a small circle (5x5 pixels)
        System.out.println("building map");
    }

    public void moveUp() {
        System.out.println("Moving Up: " + y);
        revalidate();
        y -= 10;
        repaint();
    }
    
    public void moveDown() {
        System.out.println("Moving Down: " + y);
        revalidate();
        y += 10;
        repaint();
    }
    
    public void moveLeft() {
        System.out.println("Moving Left: " + x);
        revalidate();
        x -= 10;
        repaint();
    }
    
    public void moveRight() {
        System.out.println("Moving Right: " + x);
        revalidate();
        x += 10;
        repaint();
    }
}    
