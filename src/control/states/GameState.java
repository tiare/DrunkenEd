package control.states;

import java.text.DecimalFormat;

import control.GameSettings;
import control.ProgramState;
import figure.DrunkenSkeleton;
import graphics.StandardTextures;
import graphics.background.HorizontalDrawablePool;
import graphics.background.HorizontalRow;
import graphics.background.House;
import graphics.background.Tree;


public class GameState extends WorldState {

	private float worldZoom;
	private float fallingAngle;
	private float difficultyFactor;
	
	private float gameOverTime;
	
	private boolean bendingLeft;
	private float reBend;
	
	private boolean pause;
	private float pauseTime;
	
	private boolean swingingArms = false;
	private boolean flailingArms = false;
	
	private HorizontalRow houseRow;
	private HorizontalRow treeRow;
	
	private DecimalFormat df;
	
	public GameState(){
		super();
		//time = (float)Math.PI/2.0f;
		worldZoom = 2;
		df = new DecimalFormat  ( ",##0.00" );
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
		
		
		// configure random houses
		HorizontalDrawablePool pool = new HorizontalDrawablePool();
		
		House h;
		pool.add(new House(StandardTextures.HOUSE1));
		h = new House(StandardTextures.HOUSE1);
		h.setColor(0.4f, 0.2f, 0.2f);
		pool.add(h);
		
		h = new House( StandardTextures.HOUSE1);
		h.setColor(0.2f, 0.2f, 0.2f);
		pool.add(h);
		
		h = new House(StandardTextures.HOUSE1);
		h.setColor(0.2f, 0.4f, 0.2f);
		pool.add(h);
		
		h = new House( StandardTextures.HOUSE1);
		h.setColor(0.2f, 0.2f, 0.4f);
		pool.add(h);
		
		houseRow = new HorizontalRow(pool);
		
		// configure random trees
		pool = new HorizontalDrawablePool();
		Tree t = new Tree();
		//t.setColor(0.2f, 0.2f, 0.2f);
		pool.add(t);
		
		treeRow = new HorizontalRow(pool);
		treeRow.setSpacerWidth(0.3f, 2.7f);
		
	}
	
	@Override
	public void onBend(float bending){
		bendingLeft = player.steeredBending > bending;
		
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
		if( !player.gameOver )
		synchronized(player.getSkeleton()) {
			// add bending caused by drunkenness
			float gravity;
			if(gameSettings.useGravity){
				gravity = gameSettings.gravityFactor * difficultyFactor;
				
				
				if(		
						Math.abs( player.drunkenBending + player.steeredBending) > (Math.PI/6) 
						&& (bendingLeft ^ (player.drunkenBending + player.steeredBending<0))
					){
					
					//System.out.println(bendingLeft + " " + (player.drunkenBending + player.steeredBending));
					reBend = bendingLeft ? (float)-Math.PI / 2.0f : (float)Math.PI / 2.0f;
					//player.steeredBending += (bendingLeft) ? (Math.PI/10) : -(Math.PI/10);
					//player.drunkenBending += (bendingLeft) ? -(Math.PI/20) : (Math.PI/20);
				}
				
				float addBend = 0;
				/*if( reBend != 0){
					reBend *= 0.1f;
					addBend = reBend;// * 0.5f;
					if(Math.abs( reBend ) < 0.05){
						reBend = 0;
					}
				}*/
				player.bendingSpeed = (float)Math.sin(player.drunkenBending + player.steeredBending*2)*gravity +addBend;
				//player.bendingSpeed = 0;
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
					if(bending < fallingAngle * gameSettings.swingingArmsBendFactor){
						swingingArms = false;
						player.setSwingingArms(false);
					}
				} else {
					if(bending > fallingAngle * gameSettings.swingingArmsBendFactor){
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
		
		
		synchronized(player.getSkeleton()) {
			DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
			camera.set(skeleton.mHipJoint.mPosX + player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);
			graphics2D.setCamera(camera);
		}
		player.step(deltaTime);
	}

	@Override
	public void onDraw() {
		// reset color stuff
		graphics.bindTexture(null);
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();
		
		// draw houses and trees
		houseRow.draw(graphics, graphics2D,player.posX);
		treeRow.draw(graphics, graphics2D, player.posX);
		
		
		// draw street
		graphics2D.setWhite();
		graphics.bindTexture(StandardTextures.STREET);
		
		float streetWidth = 10;
		graphics2D.drawRectCentered(player.posX, -0.5f, streetWidth, 2.0f,0.0f, 4*(2*player.posX/streetWidth-1) , 0, 4*(2*player.posX/streetWidth+1),1);
		graphics.bindTexture(null);
		
		// draw initial start sequence
		if( pause){
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics2D.drawString(0.0f, 2.5f, 1.0f, 0, 0, 0, (int)(pauseTime-stateTimer+1)+" ");
		}
		
		//config camera
//		synchronized(player.getSkeleton()) {
//			DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
//			camera.set(skeleton.mHipJoint.mPosX + player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);
//		}
		//draw player
		player.draw();
		
		//draw stats
		graphics2D.switchGameCoordinates(false);
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
		graphics2D.setColor(1.f, 1.f, 1.f);
//		graphics2D.drawStringL(1.2f, 0.8f, 0.1f, df.format( player.posX ) +"m ");
		//graphics2D.drawStringL(1.2f, 0.7f, 0.1f, df.format( player.getSpeed() ).replace("-","")+"m/s");
		String s = df.format( player.posX ) +"m";
		while(s.length()<7)
			s = "0"+s;
		graphics2D.drawString(graphics2D.getScreenLeft()+0.1f, 0.8f, 0.1f, -1, -1, 0, 0.07f, s);
		graphics2D.drawString(graphics2D.getScreenLeft()+0.1f, 0.7f, 0.1f, -1, -1, 0, 0.07f, df.format( player.getSpeed() ).replace("-","")+"m/s");
		graphics2D.switchGameCoordinates(true);
		
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
