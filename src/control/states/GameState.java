package control.states;

import figure.DrunkenSkeleton;
import graphics.StandardTextures;


public class GameState extends WorldState {

	private float worldRotation;
	private float worldZoom;
	//private float time;
	
	public GameState(){
		super();
		//time = (float)Math.PI/2.0f;
		worldZoom = 2;
	}
	
	@Override
	public void onStep(float deltaTime) {
		//float timeShift = 0.9f + (float)Math.random()/5.0f;
		
		//stateTimer;
		
		//time += deltaTime;//*timeShift;
		// calculate world rotation while considering difficulty
		worldRotation += (float)Math.sin(stateTimer+Math.PI/2) / 100.0f; 
		
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		
		//if( programController.gamesettings.difficulty == GameSettings.GAME_HARD )
		camera.set(skeleton.mHipJoint.mPosX, skeleton.mHipJoint.mPosY, worldZoom, worldRotation);
		
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
