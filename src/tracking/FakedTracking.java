package tracking;

import javax.vecmath.Point2d;

import com.sun.corba.se.impl.ior.ByteBuffer;

import ninja.game.model.Keys;

import control.ProgramController;

public class FakedTracking extends AbstractTracking{

	public float bending;
	private boolean drink = false;
	
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
		
		TrackingListener listener = programController.getCurrentState();
		if(listener!=null) {
			//listener.onDrink();
			listener.onBend(bending);
		}
		
	}

	@Override
	public void restart() {
		bending = 0;
	}
	
	@Override
	public java.nio.ByteBuffer getColorImageByteBuffer() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
