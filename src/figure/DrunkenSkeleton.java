package figure;

import graphics.skeletons.defaults.HumanSkeleton;
import graphics.skeletons.elements.Bone;
import graphics.skeletons.elements.Joint;
import graphics.skeletons.elements.JointNormalConstraint;
import graphics.translator.TextureFilter;
import graphics.translator.TextureHolder;

public class DrunkenSkeleton extends HumanSkeleton {

	public Joint mBottleJoint;
	public Bone mBottleBone;
	//private static final float[] Y_BOTTLES = {0,2f/8,5f/8,1};
	public static final int DRINK_ANIMS = 4;
	public int mDrinkId;
	public int mDrinkEmpty;
	public boolean mBottleVisible;
	
	public DrunkenSkeleton() {
		mBottleVisible = true;
		mTextureHolder = new TextureHolder("skeleton_ed",TextureFilter.NEAREST);
		mContourTextureHolder = new TextureHolder("skeleton_ed",TextureFilter.LINEAR_MIP_LINEAR);
	}
	
	@Override
	protected void build() {
		buildHuman(true, 0.07f,0.16f,0.9f);
		

		float shift;
		mHeadBone.mContourX1 += 0.2f;
		mHeadBone.mContourX2 += 0.8f;
		//mHeadBone.mContourX3 += 0.9f;
		mHeadBone.mContourX4 += 0.9f;
		mHeadBone.mContourY3 += 0.3f;
		mHeadBone.mContourX4 -= 0.3f;
		shift = -0.05f;
		mHeadBone.mShiftY1 += shift;
		mHeadBone.mShiftY2 += shift;
		mHeadBone.mShiftX1 += 0.01f;
		mHeadBone.mShiftX2 += 0.01f;
		
		shift = 0.15f;
		mRightShoulderJoint.mRelativeX += shift;
		mLeftShoulderJoint.mRelativeX += shift+0.025f;
		mLeftShoulderJoint.mRelativeY -= 0.04f;
		mLeftUpperArmBone.mShiftY1+=0.05f;
		mLeftElbowJoint.mPosY += shift;
		mRightElbowJoint.mPosY += shift;
		mLeftHandJoint.mPosY += shift;
		mRightHandJoint.mPosY += shift;
		mBodyBone.setWidth(0.35f);
		mBodyBone.setShiftX(0.05f);
		mHeadBone.setShiftX(0.05f);
		mLeftFootBone.setWidth(0.1f);
		mRightFootBone.setWidth(mLeftFootBone.mWidth1);
		float fac = 0.6f;
		mHipJoint.mPosY *= fac;
		mLeftKneeJoint.mPosY *= fac;
		mRightKneeJoint.mPosY *= fac;
		
		
		mBottleJoint = new Joint("BottleNeck",mRightHandJoint,0.5f,mRightHandJoint.mPosY,0.4f,this);
		mBottleBone = new Bone(mGraphics,"Bottle",mBottleJoint,mRightHandJoint);

		for(int i=0;i<3;i++) {
			for(int j=0;j<DRINK_ANIMS;j++)
				mBottleBone.putTextureCoords(0.5f+0.5f*j/DRINK_ANIMS, 0.25f+i/4f, 0.5f+0.5f*(j+1)/DRINK_ANIMS, 0.25f+(i+1)/4f);
		}
		mBottleBone.setWidth(0.22f);
		mBottleBone.mCelShading = false;
		mBottleBone.mVisible = true;
		mBottleBone.setShift(0.05f,0.05f,0.05f,-0.4f);
		super.addJoint(mBottleJoint);
		
		
		super.addBone(mRightLowerArmBone,1);
		super.addBone(mRightUpperArmBone,1);
		super.addBone(mBottleBone,1);
		super.addBone(mRightFootBone,2);
		super.addBone(mRightLowerLegBone,2);
		super.addBone(mRightUpperLegBone,2);
		
		super.addBone(mLeftFootBone,3);
		super.addBone(mLeftUpperLegBone,3);
		super.addBone(mLeftLowerLegBone,3);
		super.addBone(mBodyBone,3);
		super.addBone(mHeadBone,4);
		super.addBone(mLeftUpperArmBone,5);
		super.addBone(mLeftLowerArmBone,5);
		
		mContourFactor = 0.035f;
	}
	
	private void refreshBottleCoords() {
		if(!mBottleVisible || mDrinkId<0) {
			mBottleBone.mVisible = false;
		}else{
			mBottleBone.mVisible = true;
			mBottleBone.setTextureCoordinatesIndex(mDrinkId*DRINK_ANIMS+mDrinkEmpty);
			if(mDrinkId==0)
				mBottleBone.setShiftX(0.12f);
			else
				mBottleBone.setShiftX(0.05f);
		}
	}
	
	public void refreshBottle() {
		mBottleJoint.setPosByAngle(mRightHandJoint.getParentAngle()+PI/2);
		refreshBottleCoords();
	}
	
	public void setDrinkId(int drinkId) {
		mDrinkId = drinkId;
		refreshBottleCoords();
	}
	

	public void setAnimated(boolean animated) {
		for(Joint joint:mJoints) {
			joint.mAnimate = animated;
		}
	}
	
}
