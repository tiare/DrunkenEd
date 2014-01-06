package figure.animations;

import yang.graphics.skeletons.animations.KeyFrame;
import yang.graphics.skeletons.animations.WrapMode;
import yang.graphics.skeletons.pose.AnglePosture2D;


public class DrunkenJump extends DrunkenAnimation{

	public static KeyFrame frame0 = new KeyFrame(new AnglePosture2D(new float[]{-0.19f,1.724f,-0.102f,3.054f,-0.122f,0.203f,-0.387f,-0.082f,2.317f,2.008f,-3.018f,2.748f,0.364f,0.867f,-0.218f}),1);
	public static KeyFrame frame1 = new KeyFrame(new AnglePosture2D(new float[]{-0.19f,1.359f,-0.104f,2.86f,0.544f,0.877f,-1.315f,-0.293f,-1.549f,0.518f,-0.354f,1.627f,0.304f,1.078f,-2.947f}),1);

	public DrunkenJump() {
		mFramesPerSecond = 0.8f;
		mInterpolate = true;
		setFrames(WrapMode.LOOP,new KeyFrame[]{frame0,frame1});
	}


}
