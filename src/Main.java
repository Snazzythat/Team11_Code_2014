import lejos.nxt.*;
import lejos.util.*;

/**
 * 
 * Main class for Robot
 * All classes initialized here
 * Constants must be changed here and only here (Radius, etc)
 * @author Team 11 
 *
 */

public class Main {
	
	public static final double WHEEL_BASE = 17;         //Calibrate Actual numbers for orienteering
	public static final double WHEEL_RADIUS = 1.79;     //1.79
    public static TwoTrackedRobot TTR;
	//public static Calibration calib;
	
	public static Odometer odo;
	public static Navigation nav;
	public static Map map;
	public final static int DIMENTION=4;
	
	
	public static void main(String[] args) 
	{
	
		int buttonChoice;
		do 
		{
			// clear the display
			LCD.clear();
            // ask the user whether the motors should drive in a square or float
			LCD.drawString("   Go pick up   ", 0, 0);
			LCD.drawString("      THE       ", 0, 1);
			LCD.drawString("      DOOMED    ", 0, 2);
			LCD.drawString("      BLOCK     ", 0, 3);
			LCD.drawString("                ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		}
		
		while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		if (buttonChoice == Button.ID_LEFT)
		{
		
		//getBluetooth();                                    //GENERATE BLUETOOTH RECEPTION 
			
		//Class, sensor initialization
		ColorSensor cs = new ColorSensor(SensorPort.S2);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		UltrasonicPoller usPoller = new UltrasonicPoller(us, 50);
		
		odo = new Odometer();
		TTR = new TwoTrackedRobot(odo);
		nav=new Navigation (odo, TTR);
		map=new Map(DIMENTION);               
		
		
		odo.start();
		
		LCDInfo lcd = new LCDInfo(odo, us);

		
		Action act= new Action(odo, nav, TTR, map, us); //action done
		act.run();
		
   //------------------------------------------------------------------

		//calib.Calibrate();
	/*	usl = new USLocalizer(odo, TTR, usPoller);
		usl.doLocalization();
		
		TTR.turnToSp(45, 300); //GRID turn
		TTR.goForward(24, false);
		
		LightLocalizer lsl = new LightLocalizer(odo, TTR, cs);
		lsl.doLocalization();
		
		TTR.travel(0, 0, false);
		TTR.turnTo(-90);
		*/
		
	//------------------------------------------------------------------
	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
  
}
}
