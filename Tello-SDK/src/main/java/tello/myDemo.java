package tello;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;

import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class myDemo extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	
        @SuppressWarnings("NonConstantLogger")
	private final Logger logger = Logger.getGlobal();
	
        @SuppressWarnings({"CallToPrintStackTrace", "Convert2Lambda"})
	public void execute() { // method for executing the demo
		
		// sets the Jframe attributes
		frame = new JFrame("Controls");
		frame.setVisible(true);
		frame.setSize(600,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//control buttons
		JButton rise = new JButton("Rise");
		JButton lower = new JButton("Lower");
		JButton land = new JButton("Land");
		JButton forward = new JButton("Foward");
		JButton back = new JButton("Back");
		JButton right = new JButton("Right");
		JButton left = new JButton("Left");
		
		// adds buttons to the Jframe
		frame.getContentPane().add(BorderLayout.EAST, rise);
		frame.getContentPane().add(BorderLayout.WEST, lower);
		frame.getContentPane().add(BorderLayout.CENTER, land);
		frame.getContentPane().add(BorderLayout.NORTH, forward);
		frame.getContentPane().add(BorderLayout.SOUTH, back);
		frame.getContentPane().add(BorderLayout.EAST, right);
		frame.getContentPane().add(BorderLayout.WEST, left);
		
		frame.setVisible(true);
		
		logger.info("start");
		
		TelloControl telloControl = TelloControl.getInstance();
		TelloDrone drone = TelloDrone.getInstance();
		
		telloControl.setLogLevel(Level.FINE);
		
		try {
			telloControl.connect();
			
			telloControl.enterCommandMode();
			telloControl.takeOff();
			
			// Should be able to give it commands to fly now?
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (telloControl.getConnection() == TelloConnection.CONNECTED && drone.isFlying()) {
				try {

				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//ActionListeners don't crash but say connection is closed so should be good!
		
		// Exception in thread "AWT-EventQueue-0" tellolib.exception.TelloConnectionException: Socket closed
		rise.addMouseListener(new MouseAdapter() { //moves drone up when pressed, stops when released
			@Override public void mousePressed(MouseEvent e) { telloControl.up(50); }
			@Override public void mouseReleased(MouseEvent e) { telloControl.stop(); }
		});
		
		// Exception in thread "AWT-EventQueue-0" tellolib.exception.TelloConnectionException: Socket closed 
		lower.addMouseListener(new MouseAdapter() { //moves dron down when pressed, stops when released
			@Override public void mousePressed(MouseEvent e) { telloControl.down(50); }
			@Override public void mouseReleased(MouseEvent e) { telloControl.stop(); }
		});
		
		// lands drone
		land.addActionListener(new ActionListener() { 
                        @SuppressWarnings("override")
			public void actionPerformed(ActionEvent e) {
				telloControl.land();
			}
		});
		
		//moves drone forward when pressed, stops when released
		forward.addMouseListener(new MouseAdapter() { 
			@Override public void mousePressed(MouseEvent e) { telloControl.forward(50); }
			@Override public void mouseReleased(MouseEvent e) { telloControl.stop(); }
		});
		
		//moves drone backward when pressed, stops when released
		back.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) { telloControl.backward(50); }
			@Override public void mouseReleased(MouseEvent e) { telloControl.stop(); }
		});
		
		//moves drone left when pressed, stops when released
		left.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) { telloControl.left(50); }
			@Override public void mouseReleased(MouseEvent e) { telloControl.stop(); }
		});
		
		//moves drone right when pressed, stops when released
		right.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) { telloControl.right(50); }
			@Override public void mouseReleased(MouseEvent e) { telloControl.stop(); }
		});
		
		logger.info("end");
	}
	
}
