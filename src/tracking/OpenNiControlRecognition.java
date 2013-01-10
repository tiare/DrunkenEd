package tracking;

import processing.core.PApplet;
import processing.core.PVector;
import SimpleOpenNI.SimpleOpenNI;

public class OpenNiControlRecognition extends SimpleOpenNI {

	public OpenNiControlRecognition(PApplet arg0) {
		super(arg0);
	}

	public OpenNiControlRecognition(PApplet arg0, String arg1) {
		super(arg0, arg1);
	}

	public OpenNiControlRecognition(int arg0, PApplet arg1) {
		super(arg0, arg1);
	}

	public OpenNiControlRecognition(PApplet arg0, int arg1) {
		super(arg0, arg1);
	}

	public OpenNiControlRecognition(PApplet arg0, String arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public float[] getRightHandPosition(int userId){
		PVector hand3d = null, hand2d = null;
		getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_LEFT_HAND, hand3d);
		convertRealWorldToProjective(hand3d, hand2d);
		
		// correct x pos because of the mirrored image
		return new float[]{hand3d.x,hand3d.y};
	}
	
	/*public Point2DDepth getLeftHandPosition(int userId){
		PVector hand3d = new PVector();
		PVector hand2d = new PVector();
		
		getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, hand3d);
		convertRealWorldToProjective(hand3d, hand2d);
		
		// correct x pos because of the mirrored image
		return new Point2DDepth(depthWidth() - hand2d.x, hand2d.y,hand3d.z);
	}*/
	
}
