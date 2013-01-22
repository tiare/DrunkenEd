package figure;

import figure.animations.DrunkenAnimationSystem;
import tracking.AbstractTracking;
import control.Debug;
import control.ProgramController;
import graphics.defaults.Default2DGraphics;
import graphics.defaults.DefaultAnimationPlayer;
import graphics.skeletons.Skeleton;
import graphics.skeletons.SkeletonCarrier;
import graphics.skeletons.animations.AnimationPlayer;
import graphics.skeletons.constraints.AngleConstraint;
import graphics.skeletons.constraints.Constraint;
import graphics.skeletons.elements.Joint;
import graphics.translator.GraphicsTranslator;

public class Player implements SkeletonCarrier {

	public final static DrunkenAnimationSystem ANIMATION_SYSTEM = new DrunkenAnimationSystem();
	public final static float PI = 3.1415926535f;
	
	private float velX,velY;
	private ProgramController programController;
	private AbstractTracking tracking;
	private DrunkenSkeleton skeleton;
	private GraphicsTranslator graphics;
	private Default2DGraphics graphics2D;
	public float posX,posY;
	public boolean gameOver;
	private boolean armAnglesByTracking;
	public DefaultAnimationPlayer animationPlayer;
	
	public float steeredBending;
	public float bendingSpeed;
	public float drunkenBending;
	
	public Player() {
		skeleton = new DrunkenSkeleton();
		steeredBending = 0;
		drunkenBending = 0;
		gameOver = false;
	}
	
	public Player init(ProgramController programController) {
		this.programController = programController;
		this.graphics = programController.mGraphics;
		this.graphics2D = programController.mGraphics2D;
		tracking = programController.tracking;
		skeleton.init(this);
		setArmAnglesByTracking(true);
		animationPlayer = new DefaultAnimationPlayer(skeleton,null);
		start();
		return this;
	}
	
	private void refreshArms() {
		if(!armAnglesByTracking)
			return;
		skeleton.mLeftShoulderJoint.setPosByConstraint();
		skeleton.mRightShoulderJoint.setPosByConstraint();
		skeleton.mLeftUpperArmBone.setAngle(tracking.leftUpperArmAngle);
		skeleton.mLeftLowerArmBone.setAngle(tracking.leftLowerArmAngle);
		skeleton.mRightUpperArmBone.setAngle(tracking.rightUpperArmAngle);
		skeleton.mRightLowerArmBone.setAngle(tracking.rightLowerArmAngle);
	}
	
	public void setArmAnglesByTracking(boolean enabled) {
		armAnglesByTracking = enabled;
		skeleton.mLeftElbowJoint.mFixed = enabled;
		skeleton.mLeftHandJoint.mFixed = enabled;
		skeleton.mRightElbowJoint.mFixed = enabled;
		skeleton.mRightHandJoint.mFixed = enabled;
		tracking.trackArms = enabled;
		refreshArms();
	}
	
	private void setAnimated(Joint joint,boolean animated) {
		joint.mFixed = animated;
		joint.mAnimate = true;
	}
	
	public void start() {
		gameOver = false;
		skeleton.mHipJoint.mFixed = true;
		skeleton.mConstantForceY = -0.2f;
		
		skeleton.setAnimated(false);
		setAnimated(skeleton.mLeftKneeJoint,true);
		setAnimated(skeleton.mLeftFootJoint,true);
		setAnimated(skeleton.mLeftToesJoint,true);
		setAnimated(skeleton.mRightKneeJoint,true);
		setAnimated(skeleton.mRightFootJoint,true);
		setAnimated(skeleton.mRightToesJoint,true);
		setAnimated(skeleton.mBreastJoint,true);
		setAnimated(skeleton.mHipJoint,true);
		skeleton.mHeadJoint.mFixed = true;
		
		animationPlayer.setAnimation(DrunkenAnimationSystem.WALK);
	}
	
	public void setSpeedX(float speed) {
		this.velX = speed;
	}
	
	public float getSpeed() {
		return velX;
	}
	
	public void draw() {
		skeleton.refreshVisualVars();
		skeleton.draw();
		if(Debug.DRAW_SKELETON)
			skeleton.drawEditing(null);
	}

	@Override
	public Default2DGraphics getGraphics() {
		return graphics2D;
	}

	@Override
	public int getLookDirection() {
		return 1;
	}

	@Override
	public float getScale() {
		return 1;
	}

	@Override
	public Skeleton getSkeleton() {
		return skeleton;
	}

	@Override
	public float getWorldX() {
		return posX;
	}

	@Override
	public float getWorldY() {
		return posY;
	}
	
	public void setWorldPosition(float x,float y) {
		posX = x;
		posY = y;
	}

	@Override
	public void setSkeleton(Skeleton skeleton) {
		
	}

	public void step(float deltaTime) {
		if(!gameOver) {
			posX += velX*deltaTime;
			posY += velY*deltaTime;
			
			animationPlayer.proceed(velX*deltaTime);
			
			skeleton.mBreastJoint.setPosByAngle(skeleton.mHipJoint, skeleton.mBodyBone, -(drunkenBending+steeredBending)+PI);
			skeleton.mHeadJoint.setPosByAngle(PI*0.9f);
			
			refreshArms();
		}
		
		skeleton.mAccuracy = 16;
		if(gameOver)
			skeleton.applyConstraints(deltaTime);
	}

	public void fallDown() {
		gameOver = true;
		setArmAnglesByTracking(false);
		skeleton.mConstantForceY = -0.2f;
		skeleton.mLowerLimit = 0;
		skeleton.mLimitForceInwards = 5.4f;
		skeleton.mLimitForceOutwards = 2.0f;
		
		for(Joint joint:skeleton.mJoints) {
			joint.mFixed = false;
			joint.mFriction = 0.99f;
			joint.vX = velX;
		}
		
		for(Constraint constraint:skeleton.mConstraints) {
			if(constraint instanceof AngleConstraint) {
				((AngleConstraint)constraint).mStrength = 20;
			}
		}
	}
	
	public void setFlailingArms(boolean flail){
		
	}
	
	public void setSwingingArms(boolean swing){
		
	}

	@Override
	public void drawCollision() {
		
	}

	@Override
	public AnimationPlayer<?> getAnimationPlayer() {
		return animationPlayer;
	}
	
}
