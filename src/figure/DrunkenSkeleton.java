package figure;

import graphics.skeletons.defaults.HumanSkeleton;
import graphics.skeletons.elements.Joint;
import graphics.translator.TextureFilter;
import graphics.translator.TextureHolder;

public class DrunkenSkeleton extends HumanSkeleton {

	public DrunkenSkeleton() {
		mTextureHolder = new TextureHolder("skeleton_ed",TextureFilter.NEAREST);
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
		
		super.addBone(mRightLowerArmBone,1);
		super.addBone(mRightUpperArmBone,1);
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

	public void setAnimated(boolean animated) {
		for(Joint joint:mJoints) {
			joint.mAnimate = animated;
		}
	}
	
}
