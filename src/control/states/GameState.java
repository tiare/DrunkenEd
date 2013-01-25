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
		player.bendingSpeed = 0;
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

		if (pause) {
			if (stateTimer > pauseTime) {
				pauseFadeOut = 1;
				pause = false;
			}

			player.step(deltaTime);

			return;
		} else if (pauseFadeOut > 0) {
			pauseFadeOut -= 0.05f;
		}

		synchronized (camera) {
			if (!player.gameOver) {
				synchronized (player.getSkeleton()) {
					// add bending caused by drunkenness
					float gravity;
					if (gameSettings.useGravity) {
						gravity = gameSettings.gravityFactor * difficultyFactor;
						
						//Gravity
						player.bendingSpeed = 
								(float) Math.sin(player.drunkenBending
										+ player.steeredBending * 2) * gravity;

						player.drunkenBending += player.bendingSpeed;
					}

					player.drunkenBending += gameSettings.drunkenBendingFactor
							* ((float) Math.sin(stateTimer + Math.PI / 2) / 250.0f + (float) Math
									.sin(stateTimer * 1.7) / 350.0f)
							* difficultyFactor;

					float speed = (player.steeredBending + player.drunkenBending)
							/ fallingAngle * gameSettings.speedFactor;
					if (gameSettings.speedIsProportionalToBending) {
						player.setSpeedX(speed);
					} else {
						speed = (speed / 50.0f)
								* gameSettings.speedAccelerationFactor
								+ player.getSpeed();
						if (Math.abs(speed) > gameSettings.maxSpeed) {
							player.fallDown();
							gameOverTime = programController.getProgramTime();
						} else {

							if (flailingArms) {
								if (Math.abs(speed) < gameSettings.maxSpeed
										* gameSettings.flailingArmsSpeedFactor
										|| ((player.steeredBending + player.drunkenBending) < 0) != (player
												.getSpeed() < 0)) {
									flailingArms = false;
									player.setFlailingArms(false);
								}
							} else {
								if (Math.abs(speed) > gameSettings.maxSpeed
										* gameSettings.flailingArmsSpeedFactor
										&& ((player.steeredBending + player.drunkenBending) < 0) == (player
												.getSpeed() < 0)) {
									flailingArms = true;
									player.setFlailingArms(true);
								}
								if (player.posX <= 0.0f && speed < 0) {
									player.setSpeedX(0.0f);
								} else {
									player.setSpeedX(speed);
								}
							}
						}

						if (gameSettings.difficulty == GameSettings.GAME_HARD) {
							worldZoom += ((float) Math.sin(stateTimer
									* gameSettings.zoomFrequencyFactor) / 200.0)
									* gameSettings.zoomIntensityFactor;
						}

						float bending = Math.abs(player.drunkenBending
								+ player.steeredBending);
						if (bending > fallingAngle) {
							player.fallDown();
							gameOverTime = programController.getProgramTime();
						} else {
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
		}
		player.step(deltaTime);
	}

	@Override
	public void onDraw() {
		// reset color stuff
		graphics.bindTexture(null);
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();

		synchronized (camera) {
			
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
		}

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
