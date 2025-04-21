package tello;

import java.awt.event.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.net.ServerSocket;
import java.net.Socket;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;
import tellolib.camera.TelloCamera;
import tellolib.command.TelloFlip;

public class myDemo extends JFrame 
{
    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private final Logger logger = Logger.getGlobal();
    private TelloCamera camera;
    private JLabel videoLabel = new JLabel(); // NEW: label to show video frames
    private JFrame mapFrame;
    private movementMap mapPanel;

    public void execute() 
    {
        //KILLING MYSELF
        frame = new JFrame("Pilot Controls");
        frame.setSize(960, 720); // fits the videoLabel nicely
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //FUUUUUUUUUUUCK
        JButton rise = new JButton("Rise");
        JButton lower = new JButton("Lower");
        JButton land = new JButton("Land");
        JButton forward = new JButton("Forward");
        JButton back = new JButton("Back");
        JButton right = new JButton("Right");
        JButton left = new JButton("Left");
        JButton picture = new JButton("Picture");

        // when the map uh... the uhhhh...
        mapPanel = new movementMap();
        mapFrame = new JFrame("Drone Tracker");
        mapFrame.setSize(500, 500);
        mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mapFrame.add(mapPanel);
        mapFrame.pack();
        mapFrame.setVisible(true);
        System.out.println("map init");

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel altitudePanel = new JPanel(new FlowLayout());
        altitudePanel.add(rise);
        altitudePanel.add(lower);

        JPanel directionalPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        directionalPanel.add(picture);
        directionalPanel.add(forward);
        directionalPanel.add(new JLabel(""));
        directionalPanel.add(left);
        directionalPanel.add(land);
        directionalPanel.add(right);
        directionalPanel.add(new JLabel(""));
        directionalPanel.add(back);
        directionalPanel.add(new JLabel(""));

        mainPanel.add(altitudePanel, BorderLayout.NORTH);
        mainPanel.add(directionalPanel, BorderLayout.CENTER);
        mainPanel.add(videoLabel, BorderLayout.SOUTH); // add video label to bottom

        frame.add(mainPanel);
        frame.setVisible(true);

        logger.info("start");

        TelloControl telloControl = TelloControl.getInstance();
        TelloDrone drone = TelloDrone.getInstance();
        telloControl.setLogLevel(Level.FINE);
        camera = TelloCamera.getInstance();

        try {
            telloControl.connect();
            telloControl.enterCommandMode();
            System.out.println("Command mode successful"); 

            telloControl.streamOn();
            Thread.sleep(2000); // Allow stream to stabilize

            camera.startVideoCapture(true);

            // Socket thread for video streaming
            new Thread(() -> {
                try {
                    // Create a server socket that waits for a Python client to connect on port 9997
                    ServerSocket serverSocket = new ServerSocket(9997); 
                    System.out.println("Waiting for Python client on port 9997...");
                    Socket clientSocket = serverSocket.accept(); 
                    System.out.println("Python client connected!");

                    // Creates a stream to send image data through the socket to the python client
                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                    // This continuously takes video frames and send them to the Python client
                    while (true) {
                        Mat frameMat = camera.getImage(); // Gets the current frame from the drone camera
                        if (frameMat != null && !frameMat.empty()) {
                            // Encode the frame into JPEG format and turns it into a byte array
                            MatOfByte buffer = new MatOfByte();
                            Imgcodecs.imencode(".jpg", frameMat, buffer);
                            byte[] byteArray = buffer.toArray();

                            // Send the length of the byte array and the data itself
                            out.writeInt(byteArray.length);
                            out.write(byteArray);

                            // Convert the current OpenCV frame to a BufferedImage and display it in the GUI
                            BufferedImage image = matToBufferedImage(frameMat);
                            // Updates the live feed
                            if (image != null) {
                                videoLabel.setIcon(new ImageIcon(image));
                            }
                        }

                        Thread.sleep(100); // This delay is to reduce CPU usage
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (telloControl.getConnection() == TelloConnection.CONNECTED && drone.isFlying()) {
                try {
                    telloControl.land();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //RISE MY GLORIOUS CREATION
        rise.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.up(50); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //KEEP YOUR HEAD DOWN, LOWER
        lower.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.down(50); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //GET BACK HERE AND LAND
        land.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { telloControl.land(); }
        });

        //FORWARD FOR THE HUNT
        forward.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.forward(50); mapPanel.moveUp(); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //BACK UP THAT ASS
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.backward(50); mapPanel.moveDown(); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //SWIPE LEFT UGLY BITCH
        left.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.left(50); mapPanel.moveLeft(); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //SWIPE RIGHT ON MOMMY
        right.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.right(50); mapPanel.moveRight(); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //Big image gaming?????
        picture.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) 
            {
                camera.takePicture(System.getProperty("user.dir") + "\\Photos");
                ProcessBuilder pb = new ProcessBuilder("python", "src/main/python/ParkingSpots.py");
                pb.directory(new File(System.getProperty("user.dir")));
                pb.inheritIO();
                try {
                    pb.start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        logger.info("end");
    }

    // Converts OpenCV Mat to BufferedImage for displaying
    private BufferedImage matToBufferedImage(Mat mat) {
        // Makes sure that the frame is not empty
        if (mat == null || mat.empty()) {
            System.out.println("Skipped frame: mat was null or empty."); 
            return null;
        }

        // Encode the Mat into JPEG format and store it in a MatOfByte
        MatOfByte mob = new MatOfByte();
        boolean success = Imgcodecs.imencode(".jpg", mat, mob);
        if (!success) {
            System.out.println("Failed to encode Mat to JPEG."); 
            return null;
        }

        // Convert the encoded image to a byte array
        byte[] byteArray = mob.toArray();

        // Decode the byte array into a BufferedImage using ImageIO
        try {
            return ImageIO.read(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
