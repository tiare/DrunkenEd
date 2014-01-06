package figure;

import figure.animations.DrunkenAnimationSystem;
import tracking.AbstractTracking;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.DefaultAnimationPlayer;
import yang.graphics.skeletons.SkeletonCarrier;
import yang.graphics.skeletons.animations.AnimationPlayer;
import yang.physics.massaggregation.elements.Joint;
import control.Debug;
import control.ProgramController;

public class Player implements SkeletonCarrier {

	public final static DrunkenAnimationSystem ANIMATION_SYSTEM = new DrunkenAnimationSystem();
	public final static float PI = 3.1415926535f;
	public final static boolean CONTROL_HEAD = false;

	//References
	private ProgramController programController;
	private AbstractTracking tracking;
	public DrunkenSkeleton skeleton;
	private Default2DGraphics graphics2D;
	public DefaultAnimationPlayer animationPlayer;

	//Properties
	private float gravity = -9f;
	public boolean inGame;
	private boolean armAnglesByTracking;
	public boolean jumpEnabled = true;

	//State
	public float posX,posY;
	private float velX,velY;
	public boolean gameOver;
	private int drinkState;
	public boolean moved;
	public float steeredBending;
	public float bendingSpeed;
	public float drunkenBending;
	private float mSwingTime;
	public int stepCounter;
	public boolean fellDown;
	public float fellTime;
	public float lifeTime;
	private boolean mFlail;
	private boolean mSwing;


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
		this.graphics2D = programController.mGraphics2D;
		tracking = programController.tracking;
		skeleton.init(graphics2D,this);
		setArmAnglesByTracking(true);
		animationPlayer = new DefaultAnimationPlayer(skeleton,null);
		skeleton.mAccuracy = 16;

		mFlail = false;
		mSwing = false;
		mSwingTime = -1;

		fellTime = 0;
		lifeTime = 0;
		stepCounter = 0;

		start();
		return this;
	}

	private void refreshArms() {

		skeleton.mLeftShoulderJoint.setPosByConstraint();
		skeleton.mRightShoulderJoint.setPosByConstraint();
		if(drinkState<=0) {
			if(mFlail) {
				float angle = -lifeTime*20;
				if(velX<0)
					angle *= -1;
				skeleton.mLeftUpperArmBone.setAngle2D(angle+PI);
				skeleton.mLeftLowerArmBone.setAngle2D(angle+PI);
				skeleton.mRightUpperArmBone.setAngle2D(angle);
				skeleton.mRightLowerArmBone.setAngle2D(angle);
			}else if(mSwingTime>0) {
				boolean up = (int)(lifeTime*1000)/120%2==0;
				float fac = 0.3f;
				float angle = (up?PI/2+fac:PI/2-fac);
				float offset = -(steeredBending+drunkenBending);
				if(offset<0)
					offset *= 0.7f;
				skeleton.mLeftUpperArmBone.setAngle2D(-angle+offset);
				skeleton.mLeftLowerArmBone.setAngle2D(-angle+offset);
				skeleton.mRightUpperArmBone.setAngle2D(angle+offset);
				skeleton.mRightLowerArmBone.setAngle2D(angle+offset);
			}else{
				if(armAnglesByTracking) {
//					skeleton.mLeftUpperArmBone.setAngle2D(tracking.leftUpperArmAngle);
//					skeleton.mLeftLowerArmBone.setAngle2D(tracking.leftLowerArmAngle);
//					skeleton.mRightUpperArmBone.setAngle2D(tracking.rightUpperArmAngle);
//					skeleton.mRightLowerArmBone.setAngle2D(tracking.rightLowerArmAngle);
					skeleton.mLeftElbowJoint.setPosByAngle2D(tracking.leftUpperArmAngle);
					skeleton.mLeftHandJoint.setPosByAngle2D(tracking.leftLowerArmAngle);
					skeleton.mRightElbowJoint.setPosByAngle2D(tracking.rightUpperArmAngle);
					skeleton.mRightHandJoint.setPosByAngle2D(tracking.rightLowerArmAngle);
				}else{
					skeleton.mLeftElbowJoint.setPosByAngle2D(0);
					skeleton.mLeftHandJoint.setPosByAngle2D(0);
					skeleton.mRightElbowJoint.setPosByAngle2D(0);
					skeleton.mRightHandJoint.setPosByAngle2D(0);
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

	public boolean inAir() {
		return posY>skeleton.mLowerLimit;
	}

	public void step(float deltaTime) {
		stepCounter ++;
		final int nStep = 2;
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

				if(posY<=skeleton.mLowerLimit) {
					posY = skeleton.mLowerLimit;
					velY = 0;
				}else{
					velY += gravity*deltaTime;
				}

				if(inAir()) {
					if(animationPlayer.mCurrentAnimation!=DrunkenAnimationSystem.JUMP) {
						//animationPlayer.crossAnimation(DrunkenAnimationSystem.JUMP);
						animationPlayer.setAnimation(DrunkenAnimationSystem.JUMP);
					}
					//if(animationPlayer.mCurrentAnimationTime+deltaTime < DrunkenAnimationSystem.JUMP.mTotalDuration)
						animationPlayer.proceed(deltaTime);
				}else{
					if(animationPlayer.mCurrentAnimation!=DrunkenAnimationSystem.WALK)
						animationPlayer.crossAnimation(DrunkenAnimationSystem.WALK);
					animationPlayer.proceed(velX*deltaTime);
				}


				float angleOffset = 0;
				if(!inGame && !moved) {
					angleOffset = (float)Math.sin(lifeTime*5)*0.04f+0.08f;
				}

				if(Math.abs(velX)>0.001f)
					moved = true;

				float bending = (drunkenBending+steeredBending);
				float upLimit = 0.9f*PI/2;
				if(!inGame)
					upLimit *= 0.45f;
				float downLimit = -0.9f*upLimit;
				if(bending>upLimit)
					bending = upLimit;
				if(bending<downLimit)
					bending = downLimit;

				if(stepCounter%nStep==0) {
					float prevX = skeleton.mBreastJoint.mX;
					float prevY = skeleton.mBreastJoint.mY;
					skeleton.mBreastJoint.setPosByAngle2D(skeleton.mHipJoint, skeleton.mBodyBone, -bending+PI-angleOffset);
					float fac = programController.gameSettings.difficulty*0.2f+0.1f;
					float breastVX = (skeleton.mBreastJoint.mX-prevX)/(deltaTime*nStep)*fac;
					float breastVY = (skeleton.mBreastJoint.mY-prevY)/(deltaTime*nStep)*fac;
					for(Joint joint:skeleton.mJoints) {
						int dist = joint.childDistance(skeleton.mHipJoint);
						if(dist>=0)
							joint.setVelocity(-breastVX*1/(dist*2+1), -breastVY*2/(dist*2+1));
						else
							joint.setVelocity(breastVX, breastVY);
					}
				}else
					skeleton.mBreastJoint.setPosByAngle2D(skeleton.mHipJoint, skeleton.mBodyBone, -bending+PI-angleOffset);

				if(drinkState>0) {
					//skeleton.mHeadJoint.setPosByAngle(headAngle);
				}else{
					if(inGame || !CONTROL_HEAD)
						skeleton.mHeadJoint.setPosByAngle2D(PI*0.9f+angleOffset);
					else
						skeleton.mHeadJoint.setPosByAngle2D(-tracking.headangle+bending+PI);
				}

//				skeleton.mButtJoint.mPosX = skeleton.mHipJoint.mPosX;
//				skeleton.mButtJoint.mPosY = skeleton.mHipJoint.mPosY-0.2f;

				refreshArms();
			}
		}

		synchronized(skeleton) {
			if(gameOver && fellTime<20f)
				skeleton.physicalStep(deltaTime);
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

			if(this.armAnglesByTracking) {
				skeleton.mHeadJoint.setPosByAngle2D(PI*0.96f);
			}

			skeleton.refreshVisualData();
			skeleton.draw();
			if(Debug.DRAW_SKELETON)
				skeleton.drawEditing(null);
			graphics2D.setDefaultProgram();

			//graphics2D.drawRectCentered(0, 0, 1, 2, tracking.headangle);
		}
	}

	public Default2DGraphics getGraphics() {
		return graphics2D;
	}

	public int getLookDirection() {
		return 1;
	}

	@Override
	public float getScale() {
		return 1;
	}

	public DrunkenSkeleton getSkeleton() {
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

	public void setSkeleton(DrunkenSkeleton skeleton) {

	}

	public void setDrinking() {
		drinkState = 1;
	}

	public void fallDown() {
		gameOver = true;
		setArmAnglesByTracking(false);
		skeleton.mConstantForceY = -0.2f;
		skeleton.mLimitForceInwards = 5.4f;
		skeleton.mLimitForceOutwards = 2.0f;

		for(Joint joint:skeleton.mJoints) {
			joint.mFixed = false;
			joint.mFriction = 0.998f;
			joint.mVelX += velX;
			joint.mVelY += velY;
		}

//		for(Constraint constraint:skeleton.mConstraints) {
//			if(constraint instanceof AngleConstraint) {
//				((AngleConstraint)constraint).mStrength = 20;
//			}
//		}
	}

	public void setFlailingArms(boolean flail){
		mFlail = flail;
	}

	public void setSwingingArms(boolean swing){
		mSwing = swing;
	}


	public void drawCollision() {

	}


	public AnimationPlayer<?> getAnimationPlayer() {
		return animationPlayer;
	}

	public float getCenterX() {
		return posX+skeleton.mHipJoint.mX;
	}

	public float getCenterY() {
		return posY+0.9f;
	}

	public void jump(float velocity) {
		if(jumpEnabled && Debug.JUMP_ENABLED) {
			if(posY<=skeleton.mLowerLimit+0.001f) {
				velY = velocity;
			}
		}
	}

	public void setFloorY(float y) {
		if(posY<y)
			posY = y;
		skeleton.mLowerLimit = y;
	}

	@Override
	public float getWorldZ() {
		// TODO Auto-generated method stub
		return 0;
	}

}
