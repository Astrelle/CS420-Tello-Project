package tello;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// These are for socket video streaming to process the images
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;
import tellolib.camera.TelloCamera;
import tellolib.command.TelloFlip;

public class myDemo extends JFrame {

    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private final Logger logger = Logger.getGlobal();
    private TelloCamera camera;
    private JFrame mapFrame; // <-the map in question
    private movementMap mapPanel;

    public void execute() {
        //KILLING MYSELF
        frame = new JFrame("Pilot Controls");
        frame.setSize(600, 400);
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

         //when the map uh... the uhhhh...
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
            telloControl.takeOff();
            telloControl.streamOn();
            camera.startVideoCapture(true);

            // Starts a thread in the background for socket video streaming
            new Thread(() -> {
                try {
                    // Creates a socket server that listens for connections on port 9999
                    ServerSocket serverSocket = new ServerSocket(9999);
                    System.out.println("Waiting for client connection on port 9999...");

                    // This waits until DroneVideoViewer connects
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected!");

                    // This sets up a stream to send data to DroneVideoViewer
                    DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                    // Loop that continously sends video frames
                    while (true) {
                        // uses OpenCV Mat to get the newest video frames
                        Mat frameToRead = camera.getImage();
                        if (frameToRead != null) {
                            // This changes the Mat image into a jpg byte array
                            MatOfByte buffer = new MatOfByte();
                            Imgcodecs.imencode(".jpg", frameToRead, buffer);
                            byte[] byteArray = buffer.toArray();

                            //Sends length of data for the DroneVideoVIewer and sends byte array to it
                            out.writeInt(byteArray.length);
                            out.write(byteArray);
                        }
                        // has mercy on the CPU just in case
                        Thread.sleep(100);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (telloControl.getConnection() == TelloConnection.CONNECTED && drone.isFlying()) {
                try {
                    // optional: cleanup
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ///RISE MY GLORIOUS CREATION
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
            public void mousePressed(MouseEvent e) { telloControl.backward(50); mapPanel.moveDown();}
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //SWIPE LEFT UGLY BITCH
        left.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.left(50); mapPanel.moveLeft();}
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
                camera.takePicture(System.getProperty("user.dir") + "\\Photos"); //This sends it to Tello-Sdk/Photos. Check there.
                ProcessBuilder pb = new ProcessBuilder("python", "src/main/python/parkingSpots.py");
                pb.directory(new File(System.getProperty("user.dir")));
                pb.inheritIO();
                try {
					Process process = pb.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }; 
        });

        logger.info("end");
    }
}