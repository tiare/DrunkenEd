package figure.animations;

import graphics.skeletons.animations.KeyFrame;
import graphics.skeletons.pose.AnglePose;

public class DrunkenStand extends DrunkenAnimation{

	public static KeyFrame frame0 = new KeyFrame(new AnglePose(new float[]{0.016f,1.15f,-0.599f,2.814f,0.402f,1.182f,-0.892f,0.272f,-1.663f,0.601f,-0.466f,1.818f,1.592f,1.603f,1.204f}),1);
	public static KeyFrame frame1 = new KeyFrame(new AnglePose(new float[]{0.008f,1.091f,-0.569f,2.837f,0.485f,1.365f,-1.042f,0.194f,-1.675f,0.605f,-0.441f,1.873f,1.595f,1.603f,1.225f}),1);
	public static KeyFrame frame2 = new KeyFrame(new AnglePose(new float[]{0.016f,1.15f,-0.599f,2.814f,0.402f,1.182f,-0.892f,0.272f,-1.663f,0.601f,-0.466f,1.818f,1.592f,1.603f,1.204f}),1);
	public static KeyFrame frame3 = new KeyFrame(new AnglePose(new float[]{0.008f,1.091f,-0.569f,2.837f,0.485f,1.365f,-1.042f,0.194f,-1.675f,0.605f,-0.441f,1.873f,1.595f,1.603f,1.225f}),1);

	
	public DrunkenStand() {
		setFrames(new KeyFrame[]{frame0,frame1,frame2,frame3});
		mFramesPerSecond = 4f;
		mInterpolate = true;
	}

	
}
