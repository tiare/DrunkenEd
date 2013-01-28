package control.states;

import figure.DrunkenSkeleton;
import figure.Player;
import graphics.StandardTextures;
import graphics.events.Keys;
import graphics.skeletons.Skeleton;
import graphics.skeletons.elements.Bone;
import graphics.skeletons.elements.Joint;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;

import java.util.ArrayList;
import java.util.LinkedList;

import control.ProgramController;

public class MainMenuState extends WorldState {
	
	public static final int NONE = -1, LEFT = 0, CENTER = 1, RIGHT = 2;
	public static final float SPEED_FACTOR = 3.5f;
	
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
	private float timeout = 4.f;
	private boolean startLevel;
	
	private int[] scoresEasy;
	private int[] scoresMedium;
	private int[] scoresHard;
	
	//Highscore pictures
	ArrayList<Texture> easyWinners;
	ArrayList<Texture> mediumWinners;
	ArrayList<Texture> hardWinners;
	
	private Player shadowPlayer;
	private DrunkenSkeleton shadowSkeleton;
	private DrunkenSkeleton skeleton;
	private float elbowAngle = 0;
	private float shoulderAngle = 0;
	private float stepAngle = 160;
	private float activationTime = 0;
	private float hintTimeout = 5.f;
	private boolean waitedLongEnough = false;
	
	private static final String DEFAULT_TEXT = "Choose your drink";
	private static final String DRINK_TEXT = "Drink to start!";
	
	@Override
	public MainMenuState init(ProgramController programController) {
		this.programController = programController;
		super.init(programController);
		drinkTime = programController.getProgramTime();
		player.posX = -0.8f;
		startLevel = false;
		player.inGame = false;
		
		//Get Highscores
		scoresEasy = highscores.highscoresEasy;
		scoresMedium = highscores.highscoresMedium;
		scoresHard = highscores.highscoresHard;
		
		shadowPlayer = new Player(false);
		shadowPlayer.init(programController);
		shadowSkeleton = (DrunkenSkeleton)shadowPlayer.getSkeleton();
		
		player.setArmAnglesByTracking(true);
		skeleton = (DrunkenSkeleton)player.getSkeleton();
		
		return this;
	}
	
	@Override
	public void onStep(float deltaTime) {
		synchronized(camera) {
			//camera.set(0, 1, 2);
			camera.set(player.posX, 1.7f, 2.3f);
			oldPlayerPosX = player.posX;
			
			if (startLevel) {
				if (programController.getProgramTime() > drinkTime + timeout) {
					//start game
					programController.fadeToState(new GameState());
				}
				else {
					doDrinkingGesture(false);
				}
			}
			else {
				player.step(deltaTime);
			}
			
			//don't let the player walk out of the screen
			dontLeaveScreen ();
			updateActiveLevel ();
			
			if (activeLevel != NONE && programController.getProgramTime() > activationTime+hintTimeout)
				waitedLongEnough = true;
			else if (waitedLongEnough) {
				waitedLongEnough = false;
				prepareForDrinkingGesture(true);
			}
			
			shadowPlayer.posX = player.posX;
			shadowPlayer.posY = player.posY;
			updateShadowPosition();
			
			if (waitedLongEnough && !startLevel) {
				doDrinkingGesture (true);
			}
		}
	}
	
	private void updateShadowPosition() {
		LinkedList<Joint> joints = skeleton.mJoints;
		LinkedList<Joint> shadowJoints = shadowSkeleton.mJoints;
		Joint currentShadowJoint;
		Joint currentJoint;
		
		for (int i = 0; i < joints.size()-1; i++) {
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
	
	private void prepareForDrinkingGesture (boolean hint) {
		if (hint) {
			elbowAngle = 0;
			shoulderAngle = 0;
		}
		else {
			elbowAngle = (float)Math.toDegrees(skeleton.mRightHandJoint.getParentAngle());
			shoulderAngle = (float)Math.toDegrees(skeleton.mRightElbowJoint.getParentAngle());
		}
	}
	
	private void doDrinkingGesture (boolean hint) {
		if (hint) {
			if (elbowAngle > 160 && shoulderAngle > 60)
				stepAngle *= -1;
			
			if (elbowAngle < 90 || shoulderAngle < 30) {
				elbowAngle = 90;
				shoulderAngle = 30;
				stepAngle *= -1;
			}
			elbowAngle += 160/stepAngle;
			shoulderAngle += 60/stepAngle;
			shadowSkeleton.mRightElbowJoint.setPosByAngle((float)Math.toRadians(shoulderAngle));
			shadowSkeleton.mRightHandJoint.setPosByAngle((float)Math.toRadians(elbowAngle));
			
			shadowSkeleton.setModColor(0.f, 0.f, 0.f, 0.9f);
			shadowSkeleton.setAddColor(0.1f, 0.1f, 0.15f);
			shadowSkeleton.mDrawContour = false;
			shadowSkeleton.mRightLowerArmBone.mVisible = true;
			shadowSkeleton.mRightUpperArmBone.mVisible = true;
			
			shadowSkeleton.refreshBottle();
		}
		else {
			if (elbowAngle < 160) {
				elbowAngle += 160/stepAngle;
			}
			else if (elbowAngle > 165) {
				elbowAngle -= 160/stepAngle;
			}
			
			if (shoulderAngle < 60) {				
				shoulderAngle += 60/stepAngle;	
			}
			else if (shoulderAngle > 65) {
				shoulderAngle -= 60/stepAngle;
			}
			
			skeleton.mRightElbowJoint.setPosByAngle((float)Math.toRadians(shoulderAngle));
			skeleton.mRightHandJoint.setPosByAngle((float)Math.toRadians(elbowAngle));
			
			skeleton.refreshBottle();
		}
	}

	@Override
	public void onDraw() {
		synchronized(camera) {
			super.drawBackground(1.6f,0);
			graphics.bindTexture(null);
			
			//floor
			graphics2D.setColor(0.5f, 0.5f, 0.5f);
			graphics2D.drawRectCentered(0,-5.0f, 20,10.0f, 0);
			graphics2D.setColor(0.7f, 0.7f, 0.7f);
			graphics2D.drawRectCentered(0,-0.1f, 20,0.2f, 0);
			
			//Draw back wall
			graphics2D.setColor(0.4f, 0.3f, 0.2f);
			graphics2D.drawRectCentered(0, 2.f, 8 ,4.0f);
	
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics.bindTexture(StandardTextures.WALL);
			graphics2D.drawRectCentered(-2, 0.65f, 4, 1.25f);
			graphics2D.drawRectCentered( 2, 0.65f, 4, 1.25f);
			graphics.bindTexture(null);
			
			//Draw stuff on walls
			graphics.bindTexture(StandardTextures.DART);
			graphics2D.drawRectCentered(-3.4f, 1.8f, 0.7f, 0.7f);
			graphics.bindTexture(StandardTextures.FLAG1);
			graphics2D.drawRectCentered(-3.3f, 2.9f, 0.5f, 0.5f);
			graphics.bindTexture(StandardTextures.FLAG2);
			graphics2D.drawRectCentered(-3.5f, 3.2f, 0.6f, 0.4f);
			graphics.bindTexture(StandardTextures.PICTURE1);
			graphics2D.drawRectCentered(3.5f, 2.5f, 0.35f, 0.5f);
			graphics.bindTexture(StandardTextures.PICTURE2);
			graphics2D.drawRectCentered(3.2f, 3.f,0.45f, 0.45f);
			graphics.bindTexture(StandardTextures.PICTURE3);
			graphics2D.drawRectCentered(3.3f, 1.7f, 0.5f, 0.4f);
			
			graphics.bindTexture(null);
			
			//Draw side walls
			graphics.bindTexture(StandardTextures.BRICK_LEFT);
			graphics2D.drawRectCentered(-4.f,1.9f, 0.3f,4.22f, 0);
			graphics.bindTexture(StandardTextures.BRICK_RIGHT);
			graphics2D.drawRectCentered( 4.5f,1.9f, 1.2f,4.22f, 0);
			graphics.bindTexture(null);
			
			//drawBar
			graphics.bindTexture(StandardTextures.TAP);
			graphics2D.drawRectCentered(0.7f, 1.44f, 0.2f, 0.3f);
			graphics.bindTexture(StandardTextures.BAR);
			graphics2D.drawRectCentered(barPosX, barPosY, barWidth, barHeight);
			graphics.bindTexture(null);
			
			drawHighscores(stoolLx, highscoresY, LEFT, "Beer", "Easy");
			drawHighscores(stoolCx, highscoresY, CENTER, "Wine", "Medium");
			drawHighscores(stoolRx, highscoresY, RIGHT, "Vodka", "Hard");
			
			drawStool(stoolLx, stoolsY, (activeLevel == 0), LEFT);
			drawStool(stoolCx, stoolsY, (activeLevel == 1), CENTER);
			drawStool(stoolRx, stoolsY, (activeLevel == 2), RIGHT);
			graphics2D.setColor(1.f, 1.f, 1.f);
			
			//Display bottom text
			graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
			graphics2D.switchGameCoordinates(false);
			if (waitedLongEnough && !startLevel) {
				graphics2D.setColor(1.f, 1.f, (float)Math.abs(Math.sin(stateTimer*3)));
				graphics2D.drawString(0, -0.9f, 0.13f, 0, 0, 0, DRINK_TEXT);
			}
		
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics2D.drawString(0, 0.92f, 0.13f, 0, 0, 0, DEFAULT_TEXT);
			graphics2D.switchGameCoordinates(true);
			graphics.bindTexture(null);
			
			player.skeleton.setDrinkId(activeLevel);
			shadowPlayer.skeleton.setDrinkId(activeLevel);
			shadowPlayer.draw();
			
			graphics2D.setWhite();
			player.draw();
		}
	}
	
	private void drawStool (float posX, float posY, boolean active, int drink) {
		if(!active) {
			float drinkY = (drink > 0? posY + 0.96f: posY + 0.79f);
			//display the drinks
			graphics2D.setColor(1.f, 1.f, 1.f);
			graphics.bindTexture(StandardTextures.ED_SKELETON);
			graphics2D.drawRectCentered(posX, drinkY,3.5f,3.5f, 0,
					skeleton.mBottleBone.mTexCoords.get(drink * 4));
		}
		
		if (active) {
			graphics2D.setColor(1.f, 1.f, 1.f);
		}
		else {
			graphics2D.setColor(0.4f, 0.4f, 0.4f);
		}
		graphics.bindTexture(StandardTextures.STOOL);
		graphics2D.drawRectCentered(posX, posY-0.15f, stoolWidth/1.5f, stoolHeight-0.3f);
		graphics.bindTexture(null);
	}
	
	private void drawHighscores (float posX, float posY, int position, String title, String altTitle) {
		if (position == activeLevel) {
			graphics2D.setColor(1.f, 1.f, 1.f);
		}
		else {
			graphics2D.setColor(0.6f, 0.6f, 0.6f);
		}
		
		graphics.bindTexture(StandardTextures.BLACKBOARD);
		graphics2D.drawRectCentered(posX, posY-0.15f, highscoreWith, highscoreHeight);
		graphics.bindTexture(null);
		graphics.flush();
		
		int[] scores;
		Texture firstPic;
		Texture secondPic;
		Texture thirdPic;
		if (position == LEFT) {
			scores = scoresEasy;
			
			//Get Highscore Pictures
			firstPic = easyWinners.get(0);
			secondPic = easyWinners.get(1);
			thirdPic = easyWinners.get(2);
		}
		else if (position == CENTER) {
			scores = scoresMedium;
			
			firstPic = mediumWinners.get(0);
			secondPic = mediumWinners.get(1);
			thirdPic = mediumWinners.get(2);
		}
		else {
			scores = scoresHard;
			
			firstPic = hardWinners.get(0);
			secondPic = hardWinners.get(1);
			thirdPic = hardWinners.get(2);
		}
		
		//Draw highscore portraits
		graphics.bindTexture(firstPic);
		graphics2D.drawRectCentered(posX-0.3f, posY+0.1f, 0.3f, 0.35f);
		graphics.bindTexture(secondPic);
		graphics2D.drawRectCentered(posX-0.3f, posY-0.3f, 0.3f, 0.35f);
		graphics.bindTexture(thirdPic);
		graphics2D.drawRectCentered(posX-0.3f, posY-0.7f, 0.3f, 0.35f);
		graphics.bindTexture(null);
		
		//Write highscores
		//graphics2D.setDefaultFont();
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_CHALK);
		graphics2D.setShaderProgram(StandardTextures.CHALK_SHADER);
		//graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawStringL(posX-0.75f, posY-0.05f, 0.23f, "1. ");
		graphics2D.drawStringL(posX-0.75f, posY-0.45f, 0.23f, "2. ");
		graphics2D.drawStringL(posX-0.75f, posY-0.85f, 0.23f, "3. ");
		graphics2D.drawStringR(posX+0.75f, posY-0.05f, 0.23f, ""+scores[0]);
		graphics2D.drawStringR(posX+0.75f, posY-0.45f, 0.23f, ""+scores[1]);
		graphics2D.drawStringR(posX+0.75f, posY-0.85f, 0.23f, ""+scores[2]);
		graphics.bindTexture(null);
		graphics2D.setDefaultProgram();
		
		//Write blackboard title
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
		if (activeLevel == position) {
			graphics2D.setColor(1.f, 0.f, 0.f);//graphics2D.setColor(0.8f, 0.2f, 0.2f);
			graphics2D.drawString(posX, posY+0.5f, 0.2f, 0, 0, 0, altTitle);
		}
		else {
			graphics2D.setColor(0.8f, 0.8f, 0.8f);
			graphics2D.drawString(posX, posY+0.5f, 0.2f, 0, 0, 0, title);
		}
		graphics.bindTexture(null);
	}
	
	private void dontLeaveScreen () {
		//Set player back if he walks out
		if ( player.posX < - 3.1f || player.posX > 3.5f )
			player.posX = oldPlayerPosX;
	}
	
	private void updateActiveLevel () {
		
		int lastLevel = activeLevel;
		activeLevel = NONE;
		
		float playerLeft = player.posX-0.4f;
		float playerRight = player.posX;
		
		//Check if we're to the left of the center Stool
		if (playerRight < stoolCx-stoolWidth/2) {
			//Then check with left Stool only
			if (playerRight > stoolLx-stoolWidth/2 && playerLeft < stoolLx + stoolWidth/2)
				activeLevel = LEFT;
		}
		//Check if we're to the right of the center Stool
		else if (playerLeft > stoolCx+stoolWidth/2) {
			//Then check with right Stool only
			if (playerRight > stoolRx-stoolWidth/2 && playerLeft < stoolRx + stoolWidth/2)
				activeLevel = RIGHT;
		}
		else {
			activeLevel = CENTER;
		}
		
		if (lastLevel == NONE && activeLevel != NONE)
			activationTime = programController.getProgramTime();
	}

	@Override
	public void onBend(float bending){
		if (!startLevel) {
			player.steeredBending = bending;
			if(true || Math.abs(player.steeredBending+player.drunkenBending)>0.1f) {
				player.setSpeedX( (float)((player.steeredBending + player.drunkenBending) / (Math.PI/4.0) / 2.0) * SPEED_FACTOR );
			}else
				player.setSpeedX(0);
		}
	}
	
	@Override
	public void keyDown(int key) {
		//Select drink - enter level
		if( key == Keys.UP) {
			//Enter level
			if (activeLevel != NONE && !startLevel) {
				//set difficulty in gamesettings!
				super.gameSettings.difficulty = activeLevel;
				drinkTime = programController.getProgramTime();
				startLevel = true;
				prepareForDrinkingGesture(false);
			}
		}
	}
	
	@Override
	public void onDrink() {
		//Drink - enter level
		if (activeLevel != NONE && !startLevel) {
			//set difficulty in gamesettings!
			super.gameSettings.difficulty = activeLevel;
			drinkTime = programController.getProgramTime();
			startLevel = true;
			prepareForDrinkingGesture(false);
		}
	}
	
	@Override
	public int getType() {
		return super.MENU;
	}

	@Override
	public void userLost() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void startGraphics() {
		//Set Highscore Pictures
		easyWinners = setHighscorePictures (LEFT);
		mediumWinners = setHighscorePictures (CENTER);
		hardWinners = setHighscorePictures (RIGHT);
	}
	
	private ArrayList<Texture> setHighscorePictures (int level) {
		ArrayList<Texture> textures = new ArrayList<Texture>();
		if (highscores.getPictureFromPos(level, 0) != null)
			textures.add(0, graphics.createTexture(highscores.getPictureFromPos(level, 0), 80, 125, new TextureSettings()));
		else textures.add(0,getDefaultTexture (level, 0));
		if (highscores.getPictureFromPos(level, 1) != null)
			textures.add(1, graphics.createTexture(highscores.getPictureFromPos(level,1), 80, 125, new TextureSettings()));
		else textures.add(1, getDefaultTexture (level, 1));
		if (highscores.getPictureFromPos(level, 2) != null)
			textures.add(2, graphics.createTexture(highscores.getPictureFromPos(level, 2), 80, 125, new TextureSettings()));
		else textures.add(2, getDefaultTexture (level, 2));
		
		return textures;
	}
	private Texture getDefaultTexture (int level, int place) {
		int[] currentScores = scoresHard;
		if (level == LEFT) currentScores = scoresEasy;
		else if (level == CENTER) currentScores = scoresMedium;
		
		if (currentScores[place] == 0) return StandardTextures.NO_ED;
		else return StandardTextures.ED;
	}

}
