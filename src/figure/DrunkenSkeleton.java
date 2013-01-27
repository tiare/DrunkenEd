package figure;

import graphics.skeletons.constraints.AngleConstraint;
import graphics.skeletons.defaults.HumanSkeleton;
import graphics.skeletons.elements.Bone;
import graphics.skeletons.elements.Joint;
import graphics.translator.TextureFilter;
import graphics.translator.TextureHolder;

public class DrunkenSkeleton extends HumanSkeleton {

	public static final float BUTT_OFFSET = 0.25f;
	public Joint mBottleJoint;
	public Joint mButtJoint;
	public Bone mBottleBone;
	public Bone mButtBone;
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
		
		for(Joint joint:mJoints) {
			if(joint.isSubChildOf(mHipJoint))
				joint.mPosY *= 0.9f;
		}
		
		mBottleJoint = new Joint("BottleNeck",mRightHandJoint,0.5f,mRightHandJoint.mPosY,0.4f,this);
		mBottleBone = new Bone(mGraphics,"Bottle",mBottleJoint,mRightHandJoint);

		for(int i=0;i<3;i++) {
			for(int j=0;j<DRINK_ANIMS;j++)
				mBottleBone.putTextureCoords(0.5f+0.5f*j/DRINK_ANIMS, 0.25f+i/4f, 0.5f+0.5f*(j+1)/DRINK_ANIMS, 0.25f+(i+1)/4f);
		}
		mBottleBone.setWidth(0.22f);
		mBottleBone.mCelShading = false;
		mBottleBone.mVisible = true;
		mBottleBone.setShift(0.07f,0.05f,0.07f,-0.4f);
		super.addJoint(mBottleJoint);
		
//		mButtJoint = new JointNormalConstraint("Butt",mHipJoint,mBodyBone,mHipJoint.mPosX,mHipJoint.mPosY-BUTT_OFFSET,0.4f,this);
//		mButtBone = new Bone(mGraphics,"ButtBone",mHipJoint,mButtJoint);
//		mButtBone.putTextureCoords(1/8f,7/8f,2/8f,1);
//		mButtBone.setWidth(0.26f);
//		mButtBone.setShift(0,0.2f,0,0);
//		super.addJoint(mButtJoint);
		
		super.addBone(mRightLowerArmBone,1);
		super.addBone(mRightUpperArmBone,1);
		super.addBone(mBottleBone,1);
		//super.addBone(mButtBone,2);
		super.addBone(mRightFootBone,2);
		super.addBone(mRightLowerLegBone,2);
		super.addBone(mRightUpperLegBone,2);
		
		super.addBone(mBodyBone,2);
		super.addBone(mHeadBone,3);
		
		super.addBone(mLeftFootBone,4);
		super.addBone(mLeftUpperLegBone,4);
		super.addBone(mLeftLowerLegBone,4);

		super.addBone(mLeftUpperArmBone,5);
		super.addBone(mLeftLowerArmBone,5);
		
		super.getBoneConstraint(mHeadBone, AngleConstraint.class).mSpanAngle *= 0.55f;

		recalculateConstraints();
		
		float shift;
		mHeadBone.mContourX1 += 0.2f;
		mHeadBone.mContourX2 += 0.6f;
		//mHeadBone.mContourX3 += 0.9f;
		mHeadBone.mContourX4 += 0.6f;
		mHeadBone.mContourY3 += 0.3f;
		mHeadBone.mContourX4 -= 0.3f;
		shift = 0.3f;
		mLeftFootBone.mContourY1 += shift;
		mLeftFootBone.mContourY2 += shift;
		mRightFootBone.mContourY1 += shift;
		mRightFootBone.mContourY2 += shift;
		mLeftFootBone.mContourX1 += shift;
		mLeftFootBone.mContourX4 += shift;
		mRightFootBone.mContourX1 += shift;
		mRightFootBone.mContourX3 += shift;
		mRightFootBone.mContourX4 += shift;
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
		mBodyBone.mShiftY2 -= 0.075f;
		mHeadBone.setShiftX(0.05f);
		mLeftFootBone.setWidth(0.1f);
		mRightFootBone.setWidth(mLeftFootBone.mWidth1);
		
		mLeftLegJoint.mRelativeY-=0.005f;
		mRightLegJoint.mRelativeY+=0.03f;
		mLeftLegJoint.mRelativeX+=0.16f;
		mRightLegJoint.mRelativeX+=0.09f;
		mLeftUpperLegBone.mShiftY1-=0.02f;
		
		mContourFactor = 0.04f;
		mFloorFriction = 0.85f;
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
