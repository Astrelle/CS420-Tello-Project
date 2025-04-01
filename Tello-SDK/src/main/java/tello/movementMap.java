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
        y = Math.max(0, y - 10);  // Prevents out-of-bounds movement
        repaint();
    }
    
    public void moveDown() {
        y = Math.min(sizeY, y + 10);
        repaint();
    }
    
    public void moveLeft() {
        x = Math.max(0, x - 10);
        repaint();
    }
    
    public void moveRight() {
        x = Math.min(sizeX, x + 10);
        repaint();
    }
    
}    
