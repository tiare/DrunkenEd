package figure;

import graphics.skeletons.defaults.HumanSkeleton;

public class DrunkenSkeleton extends HumanSkeleton {

	@Override
	protected void build() {
		buildHuman(true, 0.07f,0.18f,0.9f);
		buildDefaultLayers();
		
		mContourFactor = 0.03f;
	}
	
}
