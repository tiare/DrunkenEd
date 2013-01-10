package control.states;

import figure.DrunkenSkeleton;
import graphics.StandardTextures;


public class GameState extends WorldState {

	@Override
	public void onStep(float deltaTime) {
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX, skeleton.mHipJoint.mPosY, 2);
	}

	@Override
	public void onDraw() {
		graphics.clear(1.0f, 0.3f, 0.3f);
		graphics2D.setWhite();
		player.draw();
		
		graphics.bindTexture(StandardTextures.CUBE);
		graphics2D.drawRectCentered(0,0.15f, 1,0.9f, player.getSpeed() < 0 ? stateTimer : -stateTimer);
		
	}
	
	@Override
	public void keyDown(int key){
		
	}

	@Override
	public void keyUp(int key){
		
	}
	
}
