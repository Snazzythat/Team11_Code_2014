
import lejos.nxt.*;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener
{
	public static final int LCD_REFRESH = 250;
	private Odometer odo;
	private Timer lcdTimer;
	UltrasonicSensor usSensor;

	NXTRegulatedMotor clawMotor=Motor.B;
	// arrays for displaying data
	private double [] pos;
	
	ColorSensor cs=new ColorSensor(SensorPort.S2);
	
	public LCDInfo(Odometer odo, UltrasonicSensor usSensor) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.usSensor=usSensor;
		this.cs=cs;
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
		
	}
	
	public void timedOut() { 
		odo.getPosition(pos);
		
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString("US:", 0, 3);
		LCD.drawString("Claw", 0, 4);
		LCD.drawString("Light", 0, 5);
		LCD.drawInt((int)(pos[0]), 3, 0);
		LCD.drawInt((int)(pos[1]), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
		LCD.drawInt( usSensor.getDistance(), 3, 3);
		LCD.drawInt((int)clawMotor.getTachoCount(), 3, 4);
		LCD.drawInt((int)cs.getNormalizedLightValue(), 3, 5);
	}   
}
