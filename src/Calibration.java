import lejos.nxt.*;

public class Calibration extends Thread{
	
	NXTRegulatedMotor leftMotor = Motor.A;
	NXTRegulatedMotor rightMotor = Motor.C;
	TwoTrackedRobot TTR;
	Odometer odo;
	
	public void Calibrate(){
		TTR=new TwoTrackedRobot(odo);
		TTR.turnToSp(180, 200);
		TTR.goForward(30);
	}

}
