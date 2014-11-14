
import lejos.nxt.*;

public class Action extends Thread {
	
	Odometer odo;
	Navigation nav;
    TwoTrackedRobot rob;
	Map map;
	UltrasonicSensor us;
	
	
	 public Action(Odometer odo, Navigation nav, TwoTrackedRobot rob, Map map, UltrasonicSensor us)
	 {
		
		 this.odo=odo;
		 this.nav=nav;
		 this.rob=rob;
		 this.map=map;
		 this.us=us;
		 
	 }
	 
	 
	 
	 public void run(){
	 
	    String path="0";
		String robot="";
		int x=0;
		int y=0;

		if (us.getDistance()<30)
			robot+="1";
		else robot+="0";
		int direction=0;
		
		
		
	//The robot will turn 360 degree to find out its possible initial positions	
		
		//the robot will turn left when it meets obstables and it will move forward when there is no obstacles in front of it.
		while (!map.ableGetPosition(path,robot))
		{
			if (us.getDistance()<30)
			{
				nav.turnN();
				direction-=1;
				if (direction==-1)
					direction=3;
				path+=direction;
				if (us.getDistance()<30)
					robot+="1";
				else robot+="0";
				
			} else
			{
				nav.travelFo(30);
				if (direction==0)
					x+=1;
				if (direction==1)
					y+=1;
				if (direction==2)
					x-=1;
				if (direction==3)
					y-=1;
				path+="4";
				if (us.getDistance()<30)
					robot+="1";
				else robot+="0";
			}
		}
		map.getPosition();// get the position by examinate the boolean array isPossible[][][]
		//System.out.println("original"+(map.originalX*30-15)+" "+((3-map.originalY)*30-15)+" "+map.originalTheta);
		map.getPath(map.originalX, map.originalY , map.getCommand(path, map.originalTheta));
		x=map.cX;
		y=map.cY;
		odo.setX(x*30-15);
		odo.setY(30*(3-y)-15);
		if (map.cT==1)
			odo.setTheta(0);
		if (map.cT==2)
			odo.setTheta(-90);
		if (map.cT==3)
			odo.setTheta(180);
		if (map.cT==0)
			odo.setTheta(90);
		//System.out.println("now"+x+" "+y+" "+map.cT);
		Sound.beep();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nav.moveToDestination();
		nav.turnTo(90);
		String orientation = null;
		if (map.originalTheta==0)
			orientation="north";
		if (map.originalTheta==1)
			orientation="east";
		if (map.originalTheta==2)
			orientation="south";
		if (map.originalTheta==3)
			orientation="west";
		System.out.println("original position: ");
		System.out.println((map.originalX*30-15)+" "+((3-map.originalY)*30-15)+" "+orientation);
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}

	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 

		
	   

