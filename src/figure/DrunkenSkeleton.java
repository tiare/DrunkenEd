package figure;

import graphics.skeletons.defaults.HumanSkeleton;

public class DrunkenSkeleton extends HumanSkeleton {

	@Override
	protected void build() {
		build(true, 0.07f,0.18f,0.9f);
		//buildDefaultLayers();
		
		
		super.addBone(mRightLowerArmBone,1);
		super.addBone(mRightUpperArmBone,1);
		super.addBone(mRightUpperLegBone,2);
		super.addBone(mRightLowerLegBone,2);
		super.addBone(mBodyBone,2);
		super.addBone(mLeftUpperLegBone,3);
		super.addBone(mLeftLowerLegBone,3);
		super.addBone(mHeadBone,4);
		super.addBone(mLeftLowerArmBone,6);
		super.addBone(mLeftUpperArmBone,6);
		
		super.addBone(mRightFootBone,2);
		super.addBone(mLeftFootBone,3);
		
		contourFactor = 0.03f;
	}
	
}
