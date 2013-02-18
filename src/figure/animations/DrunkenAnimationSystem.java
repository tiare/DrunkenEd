package figure.animations;

import figure.Player;
import graphics.skeletons.animations.Animation;
import graphics.skeletons.animations.AnimationSystem;

public class DrunkenAnimationSystem extends AnimationSystem<Player,Animation<Player>> {

	public static DrunkenAnimation STAND = new DrunkenStand();
	public static DrunkenAnimation WALK = new DrunkenWalk();
	public static DrunkenAnimation JUMP = new DrunkenJump();
	public static DrunkenAnimation AIRWALK = new DrunkenWalkJump();
	
	public DrunkenAnimationSystem() {
		super(STAND);
		mAnimations.add(STAND);
		mAnimations.add(WALK);
		mAnimations.add(JUMP);
		mAnimations.add(AIRWALK);
	}

}
