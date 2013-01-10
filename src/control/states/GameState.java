package control.states;

import control.GameSettings;
import figure.DrunkenSkeleton;
import graphics.StandardTextures;


public class GameState extends WorldState {

	private float worldZoom;
	//private float time;
	
	public GameState(){
		super();
		//time = (float)Math.PI/2.0f;
		worldZoom = 2;
	}
	
	@Override
	public void onStep(float deltaTime) {
		// calculate world rotation while considering difficulty
		//worldRotation += (float)Math.sin(stateTimer+Math.PI/2) / 100.0f; 
		
		// add bending caused by drunkenness
		player.bending += (float)Math.sin(stateTimer+Math.PI/2) / 100.0f;
		
		
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		
		if( gameSettings.difficulty == GameSettings.GAME_HARD ){
			worldZoom += (float)Math.sin(stateTimer*1.3) / 200.0f;
		}
		camera.set(skeleton.mHipJoint.mPosX + player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.bending);
		player.step(deltaTime);
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();
		player.draw();
		
		graphics.bindTexture(StandardTextures.CUBE);
		graphics2D.drawRectCentered(0,-0.45f, 2,0.9f, 0);
		//player.getSpeed() < 0 ? stateTimer : -stateTimer
		
	}
	
	@Override
	public void keyDown(int key){
		
	}

	@Override
	public void keyUp(int key){
		
	}
	
}
