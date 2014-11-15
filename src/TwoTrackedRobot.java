import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.util.Delay;

/**
 * @author Andoni Roman
 * TwoTrackedRobot class, similar to TwoWheeledRobot, responsible of every robot's movement. 
 *
 */
public class TwoTrackedRobot extends Thread  {
	
	private final int Minimum_SPEED = 100;
	private final int Maximum_SPEED = 400;
	private final int Scaling_SPEED = 10; //factor by which the error is multiplied to get the speed
	private static final int LOCALIZATION_SPEED = 80;
    private static final int FORWARD_SPEED = 250;
	private final int ROTATE_SPEED = 120;
	
	private static int spd;
	
	
	NXTRegulatedMotor leftMotor = Motor.A;
	NXTRegulatedMotor rightMotor = Motor.C;
	NXTRegulatedMotor clawMotor = Motor.B;
	
	private static double WHEEL_BASE;          //subject to change
	private static double WHEEL_RADIUS;      //subject to change
	private boolean isNavigating;
	public double thetar, xr, yr;
	public static double destinationX, destinationY;
	
	/**
	 * Constructor for TwoTrackedRobot
	 */
	private Odometer odo;
	@SuppressWarnings("deprecation")
	
	public TwoTrackedRobot(Odometer odometer){
		this.odo =  odometer;
		//clawMotor.lock(100);                     //power lock for arm motor: to be considered
		isNavigating = false;
		WHEEL_RADIUS=Main.WHEEL_RADIUS;
		WHEEL_BASE=Main.WHEEL_BASE;
		
	}
	
/**
 * Travel method, moves robot to passed coordinate, relative to staring coordinate
 * Angle and distance of traveling is calculated by using trigonometry, then goForward and turnTo methods are called to the point
 * A second travel method is also implemented, returns immediately
 * @param x Coordinate of destination
 * @param y Coordinate of destination
 */
	public void travel (double x, double y)
	{
		    destinationX = x;
			destinationY = y;

			
			synchronized (odo.lock) 
			{                                                                 //avoid other methods accessing data (colision avoidance)
				thetar = odo.getTheta() * 180 / Math.PI;
				yr = odo.getY();
				xr = odo.getX();	
			}
			
			double directionAngle =  Math.atan2(x - xr, y - yr) * 180 / Math.PI;     //angle calculated from 0 degrees
			
			double correctedAngle =  directionAngle - thetar;                        //calculation of corrected angle (angle to which robot must actually turn)
			
			double dist  = Math.sqrt(Math.pow((y-yr), 2) + Math.pow((x-xr),2));
		                                                                             //min angle to turn
			if(correctedAngle < -180)
			{
				turnTo(correctedAngle + 360);                                        //turnTo and goForward is called, depending on cases.
			}
			else if(correctedAngle > 180)
			{
				turnTo(correctedAngle - 360);
			}
			else turnTo(correctedAngle);
			
			goForward(dist);                                                        
	}
	
	
	public void travel (double x, double y, boolean immediateReturn)      //Same method as above, but returns immediately
	{               
		destinationX = x;
		destinationY = y;

		
		synchronized (odo.lock) 
		{                                                                 //avoid other methods accessing data (colision avoidance)
			thetar = odo.getTheta() * 180 / Math.PI;
			yr = odo.getY();
			xr = odo.getX();	
		}
		
		double directionAngle =  Math.atan2(x - xr, y - yr) * 180 / Math.PI;     //angle calculated from 0 degrees
		
		double correctedAngle =  directionAngle - thetar;                        //calculation of corrected angle (angle to which robot must actually turn)
		
		double distance  = Math.sqrt(Math.pow((y-yr), 2) + Math.pow((x-xr),2));
	                                                                             //min angle to turn
		if(correctedAngle < -180)
		{
			turnTo(correctedAngle + 360);                                        //turnTo and goForward is called, depending on cases.
		}
		else if(correctedAngle > 180)
		{
			turnTo(correctedAngle - 360);
		}
		else turnTo(correctedAngle);
		
		goForward(distance, immediateReturn);                                                        
}
	
	
	/**
	 * Turns the robot to given position
	 * @param theta	in degrees
	 */
		public void turnTo (double theta)
		{
		
			leftMotor.setSpeed(LOCALIZATION_SPEED);
			rightMotor.setSpeed(LOCALIZATION_SPEED);
			isNavigating = true;
			leftMotor.rotate(convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), true);
			rightMotor.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), false);
			isNavigating = false;
		}
		
		
	/**
	 * Turns the robot to given position at particular speed, angle and speed passed as parameters
	 * ImmediateReturn version implemented below
	 * @param theta	degrees 
	 * @param spd (turning speed)
	 */
		
		public void turnToSp (double theta, int spd)
		{
			
			leftMotor.setSpeed(spd);
			rightMotor.setSpeed(spd);
			isNavigating = true;
			leftMotor.rotate(convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), true);
			rightMotor.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), false);
			isNavigating = false;
		}
		
		/**
		 * Turns the robot to given position at particular speed, angle and speed passed as parameters, immediate return
		 * 
		 * @param theta	degrees 
		 * @param spd (turning speed)
		 */
			public void turnToSp (double theta, int spd, boolean immediateReturn)
			{
				
				leftMotor.setSpeed(spd);
				rightMotor.setSpeed(spd);
				isNavigating = true;
				leftMotor.rotate(convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), true);
				rightMotor.rotate(-convertAngle(WHEEL_RADIUS, WHEEL_BASE, theta), immediateReturn);
				isNavigating = false;
			}
	 
/**
 * Computes degrees needed to rotate for motor. Degrees are calculated by passing distance parameter
 * Returned to immediately
 * An immediate return version is implemented below
 * @param dist  distance that robot travels forward (cm)
 */
	public void goForward(double dist)
	{
		
		spd = FORWARD_SPEED;
		leftMotor.setSpeed(spd);
		rightMotor.setSpeed(spd);
		isNavigating = true;
	    leftMotor.rotate(convertDistance(WHEEL_RADIUS, dist), true);
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, dist), true);
		isNavigating = false;
	}
	
	
/**
 * @param Same code as above, but waits until robot moved before continuing execution of further code
 */
	public void goForward(double dist, boolean immediateReturn)
	{
		
		spd = FORWARD_SPEED;
		leftMotor.setSpeed(spd);
		rightMotor.setSpeed(spd);
		isNavigating = true;
		leftMotor.rotate(convertDistance(WHEEL_RADIUS, dist), true);
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, dist), immediateReturn);
		isNavigating = false;
	}
	
	
/**
 * Computes degrees needed to rotate for motor backwards. Degrees are calculated by passing distance parameter
 * An immediate return version is implemented below
 * @param dist  distance that robot travels backward (cm)
 */
	public void goBackward(double dist)
	{
		spd = FORWARD_SPEED;
		leftMotor.setSpeed(spd);
		rightMotor.setSpeed(spd);
		isNavigating = true;
	    leftMotor.rotate(-convertDistance(WHEEL_RADIUS, dist), true);
		rightMotor.rotate(-convertDistance(WHEEL_RADIUS, dist), false);
		isNavigating = false;
	}
	
/**
 * Computes degrees needed to rotate for motor backwards. Degrees are calculated by passing distance parameter
 * Returned to immediately
 * @param dist  distance that robot travels backward (cm)
 */
	public void goBackward(double dist, boolean immediateReturn)
	{
		
		spd = FORWARD_SPEED;
		leftMotor.setSpeed(spd);
		rightMotor.setSpeed(spd);
		isNavigating = true;
		leftMotor.rotate(-convertDistance(WHEEL_RADIUS, dist), true);
		rightMotor.rotate(-convertDistance(WHEEL_RADIUS, dist), immediateReturn);
		isNavigating = false;
	}
	
	
	
	
	
/**
 * Makes the robot rotate at localization speed.                                     
 * Robot will rotate without stopping until next movement is executed.
 * @param forward (robot rotation backwards/forward)
 */
	public void rotate (boolean forward)
	{
		leftMotor.setSpeed(LOCALIZATION_SPEED);
		rightMotor.setSpeed(LOCALIZATION_SPEED);
		if (forward)
		{
			leftMotor.forward();
			rightMotor.backward();
		} 
		else 
		{ 
			leftMotor.backward();
			rightMotor.forward();
		}
	}
	/**
	 * Makes the robot rotate at SPECIFIC speed.                                      
	 * Robot will rotate without stopping until next movement is executed.
	 * @param forward (robot rotation backwards/forward)
	 */
	public void rotateSp (boolean forward, int speed)
	{
		leftMotor.setSpeed(speed);
		rightMotor.setSpeed(speed);
		if (forward){
			leftMotor.forward();
			rightMotor.backward();
		} else { 
			leftMotor.backward();
			rightMotor.forward();
		}
	}
/**
 * Boolean to return whether robot is moving or not
 * @return boolean for movement
 */
   public boolean isMoving()
   {
	   boolean Moves= (leftMotor.isMoving() || rightMotor.isMoving());
	   return Moves;
	}

/**
 * Stops left and right motors of the robot
 */
	public void stop()
	{
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
	}

/**
 * Returns true if the robot is navigating
 * 
 * @return boolean for robot travel (returns if travels or not)
 */
	public boolean isNavigating()
	{   
		boolean Moves=this.isNavigating;
		return Moves;
	}
	
	/**
	 * Makes the robot turn to an absolute angle value, at provided speed
	 * Immediate return version is below
	 * @param theta angle to turn
	 * @param spd speed of turn
	 */
	public void turnToABS(double theta, int spd)
	{
		this.turnToSp((Math.toDegrees(-odo.getTheta()) + theta), spd);
	}
	public void turnToABS(double theta, int spd, boolean immediateReturn)
	{
		this.turnToSp((Math.toDegrees(-odo.getTheta()) + theta), spd, immediateReturn);
	} 
	
	
	
	
	public static int convertDistance(double radius, double distance) 
	{
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public static int convertAngle(double radius, double width, double angle)
	{
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	//-----ADDED:
	
	public double getHeading()
	{
        return (leftMotor.getTachoCount() * WHEEL_RADIUS -
                rightMotor.getTachoCount() * WHEEL_RADIUS) / WHEEL_BASE;
	}
	
}
