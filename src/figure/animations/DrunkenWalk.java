package figure.animations;

import graphics.skeletons.animations.KeyFrame;
import graphics.skeletons.pose.AnglePose;

public class DrunkenWalk extends DrunkenAnimation{

	public static KeyFrame frame0 = new KeyFrame(new AnglePose(new float[]{0.041f,1.23f,-0.526f,3.069f,0.021f,1.058f,-0.797f,-0.065f,0.009f,-0.056f,0.014f,-0.052f,1.556f,1.576f,1.204f}),1);
	public static KeyFrame frame1 = new KeyFrame(new AnglePose(new float[]{0.044f,1.314f,-0.532f,3.063f,0.829f,0.44f,-0.536f,-0.709f,-0.018f,-0.01f,-0.002f,0.038f,1.135f,1.563f,1.225f}),1);
	public static KeyFrame frame2 = new KeyFrame(new AnglePose(new float[]{0.04f,1.235f,-0.461f,3.069f,0.838f,-0.219f,0.009f,-1.085f,-0.019f,-0.069f,0.042f,0.09f,1.573f,1.584f,1.204f}),1);
	public static KeyFrame frame3 = new KeyFrame(new AnglePose(new float[]{0.051f,1.311f,-0.456f,3.078f,0.42f,1.015f,-0.354f,-1.058f,-0.039f,-0.058f,0.017f,0.02f,1.58f,0.836f,1.225f}),1);

	
	public DrunkenWalk() {
		setFrames(new KeyFrame[]{frame0,frame1,frame2,frame3});
		mFramesPerSecond = 2.5f;
		mInterpolate = true;
	}

	
}
