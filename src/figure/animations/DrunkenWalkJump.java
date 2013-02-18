package figure.animations;

import graphics.skeletons.animations.KeyFrame;
import graphics.skeletons.pose.AnglePose;

public class DrunkenWalkJump extends DrunkenAnimation{

	public static KeyFrame frame0 = new KeyFrame(new AnglePose(new float[]{0.07f,1.284f,-0.419f,2.928f,-0.081f,0.894f,-0.968f,-0.651f,-1.215f,0.353f,-0.383f,1.544f,0.269f,1.35f,-0.236f}),1);
	public static KeyFrame frame1 = new KeyFrame(new AnglePose(new float[]{0.052f,1.292f,-0.398f,2.943f,0.765f,0.169f,-1.2f,-1.619f,-1.282f,0.574f,-0.526f,1.858f,0.885f,0.559f,-0.231f}),1);
	public static KeyFrame frame2 = new KeyFrame(new AnglePose(new float[]{0.098f,1.309f,-0.488f,2.941f,1.149f,-0.063f,-0.46f,-1.77f,-1.338f,0.527f,-0.742f,1.819f,0.494f,-0.305f,-0.226f}),1);
	public static KeyFrame frame3 = new KeyFrame(new AnglePose(new float[]{0.043f,1.296f,-0.456f,2.934f,0.934f,1.116f,-0.499f,-0.909f,-1.068f,0.484f,-0.494f,1.473f,0.688f,0.484f,-0.209f}),1);

	
	public DrunkenWalkJump() {
		setFrames(new KeyFrame[]{frame0,frame1,frame2,frame3});
		mFramesPerSecond = 2.5f;
		mInterpolate = true;
	}

	
}
