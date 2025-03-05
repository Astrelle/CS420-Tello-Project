package tello;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class myDemo extends JFrame 
{

    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private final Logger logger = Logger.getGlobal();

    public void execute() 
	{
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

        //JPANEL GAPS AND SHIT
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        //TOP IS UP DOWN
        JPanel altitudePanel = new JPanel(new FlowLayout());
        altitudePanel.add(rise);
        altitudePanel.add(lower);

        //FOUR PANELS ARE EMPTY FROM HERE, YOU CAN CHANGE. THEY HAVE EMPTY TEXTS FOR CHANGING
        JPanel directionalPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        directionalPanel.add(picture); //EMPTY TOP LEFT
        directionalPanel.add(forward);
        directionalPanel.add(new JLabel("")); //FILL IT TOP RIGHT

        directionalPanel.add(left);
        directionalPanel.add(land);
        directionalPanel.add(right);           

        directionalPanel.add(new JLabel("")); // EMPTY BOTTOM LEFT
        directionalPanel.add(back);
        directionalPanel.add(new JLabel("")); // FILL IT BOTTOM RIGHT

        //ADDS TO THE MAIN PANEL
        mainPanel.add(altitudePanel, BorderLayout.NORTH);
        mainPanel.add(directionalPanel, BorderLayout.CENTER);

        //ADDS AND VISIBLE
        frame.add(mainPanel);
        frame.setVisible(true);

        logger.info("start");

        //TELLO CONTROLS
        TelloControl telloControl = TelloControl.getInstance();
        TelloDrone drone = TelloDrone.getInstance();
        telloControl.setLogLevel(Level.FINE);

        try 
		{
            telloControl.connect();
            telloControl.enterCommandMode();
            telloControl.takeOff();
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        } 
		finally 
		{
            if (telloControl.getConnection() == TelloConnection.CONNECTED && drone.isFlying()) {
                try 
				{
                } 
				catch (Exception e) 
				{
                    e.printStackTrace();
                }
            }
        }

        //THE VOICES LISTEN

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
            public void mousePressed(MouseEvent e) { telloControl.forward(50); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //BACK UP THAT ASS
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.backward(50); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //SWIPE LEFT UGLY BITCH
        left.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.left(50); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //SWIPE RIGHT ON MOMMY
        right.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { telloControl.right(50); }
            @Override
            public void mouseReleased(MouseEvent e) { telloControl.stop(); }
        });

        //Big image gaming?????
        picture.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {TelloControl.takePicture(); } //May also be TelloCamera.takePicture()
        });

        logger.info("end");
    }
}
