


import lejos.nxt.Motor;
import lejos.nxt.*;
import lejos.nxt.NXTRegulatedMotor;



public class Navigation extends Thread {
	//wheel radius
	double radius;;
	//robot width
	double width;
	//set speeds
	final int FORWARD_SPEED = 250;
	final int ROTATE_SPEED = 150;
	//odometer
	Odometer odometer;
	//current psition
	double currentX;
	double currentY;
	double currentTheta;
	double fakeX;
	double fakeY;
	double originalX;
	double originalY;
	double fakeTheta;
	double originalTheta=0;
	NXTRegulatedMotor leftMotor = Motor.A;
	NXTRegulatedMotor rightMotor = Motor.C;
	// is navigation boolean
	@SuppressWarnings("unused")
	private Object lock;
    TwoTrackedRobot robot;
	
	
	
	

	public Navigation(Odometer odometer, TwoTrackedRobot robot) {
		// initialise the start position
		
		this.odometer= odometer;
		width=Main.WHEEL_BASE;
		lock = new Object();
		this.robot=robot;
		fakeX=0;
		fakeY=0;
		fakeTheta=0;
		radius=Main.WHEEL_RADIUS;
	}
	

public void turnN()
{
	fakeTheta+=90;
	turnTo(fakeTheta);
}
	
	
	public void travelFo(double d)
	{
		
		fakeX+=15*Math.cos(fakeTheta*Math.PI/180);
		fakeY+=15*Math.sin(fakeTheta*Math.PI/180);
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		// make the robot go the distance
		leftMotor.setAcceleration(500);
		rightMotor.setAcceleration(500);
		
		leftMotor.rotate(convertDistance(radius, d), true);
		rightMotor.rotate(convertDistance(radius, d), false);
    }

	public void travelTo(double x, double y) {
		// get the current possition
		currentX = odometer.getX();
		currentY = odometer.getY();
		// get the turning angle
		double newTheta = 0;
		// the difference between the current position and new
		double deltaX = x - currentX;
		double deltaY = y - currentY;
		// for each turn that goes parrallel to each axis, we set a bound cause
		// the difference might be greater than zero
		// no change in x position, so robot will go up or down
		if (Math.abs(deltaX) < 2) {
			// robot turns to go up
			if (y > currentY)
				newTheta = 90;
			// robot turns to go down
			else if (y < currentY)
				newTheta = 270;
			// when there is no change in the y position
		} else if (Math.abs(deltaY) < 2) {
			// robot turns to go right
			if (x > currentX)
				newTheta = 0;
			// robot turns to go left
			else if (x < currentX)
				newTheta = 180;
			//if the nxt is going in the x pos, y pos
		} else {
			newTheta = Math.atan((deltaY) / (deltaX));
			newTheta = newTheta * 180 / Math.PI;
		}
		
		// make the robot turn
		turnTo(newTheta);
		// get the distance the robot needs to travel
		double distance = Math.sqrt((deltaY) * (deltaY)
				+ (deltaX) * (deltaX));
		// make the robot go forward
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		// make the robot go the distance
		leftMotor.rotate(convertDistance(radius, distance), true);
		rightMotor.rotate(convertDistance(radius, distance), false);
		
	}

	public void turnTo(double theta) {
		
		// get the current angle
		currentTheta = odometer.getTheta();
		// get the difference in angle
		double deltaT = currentTheta - theta;
		// find the shortest distance
		while (deltaT > 180)
			deltaT -= 360;
		while (deltaT < -180)
			deltaT += 360;
		// set the speed to rotate speed
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		// rotate the robot
		leftMotor.rotate(convertAngle(radius, width, deltaT), true);
	    rightMotor.rotate(-convertAngle(radius, width, deltaT), false);
		
		//leftMotor.stop();
		//rightMotor.stop();
		robot.stop();
	}
	
	public void moveToDestination()
	{
		if (odometer.getY()<0)
		{
			travelTo(odometer.getX(),15);
		}
			travelTo(15,odometer.getY());
		    
		   	
			travelTo(15,75);                   //Actual destination of the pick up zone    
			travelTo(75,75);
		    
	}

	

	// get the distance
	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// get the angle
	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	public void setOriginalTheta()
	{
		originalTheta=90-fakeTheta;
	}
	public void setOriginalXY(double x, double y)
	{
		originalX=x;
		originalY=y;
	}
	public double getOriginalX()
	{
		return(originalX);
	}
	public double getOriginalY()
	{
		return(originalY);
	}
	public double getOriginalTheta()
	{
		originalTheta-=90;
		while (originalTheta<0) originalTheta+=360;
		while (originalTheta>=360) originalTheta-=360;
		return(originalTheta);
	}
}
