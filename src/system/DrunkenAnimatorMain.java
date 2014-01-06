package system;

import figure.DrunkenSkeleton;
import figure.animations.DrunkenAnimationSystem;
import yang.pc.tools.animator.AnimatorFrame;
import yang.physics.massaggregation.constraints.DistanceConstraint;
import control.Config;

public class DrunkenAnimatorMain {

	public static void main(String[] args) {

		Config.preInitialize();

		AnimatorFrame animatorFrame = new AnimatorFrame("Drunken Animator");

		animatorFrame.init(900,null);
		animatorFrame.waitUntilInitialized();

		DrunkenSkeleton drunkenSkeleton = new DrunkenSkeleton();
		drunkenSkeleton.init(animatorFrame.getAnimatorSurface().mGraphics2D);
		drunkenSkeleton.mBottleBone.mVisible = false;
		drunkenSkeleton.mBottleJoint.mEnabled = false;
		drunkenSkeleton.getBoneConstraint(drunkenSkeleton.mBottleBone,DistanceConstraint.class).mEnabled = false;
		drunkenSkeleton.mBreastJoint.mFixed = false;
		animatorFrame.addSkeleton(drunkenSkeleton,new DrunkenAnimationSystem());

		animatorFrame.start();
	}

}
