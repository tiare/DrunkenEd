package tracking;

import javax.vecmath.Point2d;

import org.OpenNI.Point3D;

import yang.events.Keys;
import control.ProgramController;

public class FakedTracking extends AbstractTracking{

	public float bending;
	private boolean drink = false;
	public float headangle=0;
	public float gpareaz=0;
	public float gpareax=0;
	public float drinking=0;
	
	public FakedTracking(ProgramController programController) {
		super(programController);
		bending = 0.0f;
	}
	
	public Point2d getHeadPos() {
		return new Point2d(0,0.7f);
	}

	public void init() {
		
	}
	
	public float getTorsoBending(){
		return bending;
	}
	public void step(float deltaTime) {
		
		if( keyIsPressed(Keys.LEFT) ){
			bending += -(float)Math.PI /72.0f;
		} else if( keyIsPressed(Keys.RIGHT) ){
			bending += (float)Math.PI /72.0f;
		}
		
		if(programController.getCurrentState()!=null) {
			programController.getCurrentState().onBend(bending);
		}
		
	}
	
	public void keyDown(int key) {
		super.keyDown(key);
		TrackingListener listener = programController.getCurrentState();
		if(listener==null)
			return;
		if(key=='d')
			listener.onDrink();
		if(key==Keys.UP)
			listener.onJump(4.5f);
	}

	@Override
	public void restart() {
		bending = 0;
	}
	
	@Override
	public java.nio.ByteBuffer getColorImageByteBuffer() {
		return null;
	}

	@Override
	public Point3D[] getSkeletonPoints() {
		return null;
	}
	
}
