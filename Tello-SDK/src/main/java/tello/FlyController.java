package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import tellolib.camera.TelloCamera;
import tellolib.command.TelloFlip;
import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

/**
 * FlyController maps gamepad inputs to Tello drone commands,
 * handling takeoff, landing, motion, flips, and media capture.
 */
public class FlyController
{
	private final Logger logger = Logger.getGlobal(); 

	private TelloControl		telloControl;
	private TelloDrone			drone;
	private TelloCamera			camera;
	private ControllerManager	controllers;
   
    /**
     * Main execution loop: initializes controller, connects to drone,
     * and processes gamepad events until landing.
     * @throws Exception on initialization or communication error
     */
	public void execute() throws Exception
	{
		int		leftX, leftY, rightX, rightY, deadZone = 10;

		logger.info("start");

		// Initialize gamepad manager (supports multiple controllers).
	    controllers = new ControllerManager();
		controllers.initSDLGamepad();
    	
	    // Verify presence of first controller.
    	ControllerState currState = controllers.getState(0);
    	
    	if (!currState.isConnected) throw new Exception("controller not connected");

		// Acquire singleton instances for drone control, status, and video.
	    telloControl = TelloControl.getInstance();
	    
	    drone = TelloDrone.getInstance();
	    
	    camera = TelloCamera.getInstance();

	    telloControl.setLogLevel(Level.FINE);
		
		// Controller mapping:
		// Start button = take off
		// Back button  = land
		// A button     = take picture
	    // B button     = toggle video recording
	    // Y button     = stop, go into hover
	    // Dpad.up      = flip forward
		//
		// right joystick Y axis = forward/backward
		// right joystick X axis = left/right
		//
		// left joystick Y axis  = up/down
		// left joystick X axis  = rotate left/right

	    try 
	    {
		    telloControl.connect();
		    
		    telloControl.enterCommandMode();
		    
		    telloControl.startStatusMonitor();
		    
		    telloControl.streamOn();
		    
		    camera.setStatusBar(this::updateWindow);
		    
		    camera.startVideoCapture(true);
		   
		    // Now we loop until land button is pressed or we lose connection.
		    
		    while(telloControl.getConnection() == TelloConnection.CONNECTED) 
		    {
		    	// Read the current state of the first (and in our case only)
		    	// game pad.
		    	
		    	currState = controllers.getState(0);
		    	  
		    	// Back button lands drone and exits while loop.
		    	
		    	if (currState.backJustPressed) 
		    	{
		    		logger.info("back button");
		    		
		    		if (drone.isFlying()) telloControl.land();
		    		
		    		break;
		    	}
		    	
		    	// Start button takes off.

		    	if (currState.startJustPressed)
		    	{
		    		logger.info("start button");
		    		
		    		if (drone.isFlying())
		    			telloControl.land();
		    		else
		    			telloControl.takeOff();
		    	}

		    	// A button takes a picture.
		    	
		    	if  (currState.aJustPressed) camera.takePicture(System.getProperty("user.dir") + "\\Photos");
		    	
		    	// B button toggles video recording.
		    	
		    	if (currState.bJustPressed)
		    	{
		    		if (camera.isRecording())
		    			camera.stopRecording();
		    		else
		    			camera.startRecording(System.getProperty("user.dir") + "\\Photos");
		    	}

		    	// If flying, pass the controller joystick deflection to the drone via
		    	// the flyRC command.
		    	
		    	if (drone.isFlying())
		    	{
		    		// scale controller stick axis range -1.0 to + 1.0 to -100 to + 100
		    		// used by the drone flyRC command. Apply a dead zone to allow
		    		// for stick axis not always returning 0 when released.
		    		leftX = deadZone((int) (currState.leftStickX * 100.0), deadZone);
		    		leftY = deadZone((int) (currState.leftStickY * 100.0), deadZone);
		    		rightX = deadZone((int) (currState.rightStickX * 100), deadZone);
		    		rightY = deadZone((int) (currState.rightStickY * 100), deadZone);
		    		
		    		//logger.info("lr=" + rightX + " fb=" + rightY + " ud=" + leftY + " yaw=" + leftX);
		    		
		    		// Pass joy stick deflection to drone.
		    		//                  L/R      F/B    U/D    YAW
	    			telloControl.flyRC(rightX, rightY, leftY, leftX);

	    			// Flips or other direct drone commands must go here to be executed after FlyRC is done.
	    			
		    		if (currState.dpadUpJustPressed) telloControl.doFlip(TelloFlip.forward);
		    		
		    		if (currState.yJustPressed) telloControl.stop();
		    	}
		    	
		    	Thread.sleep(100);
		    }
	    }	
	    catch (Exception e) {
	    	e.printStackTrace();
	    } finally 
	    {
	    	if (telloControl.getConnection() == TelloConnection.CONNECTED && drone.isFlying())
	    	{
	    		try
	    		{telloControl.land();}
	    		catch(Exception e) { e.printStackTrace();}
	    	}
	    }
	    
    	telloControl.disconnect();
	    
	    logger.info("end");
	}
	
    /**
     * Zeroes small stick values to account for joystick imperfections.
     * @param value axis value scaled to [-100,100]
     * @param threshold minimum magnitude to register
     * @return adjusted axis value
     */
	private int deadZone(int value, int minValue)
	{
		if (Math.abs(value) < minValue) value = 0;
		
		return value;
	}	
	
    /**
     * Constructs status overlay text for live video feed.
     * @return formatted battery, altitude, heading, and flight status
     */
	private String updateWindow()
	{
    	 return String.format("Batt: %d  Alt: %d  Hdg: %d  Rdy: %b", drone.getBattery(), drone.getHeight(), 
    			drone.getHeading(), drone.isFlying());
	}
}

