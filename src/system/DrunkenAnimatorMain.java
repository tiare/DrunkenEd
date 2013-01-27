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
		
		DrunkenSkeleton drunkenSkeleton = new DrunkenSkeleton();
		drunkenSkeleton.init(animatorFrame.getAnimatorSurface().mGraphics2D);
		drunkenSkeleton.mBottleBone.mVisible = false;
		drunkenSkeleton.mBottleJoint.mEnabled = false;
		drunkenSkeleton.getBoneConstraint(drunkenSkeleton.mBottleBone).mEnabled = false;
		drunkenSkeleton.mBreastJoint.mFixed = false;
		animatorFrame.addSkeleton(drunkenSkeleton,new DrunkenAnimationSystem());
		
		animatorFrame.start();
	}
	
}
