package tracking;

import javax.vecmath.Point2d;

import control.ProgramController;

public class Tracking {

	public ProgramController programController;
	
	public Tracking(ProgramController programController) {
		this.programController = programController;
	}
	
	public Point2d getHeadPos() {
		return new Point2d(0,0.7f);
	}

	public void init() {
		
	}
	
	public void step(float deltaTime) {
		TrackingListener listener = programController.getCurrentState();
		if(listener!=null) {
			listener.onDrink();
		}
	}
	
}
