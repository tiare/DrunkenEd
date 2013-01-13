package control.states;

import control.GameSettings;
import control.ProgramState;
import figure.DrunkenSkeleton;


public class GameState extends WorldState {

	private float worldZoom;
	private float fallingAngle;
	//private float time;
	
	public GameState(){
		super();
		//time = (float)Math.PI/2.0f;
		worldZoom = 2;
	}
	
	@Override
	public void onStart(){
		fallingAngle = gameSettings.fallingAngle[gameSettings.difficulty];
	}
	
	@Override
	public void onBend(float bending){
		player.steeredBending += bending;
		float speed = (player.steeredBending + player.drunkenBending) / fallingAngle * gameSettings.maxSpeed;
		player.setSpeedX( speed );
		
	}
	
	
	@Override
	public void onStep(float deltaTime) {
		// calculate world rotation while considering difficulty
		//worldRotation += (float)Math.sin(stateTimer+Math.PI/2) / 100.0f; 
		
		
		
		if( !player.gameOver ){
			// add bending caused by drunkenness
			player.drunkenBending += (float)Math.sin(stateTimer+Math.PI/2) / 100.0f;
			
			if( gameSettings.difficulty == GameSettings.GAME_HARD ){
				worldZoom += (float)Math.sin(stateTimer*1.3) / 200.0f;
			}
		}
		
		if( Math.abs( player.drunkenBending + player.steeredBending ) > fallingAngle){
			player.fallDown();
		}
		
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX + player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);
		player.step(deltaTime);
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();
		
		
		// draw simple tree :)
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(0.8f,1.0f, 0.2f,2.0f, 0);
		
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(0.8f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		
		
		
		player.draw();
		
		//graphics.bindTexture(StandardTextures.CUBE);
		graphics2D.setColor(0.5f, 0.5f, 0.5f);
		graphics2D.drawRectCentered(0,-5.0f, 20,10.0f, 0);
		graphics2D.setColor(0.7f, 0.7f, 0.7f);
		graphics2D.drawRectCentered(0,-0.1f, 20,0.2f, 0);
		//player.getSpeed() < 0 ? stateTimer : -stateTimer
		
		
		
	}
	
	@Override
	public void keyDown(int key){
		
	}

	@Override
	public void keyUp(int key){
		
	}
	
	@Override
	public int getType() {
		return ProgramState.GAME;
	}
	
}
