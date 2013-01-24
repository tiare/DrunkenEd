package figure;

import graphics.skeletons.Skeleton;
import graphics.skeletons.defaults.HumanSkeleton;
import graphics.skeletons.elements.Joint;
import graphics.skeletons.elements.JointNormalConstraint;
import graphics.translator.TextureHolder;

public class DrunkenSkeleton extends HumanSkeleton {

	public DrunkenSkeleton() {
		mTextureHolder = new TextureHolder("skeleton_ed",Skeleton.DEFAULTFILTER);
	}
	
	@Override
	protected void build() {
		buildHuman(true, 0.07f,0.16f,0.9f);
		
		float shift = 0.15f;
		mHeadBone.mContourX1 += 0.2f;
		mHeadBone.mContourX2 += 0.1f;
		mHeadBone.mContourX3 += 0.9f;
		mHeadBone.mContourX4 += 0.9f;
		mRightShoulderJoint.mRelativeX += shift;
		mLeftShoulderJoint.mRelativeX += shift;
		mLeftShoulderJoint.mRelativeY -= 0.02f;
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
		super.addBone(mRightUpperLegBone,2);
		super.addBone(mRightLowerLegBone,2);
		super.addBone(mLeftFootBone,3);
		super.addBone(mLeftUpperLegBone,3);
		super.addBone(mLeftLowerLegBone,3);
		super.addBone(mBodyBone,3);
		super.addBone(mHeadBone,3);
		super.addBone(mLeftUpperArmBone,4);
		super.addBone(mLeftLowerArmBone,4);
		
		mContourFactor = 0.03f;
	}

	public void setAnimated(boolean animated) {
		for(Joint joint:mJoints) {
			joint.mAnimate = animated;
		}
	}
	
}
