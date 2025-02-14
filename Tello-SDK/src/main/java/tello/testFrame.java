package tello;

import javax.swing.*;
import java.awt.*;

public class testFrame extends JFrame{
		private static final long serialVersionUID = 1L;
		private JFrame frame;
		
		public testFrame() {
			frame = new JFrame("Controls");
			frame.setVisible(true);
			frame.setSize(600,400);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			JButton rise = new JButton("Rise");
			JButton lower = new JButton("Lower");
			
			frame.getContentPane().add(BorderLayout.EAST, rise);
			frame.getContentPane().add(BorderLayout.WEST, lower);
		}
	
}
