package tello;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DroneVideoViewer {

    public static void main(String[] args) {
        // IP address of the machine running the drone socket. will have to change dependant on whos running
        String serverIp = "172.17.106.109";
        int port = 9999;

        // Creates a window to show the drone video
        JFrame frame = new JFrame("Drone Video Viewer");
        JLabel label = new JLabel(); // Holds the image frame
        frame.getContentPane().add(label);
        frame.setSize(960, 720); // Window size
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try (
            // Connects to the server where the drone feed is being sent from
            Socket socket = new Socket(serverIp, port);
            DataInputStream input = new DataInputStream(socket.getInputStream())
        ) {
            while (true) {
                // Reads the number of bytes for the next image
                int len = input.readInt();
                byte[] data = new byte[len];

                // Reads the image bytes from the socket
                input.readFully(data);

                // Converts the bytes back into an image
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));

                // Should show the image in the window
                if (img != null) {
                    label.setIcon(new ImageIcon(img));
                    frame.repaint(); // Refresh the frame to display new image
                }
            }

        } catch (IOException e) {
            // Handle connection or image errors
            e.printStackTrace();
        }
    }
}
