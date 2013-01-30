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
	public final static boolean CONTROL_HEAD = false;
	
	private float velX,velY;
	private ProgramController programController;
	private AbstractTracking tracking;
	public DrunkenSkeleton skeleton;
	private GraphicsTranslator graphics;
	private Default2DGraphics graphics2D;
	public float posX,posY;
	public boolean gameOver;
	private boolean armAnglesByTracking;
	public DefaultAnimationPlayer animationPlayer;
	public float lifeTime;
	private boolean mFlail;
	private boolean mSwing;
	private float mSwingTime;
	public boolean inGame;
	public boolean fellDown;
	public float fellTime;
	public boolean moved;
	
	private int drinkState;
	public float headAngle;
	
	public float steeredBending;
	public float bendingSpeed;
	public float drunkenBending;
	
	public Player(boolean inGame) {
		this.inGame = inGame;
		skeleton = new DrunkenSkeleton();
		steeredBending = 0;
		drunkenBending = 0;
		fellDown = false;
		gameOver = false;
		moved = false;
		drinkState = 0;
	}
	
	public Player init(ProgramController programController) {
		this.programController = programController;
		this.graphics = programController.mGraphics;
		this.graphics2D = programController.mGraphics2D;
		tracking = programController.tracking;
		skeleton.init(this);
		setArmAnglesByTracking(true);
		animationPlayer = new DefaultAnimationPlayer(skeleton,null);
		skeleton.mAccuracy = 16;
		
		mFlail = false;
		mSwing = false;
		mSwingTime = -1;
		
		fellTime = 0;
		lifeTime = 0;
		
		start();
		return this;
	}
	
	private void refreshArms() {
		
		if(drinkState<=0) {
			if(mFlail) {
				skeleton.mLeftShoulderJoint.setPosByConstraint();
				skeleton.mRightShoulderJoint.setPosByConstraint();
				float angle = -lifeTime*20;
				if(velX<0)
					angle *= -1;
				skeleton.mLeftUpperArmBone.setAngle(angle+PI);
				skeleton.mLeftLowerArmBone.setAngle(angle+PI);
				skeleton.mRightUpperArmBone.setAngle(angle);
				skeleton.mRightLowerArmBone.setAngle(angle);
			}else if(mSwingTime>0) {
				skeleton.mLeftShoulderJoint.setPosByConstraint();
				skeleton.mRightShoulderJoint.setPosByConstraint();
				boolean up = (int)(lifeTime*1000)/120%2==0;
				float fac = 0.3f;
				float angle = (up?PI/2+fac:PI/2-fac);
				float offset = -(steeredBending+drunkenBending);
				if(offset<0)
					offset *= 0.7f;
				skeleton.mLeftUpperArmBone.setAngle(-angle+offset);
				skeleton.mLeftLowerArmBone.setAngle(-angle+offset);
				skeleton.mRightUpperArmBone.setAngle(angle+offset);
				skeleton.mRightLowerArmBone.setAngle(angle+offset);
			}else{
				skeleton.mLeftShoulderJoint.setPosByConstraint();
				skeleton.mRightShoulderJoint.setPosByConstraint();
				if(armAnglesByTracking) {
					skeleton.mLeftUpperArmBone.setAngle(tracking.leftUpperArmAngle);
					skeleton.mLeftLowerArmBone.setAngle(tracking.leftLowerArmAngle);
					skeleton.mRightUpperArmBone.setAngle(tracking.rightUpperArmAngle);
					skeleton.mRightLowerArmBone.setAngle(tracking.rightLowerArmAngle);
				}else{
					skeleton.mLeftUpperArmBone.setAngle(0);
					skeleton.mLeftLowerArmBone.setAngle(0);
					skeleton.mRightUpperArmBone.setAngle(0);
					skeleton.mRightLowerArmBone.setAngle(0);
				}
					
			}
		}
		skeleton.refreshBottle();
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
		
		lifeTime = 0;
		
		animationPlayer.setAnimation(DrunkenAnimationSystem.WALK);
	}
	
	public void setSpeedX(float speed) {
		this.velX = speed;
	}
	
	public float getSpeed() {
		return velX;
	}
	
	public void step(float deltaTime) {
		lifeTime += deltaTime;
		if(fellDown)
			fellTime += deltaTime;

		if(!gameOver) {
			if(mSwing)
				mSwingTime = 0.25f;
			else if(mSwingTime>0)
				mSwingTime -= deltaTime;
			synchronized(skeleton) {
				posX += velX*deltaTime;
				posY += velY*deltaTime;

				animationPlayer.proceed(velX*deltaTime);
				
				float angleOffset = 0;
				if(!inGame && !moved) {
					angleOffset = (float)Math.sin(lifeTime*5)*0.04f+0.08f;
				}
				
				if(Math.abs(velX)>0.001f)
					moved = true;
				
				float bending = (drunkenBending+steeredBending);
				float upLimit = 0.9f*PI/2;
				if(!inGame)
					upLimit *= 0.55f;
				float downLimit = -0.9f*upLimit;
				if(bending>upLimit)
					bending = upLimit;
				if(bending<downLimit)
					bending = downLimit;
				float prevX = skeleton.mBreastJoint.mPosX;
				float prevY = skeleton.mBreastJoint.mPosY;
				skeleton.mBreastJoint.setPosByAngle(skeleton.mHipJoint, skeleton.mBodyBone, -bending+PI-angleOffset);
				float fac = 0.15f;
				skeleton.mBreastJoint.mVelX = (skeleton.mBreastJoint.mPosX-prevX)/deltaTime*fac;
				skeleton.mBreastJoint.mVelY = (skeleton.mBreastJoint.mPosY-prevY)/deltaTime*fac;
				skeleton.mRightShoulderJoint.setSpeed(skeleton.mBreastJoint);
				skeleton.mRightElbowJoint.setSpeed(skeleton.mBreastJoint);
				skeleton.mRightHandJoint.setSpeed(skeleton.mBreastJoint);
				skeleton.mLeftShoulderJoint.setSpeed(skeleton.mBreastJoint);
				skeleton.mLeftElbowJoint.setSpeed(skeleton.mBreastJoint);
				skeleton.mLeftHandJoint.setSpeed(skeleton.mBreastJoint);
				skeleton.mHeadJoint.setSpeed(skeleton.mBreastJoint);
				
				if(drinkState>0) {
					skeleton.mHeadJoint.setPosByAngle(headAngle);
				}else{
					if(inGame || !CONTROL_HEAD)
						skeleton.mHeadJoint.setPosByAngle(PI*0.9f+angleOffset);
					else
						skeleton.mHeadJoint.setPosByAngle(-tracking.headangle+bending+PI);
				}
				
//				skeleton.mButtJoint.mPosX = skeleton.mHipJoint.mPosX;
//				skeleton.mButtJoint.mPosY = skeleton.mHipJoint.mPosY-0.2f;
				
				refreshArms();
			}
		}
		
		synchronized(skeleton) {
			if(gameOver && fellTime<0.2f)
				skeleton.applyConstraints(deltaTime);
		}
	}
	
	public void draw() {
		synchronized(skeleton) {
	
			skeleton.mBottleVisible = !inGame;
			if(drinkState>0) {
				if(drinkState<2)
					skeleton.mHeadBone.setTextureCoordinatesIndex(3);
				else
					skeleton.mHeadBone.setTextureCoordinatesIndex(0);
			}else if(fellDown) {
//				if(true || (int)(fellTime*1000)/500 % 2==0)
//					skeleton.mHeadBone.setTextureCoordinatesIndex(2);
//				else
				skeleton.mHeadBone.setTextureCoordinatesIndex(2);
			}else{
				if(mFlail || mSwingTime>0) {
					skeleton.mHeadBone.setTextureCoordinatesIndex(1);
				}else
					skeleton.mHeadBone.setTextureCoordinatesIndex(0);
			}
			
			skeleton.refreshVisualVars();
			skeleton.draw();
			if(Debug.DRAW_SKELETON)
				skeleton.drawEditing(null);
			graphics2D.setDefaultProgram();
			
			
			
			//graphics2D.drawRectCentered(0, 0, 1, 2, tracking.headangle);
		}
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
	
	public void setDrinking() {
		drinkState = 1;
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
			joint.mFriction = 0.998f;
			joint.mVelX = velX;
		}
		
		for(Constraint constraint:skeleton.mConstraints) {
			if(constraint instanceof AngleConstraint) {
				((AngleConstraint)constraint).mStrength = 20;
			}
		}
	}
	
	public void setFlailingArms(boolean flail){
		mFlail = flail;
	}
	
	public void setSwingingArms(boolean swing){
		mSwing = swing;
	}

	@Override
	public void drawCollision() {
		
	}

	@Override
	public AnimationPlayer<?> getAnimationPlayer() {
		return animationPlayer;
	}

	public float getCenterX() {
		return posX+skeleton.mHipJoint.mPosX;
	}
	
	public float getCenterY() {
		return posY+0.9f;
	}
	
}
