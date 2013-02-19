package control.states;

import java.util.ArrayList;
import java.util.LinkedList;

import figure.DrunkenSkeleton;
import figure.Player;
import control.Debug;
import control.GameSettings;
import control.ProgramController;
import graphics.FloatColor;
import graphics.StandardTextures;
import graphics.skeletons.elements.Bone;
import graphics.skeletons.elements.Joint;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;

public class MainMenuState extends WorldState {

	public static boolean NO_USER_TEST = false; // set to true, if you wish to
												// test the case of no user detected
	private static final int NONE = -1, LEFT = 0, CENTER = 1, RIGHT = 2;
	private static final float SPEED_FACTOR = 3.5f;
	private static final float HELP_FREQUENCY = 4;
	private static final float HELP_INTENSITY = 0.018f;
	private static final String START_TEXT = "Step onto the mark to play!";
	private static final String TITLE_TEXT = "Drunken Ed";
	private static final String DEFAULT_TEXT = "Choose your difficulty!";
	private static final String DRINK_TEXT = "Drink to start!";
	private static final String BEND_TEXT = "Bend to move!";
	public static final FloatColor HELP_COLOR1 = new FloatColor(0.9f, 0.7f, 0.3f);
	public static final FloatColor HELP_COLOR2 = new FloatColor(1.f, 1.f, 0.2f);
	public static final FloatColor TITLE_COLOR1 = new FloatColor(0.4f, 0.7f, 0.4f);
	public static final FloatColor TITLE_COLOR2 = new FloatColor(0.8f, 1.f, 0.6f);
	public static final FloatColor GAME_TITLE_COLOR1 = new FloatColor(0.4f,0.4f,0.8f);
	public static final FloatColor GAME_TITLE_COLOR2 = new FloatColor(0.6f,0.6f,0.99f);

	private boolean trackedUser = true;

	private int activeLevel = NONE;
	private float barWidth = 6.f;
	private float barHeight = 1.3f;
	private float barPosX = 0.f;
	private float barPosY = 0.65f;
	private float stoolWidth = 1.f;
	private float stoolHeight = 1.2f;
	private float stoolLx = -2.f;
	private float stoolCx = 0.f;
	private float stoolRx = 2.f;
	private float stoolsY = 0.6f;
	private float highscoresY = 2.9f;
	private float highscoreWith = 1.8f;
	private float highscoreHeight = 1.7f;
	private float oldPlayerPosX = 0f;

	private float drinkTime = 0.f;
	private float timeout = 2.f;
	private int gulp;
	private boolean startLevel;

	private int[] scoresEasy;
	private int[] scoresMedium;
	private int[] scoresHard;

	// Highscore pictures
	ArrayList<Texture> easyWinners;
	ArrayList<Texture> mediumWinners;
	ArrayList<Texture> hardWinners;

	private Player shadowPlayer;
	private DrunkenSkeleton shadowSkeleton;
	private DrunkenSkeleton skeleton;
	private float shadowElbowAngle = 0;
	private float shadowShoulderAngle = 0;
	private float shadowStepAngle = 93;

	private float headAngle = 150;
	private float elbowAngle1 = 165; // 1 is beer setting
	private float elbowAngle2 = 120; // 2 is wine and vodka setting
	private float shoulderAngle1 = 10;
	private float shoulderAngle2 = 20;
	private float handAngle1 = 240;
	private float handAngle2 = 240;
	private float inactiveElbowAngle = 10;
	private float inactiveShoulderAngle = -15;
	private float angleIncrease = 30;

	private float activationTime = 0;
	private float hintTimeout = 3.f;
	private boolean waitedLongEnough = false;
	private LinkedList<Float> traveledDistances;
	private float minDistance = 0.8f;

	private boolean showArrows = false;
	private float blinkValue = 0;

	@Override
	public MainMenuState init(ProgramController programController) {
		this.programController = programController;
		super.init(programController);

		drinkTime = programController.getProgramTime();
		gulp = 0;

		player.posX = -0.8f;
		startLevel = false;
		player.inGame = false;

		// Get Highscores
		scoresEasy = highscores.highscoresEasy;
		scoresMedium = highscores.highscoresMedium;
		scoresHard = highscores.highscoresHard;
		
		if (Debug.SETFAKESCORES) {
			highscores.addHighscore(GameSettings.GAME_EASY, 299, null);
			highscores.addHighscore(GameSettings.GAME_EASY, 210, null);
			highscores.addHighscore(GameSettings.GAME_EASY, 8, null);
			

			highscores.addHighscore(GameSettings.GAME_MEDIUM, 210, null);
			highscores.addHighscore(GameSettings.GAME_MEDIUM, 73, null);
			highscores.addHighscore(GameSettings.GAME_EASY, 19, null);
			

			highscores.addHighscore(GameSettings.GAME_HARD, 1337, null);
			highscores.addHighscore(GameSettings.GAME_HARD, 42, null);
			highscores.addHighscore(GameSettings.GAME_HARD, 12, null);
		}

		traveledDistances = new LinkedList<Float>();

		shadowPlayer = new Player(false);
		shadowPlayer.init(programController);
		shadowSkeleton = (DrunkenSkeleton) shadowPlayer.getSkeleton();

		player.setArmAnglesByTracking(true);
		skeleton = (DrunkenSkeleton) player.getSkeleton();
		skeleton.mBottleAutoAngle = true;

		player.jumpEnabled = false;
		return this;
	}

	@Override
	public void onStep(float deltaTime) {
		synchronized (camera) {
			if (!Debug.FAKE_CONTROLS)
				trackedUser = programController.tracking.trackedUser;
			else
				trackedUser = !NO_USER_TEST;

			blinkValue = pulse(4.0f, 1);

			camera.mAdaption = 0.1f;
			if (!trackedUser) {
				camera.set(0, 1.95f, 2.3f);

				return;
			}

			camera.set(player.getCenterX(), 1.7f, 2.3f);
			//camera.set(0, 1.7f, 2.3f);

			oldPlayerPosX = player.posX;
			player.step(deltaTime);
			if (startLevel) {
				if (programController.getProgramTime() > drinkTime + timeout) {
					// start game
					programController.fadeToState(new GameState());
				} else {
					prepareForDrinkingGesture(false);
					doDrinkingGesture(false);
				}
			}

			// don't let the player walk out of the screen
			dontLeaveScreen();
			updateActiveLevel();

			if (activeLevel != NONE && programController.getProgramTime() > activationTime + hintTimeout)
				waitedLongEnough = true;
			else if (waitedLongEnough) {
				waitedLongEnough = false;
				prepareForDrinkingGesture(true);
			}

			shadowPlayer.posX = player.posX;
			shadowPlayer.posY = player.posY;
			updateShadowPosition();

			if (waitedLongEnough && !startLevel) {
				doDrinkingGesture(true);
			}

			traveledDistances.push(Math.abs(oldPlayerPosX - player.posX));
			// does the player need a hint?
			needBendingHint();
		}
	}

	private void updateShadowPosition() {
		LinkedList<Joint> joints = skeleton.mJoints;
		LinkedList<Joint> shadowJoints = shadowSkeleton.mJoints;
		Joint currentShadowJoint;
		Joint currentJoint;

		for (int i = 0; i < joints.size() - 1; i++) {
			currentJoint = joints.get(i);
			currentShadowJoint = shadowJoints.get(i);

			currentShadowJoint.mPosX = currentJoint.mPosX;
			currentShadowJoint.mPosY = currentJoint.mPosY;
		}

		LinkedList<Bone> shadowBones = shadowSkeleton.mBones;
		for (Bone bone : shadowBones) {
			bone.mVisible = false;
		}

		shadowSkeleton.refreshBottle();

	}

	private void prepareForDrinkingGesture(boolean hint) {
		if (hint) {
			shadowElbowAngle = 0;
			shadowShoulderAngle = 0;
		} else {
			float increase =  angleIncrease*gulp;
			skeleton.mHeadJoint.setPosByAngle((float) Math.toRadians(headAngle+increase));
			// set inactive left arm
			skeleton.mLeftElbowJoint.setPosByAngle((float) Math.toRadians(inactiveShoulderAngle));
			skeleton.mLeftHandJoint.setPosByAngle((float) Math.toRadians(inactiveElbowAngle));
			// set right drinking arm
			skeleton.mRightElbowJoint.setPosByAngle((float) Math.toRadians((activeLevel == LEFT) ? shoulderAngle1+increase*1.2 : shoulderAngle2+increase*1.2));
			skeleton.mRightHandJoint.setPosByAngle((float) Math.toRadians((activeLevel == LEFT) ? elbowAngle1+increase*0.5 : elbowAngle2+increase*0.45));
			skeleton.mBottleJoint.setPosByAngle((float) Math.toRadians((activeLevel == LEFT) ? handAngle1+increase*0.6 : handAngle2+increase*0.55));
			shadowSkeleton.refreshBottle();
		}
	}

	private void doDrinkingGesture(boolean hint) {
		if (hint) {
			if (shadowElbowAngle > 160 && shadowShoulderAngle > 60)
				shadowStepAngle *= -1;

			if (shadowElbowAngle < 90 || shadowShoulderAngle < 30) {
				shadowElbowAngle = 90;
				shadowShoulderAngle = 30;
				shadowStepAngle *= -1;
			}
			shadowElbowAngle += 160 / shadowStepAngle;
			shadowShoulderAngle += 60 / shadowStepAngle;
			shadowSkeleton.mRightElbowJoint.setPosByAngle((float) Math.toRadians(shadowShoulderAngle));
			shadowSkeleton.mRightHandJoint.setPosByAngle((float) Math.toRadians(shadowElbowAngle));

			shadowSkeleton.setModColor(0.f, 0.f, 0.f, 0.9f);
			shadowSkeleton.setAddColor(0.1f, 0.1f, 0.15f);
			shadowSkeleton.mDrawContour = false;
			shadowSkeleton.mRightLowerArmBone.mVisible = true;
			shadowSkeleton.mRightUpperArmBone.mVisible = true;

			shadowSkeleton.refreshBottle();
		} else {
			if (programController.getProgramTime() > drinkTime + gulp * (timeout / 3)) {
				// Empty the drink a little further
				skeleton.setDrinkState(gulp);
				gulp++;
			}
		}
	}

	private void needBendingHint() {
		if (traveledDistances.size() > 150) {
			float totalDistance = 0;
			for (Float distance : traveledDistances) {
				totalDistance += distance;
			}

			if (traveledDistances.size() > 299) {
				traveledDistances.removeLast();

			}

			if (totalDistance > minDistance)
				showArrows = false;
			else {
				showArrows = true;
			}
		}
	}

	@Override
	public void onDraw() {
		synchronized (camera) {
			super.drawBackground(1.6f, 0);
			graphics.bindTexture(null);

			// floor
			graphics2D.setColor(0.5f, 0.5f, 0.5f);
			graphics2D.drawRectCentered(0, -5.0f, 20, 10.0f, 0);
			graphics2D.setColor(0.7f, 0.7f, 0.7f);
			graphics2D.drawRectCentered(0, -0.1f, 20, 0.2f, 0);

			// Draw back wall
			graphics2D.setColor(0.4f, 0.3f, 0.2f);
			graphics2D.drawRectCentered(0, 2.5f, 8 ,5.0f);
	
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics.bindTexture(StandardTextures.WALL);
			graphics2D.drawRectCentered(-2, 0.65f, 4, 1.25f);
			graphics2D.drawRectCentered( 2, 0.65f, 4, 1.25f);
			graphics.bindTexture(null);

			// Draw side walls
			graphics.bindTexture(StandardTextures.BRICK_LEFT);
			graphics2D.drawRectCentered(-4.f, 2.15f, 0.3f, 4.72f, 0);
			graphics.bindTexture(StandardTextures.BRICK_RIGHT);
			graphics2D.drawRectCentered(4.5f, 2.15f, 1.2f, 4.72f, 0);
			graphics.bindTexture(null);

			// drawBar
			if (trackedUser) {
				graphics.bindTexture(StandardTextures.TAP);
				graphics2D.drawRectCentered(0.7f, 1.44f, 0.2f, 0.3f);
			}
			graphics.bindTexture(StandardTextures.BAR);
			graphics2D.drawRectCentered(barPosX, barPosY, barWidth, barHeight);
			graphics.bindTexture(null);

			drawHighscores(stoolLx, highscoresY, LEFT, "Beer");
			drawHighscores(stoolCx, highscoresY, CENTER, "Wine");
			drawHighscores(stoolRx, highscoresY, RIGHT, "Vodka");

			drawStool(stoolLx, stoolsY, (activeLevel == 0), LEFT);
			drawStool(stoolCx, stoolsY, (activeLevel == 1), CENTER);
			drawStool(stoolRx, stoolsY, (activeLevel == 2), RIGHT);
			graphics2D.setColor(1.f, 1.f, 1.f);

			if (!trackedUser) { // Do this if there is no tracked player
				graphics2D.switchGameCoordinates(false);
				graphics2D.setColorWeighted(HELP_COLOR1, HELP_COLOR2, blinkValue);
				graphics2D.drawString(0, -0.14f, 0.13f + pulse(HELP_FREQUENCY, HELP_INTENSITY), 0, 0, 0, START_TEXT);
				// graphics2D.setColorWeighted(TITLE_COLOR1, TITLE_COLOR2,
				// blinkValue);
				graphics2D.setColorWeighted(GAME_TITLE_COLOR1,GAME_TITLE_COLOR2,pulse(HELP_FREQUENCY*0.5f,1));
				graphics2D.drawString(0, 0.85f, 0.19f + (float)Math.sin(stateTimer*1.5f)*0.02f, 0, 0, (float) Math.sin(stateTimer * 3) * 0.05f, TITLE_TEXT);
				graphics.bindTexture(null);
				graphics2D.switchGameCoordinates(true);

				return;
			}
			
			// Draw stuff on walls
			graphics.bindTexture(StandardTextures.DART);
			graphics2D.drawRectCentered(-3.4f, 1.8f, 0.7f, 0.7f);
//			graphics.bindTexture(StandardTextures.FLAG1);
//			graphics2D.drawRectCentered(-3.4f, 2.7f, 0.5f, 0.5f);
			graphics.bindTexture(StandardTextures.FLAG2);
			graphics2D.drawRectCentered(-3.4f, 2.7f, 0.6f, 0.4f);
			graphics.bindTexture(StandardTextures.PICTURE1);
			graphics2D.drawRectCentered(3.35f, 2.8f, 0.35f, 0.5f);
//			graphics.bindTexture(StandardTextures.PICTURE2);
//			graphics2D.drawRectCentered(3.2f, 3.f, 0.45f, 0.45f);
			graphics.bindTexture(StandardTextures.PICTURE3);
			graphics2D.drawRectCentered(3.3f, 1.7f, 0.5f, 0.4f);
			graphics.bindTexture(null);

			// Display bottom text
			graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
			graphics2D.switchGameCoordinates(false);
			if ((waitedLongEnough || showArrows) && !startLevel) {
				graphics2D.setColorWeighted(HELP_COLOR1, HELP_COLOR2, blinkValue);
				if (!programController.markWarning) {
					if (waitedLongEnough) {
						graphics2D.drawString(0, -0.9f, 0.13f + pulse(HELP_FREQUENCY, HELP_INTENSITY), 0, 0, 0, DRINK_TEXT);
					} else {
						graphics2D.drawString(0, -0.9f, 0.13f + pulse(HELP_FREQUENCY, HELP_INTENSITY), 0, 0, 0, BEND_TEXT);
					}
				}
			}

			graphics2D.setColor(GAME_TITLE_COLOR2);
			if (!programController.markWarning)
				graphics2D.drawString(0, 0.92f, 0.125f+pulse(HELP_FREQUENCY*0.5f,0.01f), 0, 0, 0, DEFAULT_TEXT);
			graphics2D.switchGameCoordinates(true);
			graphics.bindTexture(null);

			showBendingHint();

			player.skeleton.setDrinkId(activeLevel);
			shadowPlayer.skeleton.setDrinkId(activeLevel);

			if (waitedLongEnough && !startLevel)
				shadowPlayer.draw();

			graphics2D.setWhite();
			player.draw();
		}
	}

	private void drawStool(float posX, float posY, boolean active, int drink) {
		if (!active && trackedUser) {
			float drinkY = (drink > 0 ? posY + 0.96f : posY + 0.79f);
			// display the drinks
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics.bindTexture(StandardTextures.ED_SKELETON);
			graphics2D.drawRectCentered(posX, drinkY, 3.5f, 3.5f, 0, skeleton.mBottleBone.mTexCoords.get(drink * 4));
		}

		if (active) {
			graphics2D.setColor(1.f, 1.f, 1.f);
		} else {
			graphics2D.setColor(0.4f, 0.4f, 0.4f);
		}
		graphics.bindTexture(StandardTextures.STOOL);
		graphics2D.drawRectCentered(posX, posY - 0.15f, stoolWidth / 1.5f, stoolHeight - 0.3f);
		graphics.bindTexture(null);
	}

	private void drawHighscores(float posX, float posY, int position, String title) {
		if (position == activeLevel) {
			graphics2D.setColor(1.f, 1.f, 1.f);
		} else {
			graphics2D.setColor(0.6f, 0.6f, 0.6f);
		}

		graphics.bindTexture(StandardTextures.BLACKBOARD);
		graphics2D.drawRectCentered(posX, posY - 0.15f, highscoreWith, highscoreHeight);
		graphics.bindTexture(null);
		graphics.flush();

		int[] scores;
		Texture firstPic;
		Texture secondPic;
		Texture thirdPic;
		if (position == LEFT) {
			scores = scoresEasy;

			// Get Highscore Pictures
			firstPic = easyWinners.get(0);
			secondPic = easyWinners.get(1);
			thirdPic = easyWinners.get(2);
		} else if (position == CENTER) {
			scores = scoresMedium;

			firstPic = mediumWinners.get(0);
			secondPic = mediumWinners.get(1);
			thirdPic = mediumWinners.get(2);
		} else {
			scores = scoresHard;

			firstPic = hardWinners.get(0);
			secondPic = hardWinners.get(1);
			thirdPic = hardWinners.get(2);
		}

		// Draw highscore portraits
		float dimX = 0.25f;
		float dimY = 0.35f;
		final float cl = 0.45f;
		if(firstPic!=null) {
			graphics.bindTexture(firstPic);
			graphics2D.drawRectCentered(posX - 0.3f, posY + 0.1f, dimX, dimY);
			if(firstPic!=StandardTextures.NO_ED) {
				graphics2D.setColor(cl);
				graphics.bindTexture(StandardTextures.PHOTO_FRAME);
				graphics2D.drawRectCentered(posX - 0.3f, posY + 0.1f, dimX*1.15f, dimY*1.1f);
				graphics2D.setColor(1);
			}
		}
		if(secondPic!=null) {
			graphics.bindTexture(secondPic);
			graphics2D.drawRectCentered(posX - 0.3f, posY - 0.3f, dimX, dimY);
			if(secondPic!=StandardTextures.NO_ED) {
				graphics2D.setColor(cl);
				graphics.bindTexture(StandardTextures.PHOTO_FRAME);
				graphics2D.drawRectCentered(posX - 0.3f, posY - 0.3f, dimX*1.15f, dimY*1.1f);
				graphics2D.setColor(1);
			}
		}
		if(thirdPic!=null) {
			graphics.bindTexture(thirdPic);
			graphics2D.drawRectCentered(posX - 0.3f, posY - 0.7f, dimX, dimY);
			if(thirdPic!=StandardTextures.NO_ED) {
				graphics2D.setColor(cl);
				graphics.bindTexture(StandardTextures.PHOTO_FRAME);
				graphics2D.drawRectCentered(posX - 0.3f, posY - 0.7f, dimX*1.15f, dimY*1.1f);
				graphics2D.setColor(1);
			}
			graphics.bindTexture(null);
		}

		// Write highscores
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_CHALK);
		graphics2D.setShaderProgram(StandardTextures.CHALK_SHADER);
		graphics2D.drawStringL(posX - 0.75f, posY - 0.05f, 0.23f, "1. ");
		graphics2D.drawStringL(posX - 0.75f, posY - 0.45f, 0.23f, "2. ");
		graphics2D.drawStringL(posX - 0.75f, posY - 0.85f, 0.23f, "3. ");
		if(firstPic!=null)
			graphics2D.drawStringR(posX + 0.75f, posY - 0.05f, 0.23f, "" + scores[0] + "m");
		if(secondPic!=null)
			graphics2D.drawStringR(posX + 0.75f, posY - 0.45f, 0.23f, "" + scores[1] + "m");
		if(thirdPic!=null)
			graphics2D.drawStringR(posX + 0.75f, posY - 0.85f, 0.23f, "" + scores[2] + "m");
		graphics.bindTexture(null);
		graphics2D.setDefaultProgram();

		// Write blackboard title
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
		if (activeLevel == position) {
			graphics2D.setColorWeighted(TITLE_COLOR1, TITLE_COLOR2, blinkValue);
			graphics2D.drawString(posX, posY + 0.5f, 0.2f + pulse(HELP_FREQUENCY, HELP_INTENSITY), 0, 0, 0, title);
		} else {
			graphics2D.setColor(0.8f, 0.8f, 0.8f);
			graphics2D.drawString(posX, posY + 0.5f, 0.2f, 0, 0, 0, title);
		}
		graphics.bindTexture(null);
	}

	private void showBendingHint() {
		if (programController.markWarning)
			return;
		if (showArrows && !waitedLongEnough && !startLevel) {
			graphics2D.setColorWeighted(HELP_COLOR1, HELP_COLOR2, blinkValue);

			float a = PI / 2 * 0.27f + pulse(HELP_FREQUENCY, 0.13f);
			float r = 1.5f;
			float angleOffset = -0.3f;
			float dX = (float) Math.sin(a) * r;
			float y = -r + 1.1f + player.posY + (float) Math.cos(a) * r;
			graphics.bindTexture(StandardTextures.ARROW_L);
			graphics2D.drawRectCentered(player.getCenterX() - dX, y, 0.6f, 0.32f, a * 1.01f + angleOffset);
			graphics.bindTexture(StandardTextures.ARROW_R);
			graphics2D.drawRectCentered(player.getCenterX() + dX, y, 0.6f, 0.32f, -a * 1.01f - angleOffset);
			graphics.bindTexture(null);
		}
	}

	private void dontLeaveScreen() {
		// Set player back if he walks out
		if (player.posX < -3.1f || player.posX > 3.5f)
			player.posX = oldPlayerPosX;
	}

	private void updateActiveLevel() {

		int lastLevel = activeLevel;
		activeLevel = NONE;

		float playerLeft = player.posX - 0.4f;
		float playerRight = player.posX;

		// Check if we're to the left of the center Stool
		if (playerRight < stoolCx - stoolWidth / 2) {
			// Then check with left Stool only
			if (playerRight > stoolLx - stoolWidth / 2 && playerLeft < stoolLx + stoolWidth / 2)
				activeLevel = LEFT;
		}
		// Check if we're to the right of the center Stool
		else if (playerLeft > stoolCx + stoolWidth / 2) {
			// Then check with right Stool only
			if (playerRight > stoolRx - stoolWidth / 2 && playerLeft < stoolRx + stoolWidth / 2)
				activeLevel = RIGHT;
		} else {
			activeLevel = CENTER;
		}

		if (lastLevel == NONE && activeLevel != NONE)
			activationTime = programController.getProgramTime();
	}

	@Override
	public void onBend(float bending) {
		if (!startLevel) {
			player.steeredBending = bending;
			if (true || Math.abs(player.steeredBending + player.drunkenBending) > 0.1f) {
				player.setSpeedX((float) ((player.steeredBending + player.drunkenBending) / (Math.PI / 4.0) / 2.0) * SPEED_FACTOR);
			} else {
				player.setSpeedX(0);
			}
		} else {
			player.steeredBending = 0;
			player.setSpeedX(0);
		}
	}

	@Override
	public void onDrink() {
		// Drink - enter level
		if (activeLevel != NONE && !startLevel) {
			// set difficulty in gamesettings!
			super.gameSettings.difficulty = activeLevel;
			drinkTime = programController.getProgramTime();
			startLevel = true;
			player.setDrinking(); // release arms and head
			skeleton.mBottleAutoAngle = false;
		}
	}

	@Override
	public int getType() {
		return super.MENU;
	}

	@Override
	public void startGraphics() {
		// Set Highscore Pictures
		easyWinners = setHighscorePictures(LEFT);
		mediumWinners = setHighscorePictures(CENTER);
		hardWinners = setHighscorePictures(RIGHT);
		shadowPlayer.getSkeleton().loadTexture();
	}

	private ArrayList<Texture> setHighscorePictures(int level) {
		ArrayList<Texture> textures = new ArrayList<Texture>();
		if (highscores.getPictureFromPos(level, 0) != null)
			textures.add(0, graphics.createTexture(highscores.getPictureFromPos(level, 0), 80, 125, new TextureSettings()));
		else
			textures.add(0, getDefaultTexture(level, 0));
		if (highscores.getPictureFromPos(level, 1) != null)
			textures.add(1, graphics.createTexture(highscores.getPictureFromPos(level, 1), 80, 125, new TextureSettings()));
		else
			textures.add(1, getDefaultTexture(level, 1));
		if (highscores.getPictureFromPos(level, 2) != null)
			textures.add(2, graphics.createTexture(highscores.getPictureFromPos(level, 2), 80, 125, new TextureSettings()));
		else
			textures.add(2, getDefaultTexture(level, 2));

		return textures;
	}

	private Texture getDefaultTexture(int level, int place) {
		int[] currentScores = scoresHard;
		if (level == LEFT)
			currentScores = scoresEasy;
		else if (level == CENTER)
			currentScores = scoresMedium;

		if (currentScores[place] == 0)
			return null;
		else
			return StandardTextures.NO_ED;
	}

}
