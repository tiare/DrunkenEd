package system;

import control.Config;
import figure.DrunkenSkeleton;
import figure.animations.DrunkenAnimationSystem;
import pc.fileio.IOCommon;
import pc.tools.animator.AnimatorFrame;

public class DrunkenAnimatorMain {

	public static void main(String[] args) {
		
		IOCommon.IMAGE_PATH = "textures/";
		Config.preInitialize();
		
		AnimatorFrame animatorFrame = new AnimatorFrame("Drunken Animator");

		animatorFrame.init(900,null);
		animatorFrame.waitUntilInitialized();
		
		animatorFrame.addSkeleton(DrunkenSkeleton.class,new DrunkenAnimationSystem());
		
		animatorFrame.start();
	}
	
}
