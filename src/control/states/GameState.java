package control.states;

import java.text.DecimalFormat;

import figure.DrunkenSkeleton;
import control.Debug;
import control.GameSettings;
import control.ProgramState;
import graphics.StandardTextures;
import graphics.background.HorizontalDrawablePool;
import graphics.background.HorizontalRow;
import graphics.background.TexturedObject;

public class GameState extends WorldState {

	private static final float PUSH_TIME = 0;
	
	private float worldZoom;
	private float fallingAngle;
	private float difficultyFactor;

	private boolean fixedCameraMode;
	private float gameOverTime;

	private boolean bendingLeft;
	private float reBend;

	private boolean pause;
	private float pauseTime;
	private float pauseFadeOut;

	private boolean swingingArms = false;
	private boolean flailingArms = false;
	private float nextEvent;
	private float push;
	private int pushDir;

	private HorizontalRow houseRow;
	private HorizontalRow streetRow;
	private HorizontalRow streetItemRow;
	
	private TexturedObject moes;

	private DecimalFormat df;

	public GameState() {
		super();
		// time = (float)Math.PI/2.0f;
		worldZoom = 2;
		df = new DecimalFormat(",#0.0");
	}

	@Override
	public void onStart() {
		fallingAngle = gameSettings.fallingAngle[gameSettings.difficulty];
		fallingAngle = (float)Math.toRadians(95);
		player.bendingSpeed = 0;
		player.inGame = true;
		player.setArmAnglesByTracking(false);
		//difficultyFactor = (1 - (2 * gameSettings.difficultyAddition) + gameSettings.difficulty * gameSettings.difficultyAddition);
		difficultyFactor = gameSettings.difficulty;
		pause = true;
		if(Debug.USE_COUNTDOWN)
			pauseTime = 3.0f;
		else
			pauseTime = 0;
		fixedCameraMode = false;

		DrunkenSkeleton skeleton = (DrunkenSkeleton) player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX + player.posX,
				skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);

		// configure random houses
		HorizontalDrawablePool pool = new HorizontalDrawablePool();

		TexturedObject to;
		pool.add(new TexturedObject(StandardTextures.HOUSE1));
		to = new TexturedObject(StandardTextures.HOUSE1);
		to.setColor(0.25f, 0.2f, 0.2f);
		pool.add(to);

		to = new TexturedObject(StandardTextures.HOUSE1);
		to.setColor(0.2f, 0.2f, 0.2f);
		pool.add(to);

		to = new TexturedObject(StandardTextures.HOUSE1);
		to.setColor(0.2f, 0.25f, 0.2f);
		pool.add(to);

		to = new TexturedObject(StandardTextures.HOUSE1);
		to.setColor(0.2f, 0.2f, 0.25f);
		pool.add(to);

		houseRow = new HorizontalRow(pool);
		houseRow.setStart(-2.4f);
		
		// add moes tavern only once
		moes = new TexturedObject(StandardTextures.MOES);
		
		houseRow.add(moes);
		

		// configure random trees
		float yOffset = +0.17f;
		pool = new HorizontalDrawablePool();
		//Tree t = new Tree();
		// t.setColor(0.2f, 0.2f, 0.2f);
		//pool.add(t);
		to = new TexturedObject(StandardTextures.TREE1);
		to.setYOffset(yOffset);
		pool.add(to);
		

		to = new TexturedObject(StandardTextures.TREE2);
		to.setYOffset(yOffset);
		pool.add(to);
		
		// add Lantern to pool
		to = new TexturedObject(StandardTextures.LANTERN);
		to.setColor(0.2f, 0.2f, 0.2f);
		to.setYOffset(yOffset+0.1f);
		pool.add(to);

		streetItemRow = new HorizontalRow(pool);
		streetItemRow.setSpacerWidth(0.3f, 2.7f);
		streetItemRow.setStart(-1.5f);
		
		
		// street stuff
		pool = new HorizontalDrawablePool();
		
		to = new TexturedObject(StandardTextures.STREET);
		to.setYOffset(-2.6f);
		pool.add(to);
		streetRow = new HorizontalRow(pool);
		streetRow.setSpacerWidth(0, 0);
		streetRow.setStart(-5);
		
		
	}

	@Override
	public void onBend(float bending) {
		bendingLeft = player.steeredBending > bending;

		player.steeredBending = bending;
	}

	@Override
	public void onStep(float deltaTime) {
		// calculate world rotation while considering difficulty
		// worldRotation += (float)Math.sin(stateTimer+Math.PI/2) / 100.0f;
		synchronized (camera) {
			
		if (pause) {
			if (stateTimer > pauseTime) {
				pauseFadeOut = 1;
				nextEvent = 2+(float)Math.random()*5;
				pause = false;
			}

			player.step(deltaTime);

			return;
		} else if (pauseFadeOut > 0) {
			pauseFadeOut -= 0.05f;
		}

		
			if (!player.gameOver) {
				float t = stateTimer-6;
				if(t<0)
					t=0;
				difficultyFactor = gameSettings.difficulty + ((float)Math.pow(t,0.8f)*0.05f);
				//---PLAYER-CONTROLS---
				
				synchronized (player.getSkeleton()) {
					
					float bendingSum = player.steeredBending + player.drunkenBending;
					
					if(stateTimer>=nextEvent) {
						nextEvent = stateTimer+3+(float)Math.random()*3;
						if(Math.abs(bendingSum)<Math.PI/4) {
							push = (1.5f+(float)Math.random()*0.1f)*PUSH_TIME;
							if(bendingSum>0)
								pushDir = 1;
							else
								pushDir = -1;
						}
					}
			
					if(push>0) {
						push-=deltaTime;
						player.drunkenBending += push*Math.min(0.014f,stateTimer*0.0003f)*pushDir;
					}
					
					//player.drunkenBending -= player.getSpeed()*0.005f;
					
					// add bending caused by drunkenness
					float gravity;
					if (gameSettings.useGravity) {
						gravity = gameSettings.gravityFactor;
						
						//Gravity
						int sign = player.steeredBending<0?-1:1;
						player.bendingSpeed = 
								(float) (player.drunkenBending * gravity + player.steeredBending * player.steeredBending * sign * 0.1f) * difficultyFactor;

						player.drunkenBending += player.bendingSpeed;
					}

					//Oszillation
					player.drunkenBending += gameSettings.drunkenBendingFactor * ((float) Math.sin(stateTimer + Math.PI / 2) / 250.0f 
							+ (float) Math.sin(stateTimer * 1.7) / 350.0f) * (stateTimer*0.005f);

					bendingSum = player.steeredBending + player.drunkenBending;
					int sign = bendingSum<0?-1:1;
					float acceleration = 0.0f;
					if(bendingSum * player.getSpeed()>0) {
						if(Math.abs(player.getSpeed())*0.3f<Math.abs(bendingSum))
							acceleration = (float)Math.pow(Math.min(0.8*Math.PI/2,sign*bendingSum),0.9f)*sign * 1.1f;
					}else
						acceleration = bendingSum * 2;
					acceleration *= 2 / fallingAngle * gameSettings.speedFactor;
					acceleration *= (difficultyFactor*0.4f+0.5f);
					if (gameSettings.speedIsProportionalToBending) {
						player.setSpeedX(acceleration);
					} else {
						float speed;
						speed = (acceleration / 50.0f) * gameSettings.speedAccelerationFactor + player.getSpeed();
						//Maximum speed
						if (Math.abs(speed) > gameSettings.maxSpeed) {
							player.fallDown();
							gameOverTime = programController.getProgramTime();
						} else {

							//Flailing
							if (flailingArms) {
								if (Math.abs(speed) < gameSettings.maxSpeed * gameSettings.flailingArmsSpeedFactor
										|| ((player.steeredBending + player.drunkenBending) < 0) != (player.getSpeed() < 0)) {
									flailingArms = false;
									player.setFlailingArms(false);
								}
							} else {
								if (Math.abs(speed) > gameSettings.maxSpeed * gameSettings.flailingArmsSpeedFactor
										&& ((player.steeredBending + player.drunkenBending) < 0) == (player.getSpeed() < 0)) {
									flailingArms = true;
									player.setFlailingArms(true);
								}
							}
							
							//Limit
							if (player.posX <= 0.0f && speed < 0) {
								player.setSpeedX(0.0f);
								player.posX = 0;
							} else {
								player.setSpeedX(speed);
							}
						}

						//Sine Zoom effect
						if (gameSettings.difficulty == GameSettings.GAME_HARD) {
							worldZoom += ((float) Math.sin(stateTimer
									* gameSettings.zoomFrequencyFactor) / 200.0)
									* gameSettings.zoomIntensityFactor;
						}

						float bending = Math.abs(player.drunkenBending + player.steeredBending);
						//Overbend
						if (bending > fallingAngle) {
							//player.fallDown();
							//gameOverTime = programController.getProgramTime();
						} else {
							
							//Swinging
							if (swingingArms) {
								if (bending < fallingAngle
										* gameSettings.swingingArmsBendFactor) {
									swingingArms = false;
									player.setSwingingArms(false);
								}
							} else {
								if (bending > fallingAngle
										* gameSettings.swingingArmsBendFactor) {
									swingingArms = true;
									player.setSwingingArms(true);
								}
							}
						}
					}
				}
			} else {
				
				//---GAME-OVER---
				if (programController.getProgramTime() > gameOverTime
						+ gameSettings.dyingTimeout) {
					// TODO: -1f, -1f -> distance, time
					super.programController.switchState(new GameOverState(
							programController, player.getWorldX(), stateTimer
									- pauseTime).init(programController));
				}
			}
			DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
			if(!fixedCameraMode)
				camera.set(skeleton.mHipJoint.mPosX+player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending );
			else
				if(player.getWorldX()>camera.getX()+8) {
					fixedCameraMode = false;
				}
		
			player.step(deltaTime);
		}
	}

	@Override
	public void onDraw() {
		// reset color stuff
		graphics.bindTexture(null);
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();

		// draw houses
		houseRow.draw(graphics, graphics2D, camera.getX());
		
		// draw street
		streetRow.draw(graphics, graphics2D, camera.getX());
		/*graphics2D.setWhite();
		graphics.bindTexture(StandardTextures.STREET);
		float streetWidth = 10;
		graphics2D.drawRectCentered(player.posX, -1.0f, streetWidth, 2.75f,
				0.0f, 4 * (2 * player.posX / streetWidth - 1), 1, 4 * (2
						* player.posX / streetWidth + 1), 0);
		graphics.bindTexture(null);*/
		
		
		// draw trees lanterns and banks
		streetItemRow.draw(graphics, graphics2D, camera.getX());
		
		

		// draw initial start sequence
		if (pause) {
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics2D.drawString(0.0f, 2.3f, 0.5f, 0, 0, 0,
					(int) Math.ceil(pauseTime - stateTimer) + " ");
		} else if (pauseFadeOut > 0) {
			graphics2D.setColor(1.f, 1.f, 1.f, pauseFadeOut);
			graphics2D.drawString(0.0f, 2.3f, 0.5f, 0, 0, 0, "Walk!");
		}

		// config camera
		// synchronized(player.getSkeleton()) {
		// DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		// camera.set(skeleton.mHipJoint.mPosX + player.posX,
		// skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);
		// }
		// draw player
		player.draw();

		// draw stats
		graphics2D.switchGameCoordinates(false);
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
		graphics2D.setColor(1.f, 1.f, 1.f);
		// graphics2D.drawStringL(1.2f, 0.8f, 0.1f, df.format( player.posX )
		// +"m ");
		// graphics2D.drawStringL(1.2f, 0.7f, 0.1f, df.format( player.getSpeed()
		// ).replace("-","")+"m/s");
		String s = (int) player.posX + "m";
		while (s.length() < 5)
			s = "0" + s;
		String t = (int) (stateTimer - pauseTime) % 60 + "";

		if (stateTimer - pauseTime < 0)
			t = "00";

		if (t.length() < 2) {
			t = "0" + t;
		}
		t = (int) (stateTimer - pauseTime) / 60 + " " + t;
		if (t.length() < 5) {
			t = "0" + t;
		}

		graphics2D.drawString(graphics2D.getScreenLeft() + 0.1f, 0.8f, 0.1f,
				-1, -1, 0, 0.07f, s);
		graphics2D.drawString(graphics2D.getScreenLeft() + 0.1f, 0.7f, 0.1f,
				-1, -1, 0, 0.07f, t);
		graphics2D.drawStringL(graphics2D.getScreenLeft() + 0.255f, 0.7f, 0.1f,
				":");
		graphics2D.switchGameCoordinates(true);

	}

	@Override
	public void keyDown(int key) {
		if(key == 'f') {
			fixedCameraMode = true;
			camera.setPos(camera.getX()+8f, camera.getY());
			camera.setRotation(0);
		}
	}

	@Override
	public void keyUp(int key) {

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
