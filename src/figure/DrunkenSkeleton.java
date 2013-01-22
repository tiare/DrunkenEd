package figure;

import graphics.skeletons.defaults.HumanSkeleton;
import graphics.skeletons.elements.Joint;

public class DrunkenSkeleton extends HumanSkeleton {

	@Override
	protected void build() {
		buildHuman(true, 0.07f,0.18f,0.9f);
		buildDefaultLayers();
		
		mContourFactor = 0.03f;
	}

	public void setAnimated(boolean animated) {
		for(Joint joint:mJoints) {
			joint.mAnimate = animated;
		}
	}
	
}
