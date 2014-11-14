
import lejos.nxt.*;

/**
 * @author Team11
 * A simple Claw test integrated in LAb4
 * Once robot localizes, it moves forward till block is seen.
 * Then block is taken and robot continues its path in a straight line
 * 
 */
public class ClawAction extends Thread{
	
	public final int ClawROTATION_SPEED=700;
	public final int FORWARD_SPEED= 250;
	public final int SCANNING_SPEED=300;
	public boolean isGrabbed;
	public boolean isnearPickUp;
	private NXTRegulatedMotor clawMotor;
	NXTRegulatedMotor leftMotor = Motor.A;
	NXTRegulatedMotor rightMotor = Motor.C;
    UltrasonicSensor usSensor;
    private final int threshold=20;   //CHANGE DEPENDNG CLAW
	private double radius=2.15;     //CHANGE DEPENDING BASE
	private final int pickUpDistance=2;
	private double leftToObject=threshold-2;
	private final double clawMaxAngle=270;   //***TEST getTacho when block 
	double currentClawAngle;
	private double currX;
	private double currY;
	//private Map pickupMap;
	//boolean isnearPickUp;
	
	public ClawAction(NXTRegulatedMotor clawMotor, UltrasonicSensor usSensor){
		
		this.clawMotor=clawMotor;
		this.usSensor=usSensor;
	    this.radius=radius;
	    isGrabbed=true;
	    
	}
	
	/* Block of code that will tell if robot is at position next to the pick up area. Will return a true boolean if it is, if not, continue to navigate
	 * passed parameters: current x and current y. Assume dropX's and dropY's are in an array
	 * 
	 *public boolean isNearPickup(double x, double y){
	 *
	 * isnearPickUp=false;
	 * 
	 * x=currX;
	 * y=currY;
	 * 
	 * for(int=0; i<dropX.length; i++)
	 * {
	 *  
	 *   for(int i=0; j<dropY.length; i++)
	 *   {
	 *      if(pickX[i]-currX>-1 || pickX[i]-currX<1 && pickY[j]-currY>-1 ||pickY[j]-currY<1){
	 *    
	 *      isnearPickUp=true;
	 *      
	 *      }
	 *        else
	 *        {
	 *        isnearPickUp=false;
	 *        }
	 *   }
	 *   
	 *  }
	 *  
	 *  return isnearPickUp;
	 *  }
	 */
	
	
	//This block of code proceeds only if isnearPickUp is true:
	
	/**
	 * Method that will make the robot Advance and Lift (grab) the object if it is in the vicinity of pick up area. Validity
	 * of the robot being near pick up area is going to be determined by isNearPickup
	 * @param clawMotor
	 * @param usSensor
	 */
	public void advanceAndLift(NXTRegulatedMotor clawMotor, UltrasonicSensor usSensor)
	{
		
		clawMotor.setSpeed(ClawROTATION_SPEED);
		clawMotor.rotate(360);
		clawMotor.resetTachoCount();
		clawMotor.stop(true);
		
		//shall implement scanning for object code code. Turn left 45deg, turn right 45deg, scanning. Lock at the nearest block
	    //once it scanned, it will lock down closest distance scanned, and robot will turn to that angle (at which is the closest block)
		
	    while(usSensor.getDistance()>threshold)                                     //if distance at which robot locked is greater than trash, advance. 
		{  
            leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
			leftMotor.forward();
			rightMotor.forward();
	    }
		
		rightMotor.stop(true);                                                     //stop motors when while loop exits
		leftMotor.stop(true);
		
		
		if(usSensor.getDistance()<threshold || usSensor.getDistance()==threshold)  //if within threshold slam the claw down
		{   
			clawMotor.rotate(-360);//open claw, travel remaining distance till actual block
			clawMotor.resetTachoCount();
			clawMotor.stop();
			rightMotor.setSpeed(FORWARD_SPEED);
			leftMotor.setSpeed(FORWARD_SPEED);
			leftMotor.rotate(convertDistance(radius, leftToObject), true);        //travel till object (left distance)
			rightMotor.rotate(convertDistance(radius, leftToObject), false);
		}   
			
	        
		                        //resets tacho for claw after every motion. Used to get the angle at which block will be picked up
		    clawMotor.rotate(360);                             //claw closes, verification algorithm starts
			clawMotor.stop(true);
			
			                   
		    double currentClawAngle=clawMotor.getTachoCount();  //With this angle, it will be concluded if claw grabbed the brick, or not
		    
		    
		    
		      if(currentClawAngle>clawMaxAngle)
		      {                                                   //if claw motor rotated more than max angle of grabbing, then do correction
		    	  while(isGrabbed==false)
		    	  {                                               //do correction while isGrabbed is false
		              isGrabbedCorrection(currentClawAngle);
		           }
	           }
		    
			
		    //At this stage, the robot shall have the pick up area and proceed travelling to drop off area aka calling (travelTo(dropX, dropY) from same method from which clawaction is called
	}
	
	/**
	 * 
	 * @param currentClawAngle
	 * @return isGrabbed boolean
	 * Method that corrects grabbing if objects fails to be grabbed at first attempt. For now, it is a linear back and forth correction
	 * however, can move robot to left and right, then align the robot and try grabbing again. (future dev).
	 * 
	 */
	public boolean isGrabbedCorrection (double currentClawAngle)
	{
		rightMotor.setSpeed(FORWARD_SPEED);                  //backup slightly (can chose distance after testing) ex here: 3
		leftMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(radius, -3), true);
		rightMotor.rotate(convertDistance(radius, -3), false);
		clawMotor.rotate(-360);   //descend claw again
		clawMotor.resetTachoCount();
		
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.setSpeed(FORWARD_SPEED);
		leftMotor.rotate(convertDistance(radius, 3), true);
		rightMotor.rotate(convertDistance(radius, 3), false); // move forward again, same as backed up distance
		clawMotor.rotate(360);
		
		         //*****NOTE: need to reset tacho of claw motor****
		
		if(clawMotor.getTachoCount()>clawMaxAngle){//after correction, if angle of grabbing is smaller or equal, that means that object is grabbed!
			isGrabbed=false;
		}
		else{
			isGrabbed=true;
		}
		
		return isGrabbed;
		
	}
	
	
   /**
    * 
    * Method for distance convertion for motor rotation
    * @param radius
    * @param distance
    * @return distance for motors to travel
    * 
    */
	
	private static int convertDistance(double radius, double distance)
	{
		return (int) ((180.0 * distance) / (Math.PI * radius)); 
	}
	
	
	
	

}
