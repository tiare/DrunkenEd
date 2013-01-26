package tracking;

import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.vecmath.Point2d;

import org.OpenNI.Point3D;


import control.ProgramController;

public abstract class AbstractTracking {


	public ProgramController programController;
	
	private HashMap<Integer, Boolean> pressedKeys;

	public boolean trackArms;
	public float leftUpperArmAngle;
	public float leftLowerArmAngle;
	public float rightUpperArmAngle;
	public float rightLowerArmAngle;
	public float headangle;
	public float gpareaz;
	public float gpareax;
	
	public AbstractTracking(ProgramController programController) {
		this.programController = programController;
		trackArms = false;
		pressedKeys = new HashMap<Integer, Boolean>();
	}

	public abstract Point2d getHeadPos();
	public abstract ByteBuffer getColorImageByteBuffer(); 
	

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
		pressedKeys.put(key, false);	//Andreas: nicht eher remove?
	}
	
	public boolean keyIsPressed(int key){
		if( pressedKeys.containsKey(key)){
			return pressedKeys.get(key);
		}
		
		return false;
	}

	public void restart() {
		
	}
	
}
