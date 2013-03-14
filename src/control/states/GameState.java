package control.states;

import figure.DrunkenSkeleton;
import model.Obstacle;
import control.Debug;
import control.GameSettings;
import control.ProgramState;
import graphics.StandardTextures;

public class GameState extends LevelState {

	private static final float PUSH_TIME = 0;

	private float fallingAngle;
	private float difficultyFactor;

	private boolean fixedCameraMode;
	private float gameOverTime;

	private float maxSpeedTime;

	private boolean pause;
	private float pauseTime;
	private float pauseFadeOut;

	private boolean swingingArms = false;
	private boolean flailingArms = false;
	private float nextEvent;
	private float push;
	private int pushDir;

	public boolean gameOverOverlay;

	public GameState() {
		super();
		useObstacles = Debug.USE_OBSTACLES;
	}

	@Override
	public void onStart() {
		super.onStart();
		
		placeObstacles((gameSettings.difficulty)*1100+11,(gameSettings.difficulty+1)*7);
		
		fallingAngle = gameSettings.fallingAngle[gameSettings.difficulty];
		fallingAngle = (float) Math.toRadians(105);
		player.bendingSpeed = 0;
		player.inGame = true;
		player.setArmAnglesByTracking(false);
		// difficultyFactor = (1 - (2 * gameSettings.difficultyAddition) +
		// gameSettings.difficulty * gameSettings.difficultyAddition);
		difficultyFactor = gameSettings.difficulty;
		pause = true;
		if (Debug.USE_COUNTDOWN)
			pauseTime = 3.0f;
		else
			pauseTime = 0;
		fixedCameraMode = false;
		maxSpeedTime = 0;

		gameOverOverlay = false;

		DrunkenSkeleton skeleton = (DrunkenSkeleton) player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX + player.posX, skeleton.mHipJoint.mPosY, worldZoom, player.drunkenBending);
		player.setFloorY(0.1f);
	}

	@Override
	public void onBend(float bending) {
		player.steeredBending = bending;
	}
	
	protected void setGameOver() {
		player.fallDown();
		gameOverTime = programController.getProgramTime();
	}

	@Override
	public void onStep(float deltaTime) {
		if(fixedCameraMode)
			deltaTime/=2;
		// calculate world rotation while considering difficulty
		// worldRotation += (float)Math.sin(stateTimer+Math.PI/2) / 100.0f;
		synchronized (camera) {

			if(useObstacles)
				worldZoom = 2.3f;
			else
				worldZoom = 2;
			float camShiftX = useObstacles?0.2f:0;
			if (gameOverOverlay) {
				brightness += (0.4f - brightness) * 0.05f;
				camera.mAdaption = 0.06f;
				camera.setRotation(camera.getRotation() * 0.96f);
				camera.setZoom(camera.getZoom() + (3.5f - camera.getZoom()) * 0.7f);
				camera.setPos(player.skeleton.mHipJoint.mPosX + player.posX, player.skeleton.mHipJoint.mPosY + 1);
			} else {
				if (pause) {
					if (stateTimer > pauseTime) {
						pauseFadeOut = 1;
						nextEvent = 2 + (float) Math.random() * 5;
						pause = false;
					}

					player.step(deltaTime);

					return;
				} else if (pauseFadeOut > 0) {
					pauseFadeOut -= 0.05f;
				}

				if (!player.gameOver) {
					float t = stateTimer - 10;
					if (t < 0)
						t = 0;
					difficultyFactor = gameSettings.difficulty + ((float) Math.pow(t, 0.5f) * 0.1f);
					if(fixedCameraMode)
						difficultyFactor = 0;
					// ---PLAYER-CONTROLS---

					synchronized (player.getSkeleton()) {

						float bendingSum = player.steeredBending + player.drunkenBending;

						if (stateTimer >= nextEvent) {
							nextEvent = stateTimer + 3 + (float) Math.random() * 3;
							if (Math.abs(bendingSum) < Math.PI / 4) {
								push = (1.5f + (float) Math.random() * 0.1f) * PUSH_TIME;
								if (bendingSum > 0)
									pushDir = 1;
								else
									pushDir = -1;
							}
						}

						if (push > 0) {
							push -= deltaTime;
							player.drunkenBending += push * Math.min(0.014f, stateTimer * 0.0003f) * pushDir;
						}

						// player.drunkenBending -= player.getSpeed()*0.005f;

						float airFactor = player.inAir()?0.5f:1;
						// add bending caused by drunkenness
						if (gameSettings.useGravity) {

							// Gravity
							int sign = player.steeredBending < 0 ? -1 : 1;
							float uBend = player.drunkenBending;
							float limit = (float) Math.PI / 2 * 0.8f;
							if (uBend > limit)
								uBend = limit;
							if (uBend < -limit)
								uBend = -limit;
							player.bendingSpeed = (float) (uBend * gameSettings.gravityFactor + player.steeredBending * player.steeredBending * sign * 0.15f) * difficultyFactor * airFactor;

							player.drunkenBending += player.bendingSpeed;
						}

						// Oszillation
						if(!fixedCameraMode)
						player.drunkenBending += gameSettings.drunkenBendingFactor
								* ((float) Math.sin(stateTimer + Math.PI / 2) / 250.0f + (float) Math.sin(stateTimer * 1.7) / 350.0f) * (stateTimer * 0.005f);

						// Acceleration
						bendingSum = player.steeredBending + player.drunkenBending;
						float acceleration = 0.0f;
						boolean noFlail = true;
						if (!fixedCameraMode && !player.inAir() && Math.abs(bendingSum) > 0.05f) {
							int sign = bendingSum < 0 ? -1 : 1;
							if (bendingSum * player.getSpeed() > 0) {
								if(Math.abs(player.getSpeed())*0.15f<Math.abs(bendingSum)) {
									// noFlail = Math.abs(player.getSpeed())>0.02f
									// && Math.abs(bendingSum)<0.4f;
									noFlail = false;
									if (!noFlail)
										acceleration = (float) Math.pow(Math.min(0.7 * Math.PI / 2, sign * bendingSum), 0.5f) * sign * 0.3f;
								}
							} else
								acceleration = bendingSum * 2;
							acceleration *= 2 / fallingAngle * gameSettings.speedFactor;
							acceleration *= (difficultyFactor * 0.2f + 0.7f);
						}

						if (gameSettings.speedIsProportionalToBending) {
							player.setSpeedX(acceleration);
						} else {
							float speed;
							speed = (acceleration / 50.0f) * gameSettings.speedAccelerationFactor + player.getSpeed();
							// Maximum speed
							if (Math.abs(speed) > gameSettings.maxSpeed) {
								if (maxSpeedTime > 0.55f) {
									setGameOver();
								} else
									maxSpeedTime += deltaTime;

							} else {
								maxSpeedTime = 0;
								// Flailing
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

								// Limit
								if (player.posX <= 0.0f && speed < 0) {
									player.setSpeedX(0.0f);
									player.posX = 0;
								} else {
									player.setSpeedX(speed);
								}
							}

							// Sine Zoom effect
							if (gameSettings.difficulty == GameSettings.GAME_HARD) {
								worldZoom += ((float) Math.sin(stateTimer * gameSettings.zoomFrequencyFactor) / 200.0) * gameSettings.zoomIntensityFactor;
							}

							//Obstacles
							if(useObstacles) {
								if(player.posY<=0.1f) {
									for(Obstacle obstacle:obstacles) {
										if(player.posX>=obstacle.getLeft() && player.posX<=obstacle.getRight()){
											setGameOver();
											break;
										}
									}
								}
							}
							
							if(!player.fellDown) {
								float bending = Math.abs(player.drunkenBending + player.steeredBending);
								// Overbend
								if (bending > fallingAngle) {
									setGameOver();
								} else {
									// Swinging
									if (swingingArms) {
										if (bending < Math.PI / 2 * gameSettings.swingingArmsBendFactor) {
											swingingArms = false;
											player.setSwingingArms(false);
										}
									} else {
										if (bending > Math.PI / 2 * gameSettings.swingingArmsBendFactor) {
											swingingArms = true;
											player.setSwingingArms(true);
										}
									}
								}
							}

						}
					}
				} else {

					// ---GAME-OVER---
					if (programController.getProgramTime() > gameOverTime + gameSettings.dyingTimeout) {
						// TODO: -1f, -1f -> distance, time
						player.fellDown = true;
						super.programController.switchState(new GameOverState(this, player.getWorldX(), stateTimer - pauseTime));
					}
				}
				DrunkenSkeleton skeleton = (DrunkenSkeleton) player.getSkeleton();
				if (!fixedCameraMode)
					camera.set(skeleton.mHipJoint.mPosX + player.posX + camShiftX, skeleton.mHipJoint.mPosY + player.posY + 0.2f, worldZoom*1.5f, player.drunkenBending);
				else if (player.getWorldX() > camera.getX() + 10) {
					fixedCameraMode = false;
				}
			}
			player.step(deltaTime);
		}
	}

	@Override
	public void onDraw() {
		super.onDraw();

		// draw initial start sequence
		if (pause) {
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics2D.drawString(0.0f, 2.3f, 0.5f, 0, 0, 0, (int) Math.ceil(pauseTime - stateTimer) + " ");
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

		// Draw stats
		if (!gameOverOverlay) {
			graphics2D.switchGameCoordinates(false);
			graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
			graphics2D.setColor(1.f, 1.f, 1.f);
			// graphics2D.drawStringL(1.2f, 0.8f, 0.1f, df.format( player.posX )
			// +"m ");
			// graphics2D.drawStringL(1.2f, 0.7f, 0.1f, df.format(
			// player.getSpeed()
			// ).replace("-","")+"m/s");
			String s = (int) player.posX + "m";
			while (s.length() < 4)
				s = " " + s;
			while (s.length() < 5)
				s = " " + s;
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

			if (!fixedCameraMode && player.posX >= 1 && !programController.markWarning)
				graphics2D.drawString(-0.24f, -0.86f, 0.15f, -1, -1, 0, 0.107f, s);
			// graphics2D.drawString(graphics2D.getScreenLeft() + 0.1f, 0.8f,
			// 0.1f,-1, -1, 0, 0.07f, s);
			// graphics2D.drawString(graphics2D.getScreenLeft() + 0.1f, 0.7f,
			// 0.1f,-1, -1, 0, 0.07f, t);
			// graphics2D.drawStringL(graphics2D.getScreenLeft() + 0.255f, 0.7f,
			// 0.1f,":");
			graphics2D.switchGameCoordinates(true);
		}

		super.drawObstacles(!gameOverOverlay);
	}

	@Override
	public void keyDown(int key) {
		if (key == 'f') {
			fixedCameraMode = true;
			camera.setPos(camera.getX() + 8f, camera.getY());
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

}
