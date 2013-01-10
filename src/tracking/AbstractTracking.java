package tracking;

import java.util.HashMap;

import javax.vecmath.Point2d;

import control.ProgramController;

public abstract class AbstractTracking {


	public ProgramController programController;
	
	private HashMap<Integer, Boolean> pressedKeys;
	
	public AbstractTracking(ProgramController programController) {
		this.programController = programController;
		
		pressedKeys = new HashMap<Integer, Boolean>();
	}

	public abstract Point2d getHeadPos();

	public abstract void init();
	public abstract float getTorsoBending();
	
	public void step(float deltaTime) {
		TrackingListener listener = programController.getCurrentState();
		if(listener!=null) {
			listener.onDrink();
		}
	}
	
	
	public void pointerDown(float x,float y,int pId) {
		
	}
	
	public void pointerDragged(float x,float y,int pId) {
		
	}
	
	public void pointerUp(float x,float y,int pId) {
		
	}
	
	public void keyDown(int key) {
		pressedKeys.put(key, true);
	}
	
	public void keyUp(int key) {
		pressedKeys.put(key, false);
	}
	
	public boolean keyIsPressed(int key){
		if( pressedKeys.containsKey(key)){
			return pressedKeys.get(key);
		}
		
		return false;
	}
	
}
