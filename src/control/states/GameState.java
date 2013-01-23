package control.states;

import java.sql.Time;

import control.GameSettings;
import control.ProgramState;
import figure.DrunkenSkeleton;
import graphics.House;
import graphics.StandardTextures;
import graphics.translator.TextureCoordinates;


public class GameState extends WorldState {

	private float worldZoom;
	private float fallingAngle;
	private float difficultyFactor;
	
	private float gameOverTime;
	
	private boolean pause;
	private float pauseTime;
	
	private boolean swingingArms = false;
	private boolean flailingArms = false;
	
	private House house1;
	private House house2;
	
	public GameState(){
		super();
		//time = (float)Math.PI/2.0f;
		worldZoom = 2;
	}
	
	@Override
	public void onStart(){
		fallingAngle = gameSettings.fallingAngle[gameSettings.difficulty];
		player.bendingSpeed = 0;
		difficultyFactor = (1-(2*gameSettings.difficultyAddition) + gameSettings.difficulty * gameSettings.difficultyAddition);
		pause = true;
		pauseTime = 2.0f;
		
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX + player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);
		
		house1 = new House(-2.8f, StandardTextures.HOUSE1);
		house2 = new House(+2.8f, StandardTextures.HOUSE1);
		house2.setColor(0.4f, 0.2f, 0.2f);
	}
	
	@Override
	public void onBend(float bending){
		player.steeredBending = bending;
	}
	
	
	@Override
	public void onStep(float deltaTime) {
		// calculate world rotation while considering difficulty
		//worldRotation += (float)Math.sin(stateTimer+Math.PI/2) / 100.0f; 
		
		
		if( pause ){
			if (stateTimer > pauseTime){
				pause = false;
			}
			
			player.step(deltaTime);
			
			return;
		}
		if( !player.gameOver ){
			// add bending caused by drunkenness
			float gravity;
			if(gameSettings.useGravity){
				gravity = gameSettings.gravityFactor * difficultyFactor;
				player.bendingSpeed = (float)Math.sin(player.drunkenBending + player.steeredBending*2)*gravity;
				player.drunkenBending += player.bendingSpeed;
			}
			
			player.drunkenBending += gameSettings.drunkenBendingFactor * 
									((float)Math.sin(stateTimer+Math.PI/2) / 250.0f+
									(float)Math.sin(stateTimer*1.7) / 350.0f)
									* difficultyFactor;
			
			
			
			float speed = (player.steeredBending + player.drunkenBending) / fallingAngle * gameSettings.speedFactor;
			if( gameSettings.speedIsProportionalToBending ){
				player.setSpeedX( speed );
			} else {
				speed = (speed/50.0f)*gameSettings.speedAccelerationFactor + player.getSpeed();
				if( Math.abs(speed) > gameSettings.maxSpeed){
					player.fallDown();
					gameOverTime = programController.getProgramTime();
				} else{
					
					if( flailingArms ){
						if(Math.abs(speed) < gameSettings.maxSpeed * gameSettings.flailingArmsSpeedFactor) {
							flailingArms = false;
							player.setFlailingArms(false);
						}
					} else {
						if(Math.abs(speed) > gameSettings.maxSpeed * gameSettings.flailingArmsSpeedFactor) {
							flailingArms = true;
							player.setFlailingArms(true);
						}
					}
					
					player.setSpeedX( speed );
				}
			}
			
			
			
			if( gameSettings.difficulty == GameSettings.GAME_HARD ){
				worldZoom += ((float)Math.sin(stateTimer*gameSettings.zoomFrequencyFactor) / 200.0) * gameSettings.zoomIntensityFactor;
			}
			
			float bending = Math.abs( player.drunkenBending + player.steeredBending );
			if( bending > fallingAngle){
				player.fallDown();
				gameOverTime = programController.getProgramTime();
			} else {
				if(swingingArms){
					if(bending < fallingAngle * gameSettings.swingingArmsBendFactor * difficultyFactor){
						swingingArms = false;
						player.setSwingingArms(false);
					}
				} else {
					if(bending > fallingAngle * gameSettings.swingingArmsBendFactor * difficultyFactor){
						swingingArms = true;
						player.setSwingingArms(true);
					}
				}
			}
		}
		else {
			if (programController.getProgramTime() > gameOverTime + gameSettings.dyingTimeout){
				//TODO: -1f, -1f -> distance, time
				super.programController.switchState(new GameOverState(programController, player.getWorldX(), stateTimer-pauseTime).init(programController));
			}
			
		}
		
		
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX + player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);
		player.step(deltaTime);
	}

	@Override
	public void onDraw() {
		graphics.bindTexture(null);
		
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();
		
		// draw house
		house1.draw(graphics, graphics2D);
		house2.draw(graphics, graphics2D);
		
		
		// draw simple tree :)
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(0.8f,1.0f, 0.2f,2.0f, 0);
		
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(0.8f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		
		// draw left tree
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(-3.3f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(-3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(-3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/4.0f);
		graphics2D.drawRectCentered(-3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/7.0f);
		
		// draw right tree
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(3.3f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/2.0f);
		graphics2D.drawRectCentered(3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/5.0f);
		
		//draw another tree
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(8.f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(8.f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(8.f,2.5f, 1.0f,1.0f, (float)Math.PI/4.0f);
		graphics2D.drawRectCentered(8.f,2.5f, 1.0f,1.0f, (float)Math.PI/7.0f);
		
		// and another
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(10.8f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(10.8f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		
		
		// and one more
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(15.f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(15.f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(15.f,2.5f, 1.0f,1.0f, (float)Math.PI/4.0f);
		graphics2D.drawRectCentered(15.f,2.5f, 1.0f,1.0f, (float)Math.PI/7.0f);
		
		// final one - promise!
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(17.3f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(17.3f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(17.3f,2.5f, 1.0f,1.0f, (float)Math.PI/2.0f);
		graphics2D.drawRectCentered(17.3f,2.5f, 1.0f,1.0f, (float)Math.PI/5.0f);
		
		
		//player.draw();
		
		
		//draw floor
		//graphics2D.setColor(0.5f, 0.5f, 0.5f);
		//graphics2D.drawRectCentered(0,-5.0f, 20,10.0f, 0);
		//graphics2D.drawRectCentered(20,-5.0f, 20,10.0f, 0);
		//street
		
		
		//graphics2D.setColor(0.7f, 0.7f, 0.7f);
		graphics2D.setWhite();
		graphics.bindTexture(StandardTextures.STREET);
		
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX + player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);
		
		float streetWidth = 10;
		graphics2D.drawRectCentered(player.posX, -0.5f, streetWidth, 2.0f,0.0f, 4*(2*player.posX/streetWidth-1) , 0, 4*(2*player.posX/streetWidth+1),1);
		
		if( pause){
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics2D.drawString(0.0f, 2.5f, 1.0f, 0, 0, 0, (int)(pauseTime-stateTimer+1)+" ");
		}
		
		graphics.bindTexture(null);
		//graphics2D.drawRectCentered(0,-0.1f, 20,0.2f, 0);
		//graphics2D.drawRectCentered(20,-0.1f, 20,0.2f, 0);
		//player.getSpeed() < 0 ? stateTimer : -stateTimer
		
		
		player.draw();
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

	@Override
	public void userLost() {
		// TODO Auto-generated method stub
		
	}
	
}
