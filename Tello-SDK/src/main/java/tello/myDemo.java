package tello;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;

import tellolib.command.TelloFlip;
import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class myDemo extends JFrame{
	
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	
	private final Logger logger = Logger.getGlobal();
	
	public void execute() {
		
		frame = new JFrame("Controls");
		frame.setVisible(true);
		frame.setSize(600,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton rise = new JButton("Rise");
		JButton lower = new JButton("Lower");
		JButton land = new JButton("Land");
		JButton forward = new JButton("Foward");
		JButton back = new JButton("Back");
		JButton right = new JButton("Right");
		JButton left = new JButton("Left");
		
		//frame.getContentPane().add(BorderLayout.EAST, rise);
		//frame.getContentPane().add(BorderLayout.WEST, lower);
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
		rise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				telloControl.up(50);
			}
		});
		
		// Exception in thread "AWT-EventQueue-0" tellolib.exception.TelloConnectionException: Socket closed 
		lower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				telloControl.down(50);
			}
		});
		
		land.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				telloControl.land();
			}
		});
		
		forward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				telloControl.forward(50);
			}
		});
		
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				telloControl.backward(50);
			}
		});
		
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				telloControl.left(50);
			}
		});
		
		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				telloControl.right(50);
			}
		});
		
		logger.info("end");
	}
	
}
