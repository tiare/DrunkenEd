package control.states;

import figure.DrunkenSkeleton;
import figure.Player;
import graphics.StandardTextures;
import graphics.events.Keys;
import graphics.skeletons.elements.Bone;
import graphics.skeletons.elements.Joint;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;

import java.util.ArrayList;
import java.util.LinkedList;

import control.ProgramController;

public class MainMenuState extends WorldState {
	
	public static final int NONE = -1, LEFT = 0, CENTER = 1, RIGHT = 2;
	private int activeLevel = NONE;
	private float barWidth = 6.f;
	private float barHeight = 1.5f;
	private float barPosX = 0.f;
	private float barPosY = 0.45f;
	private float stoolWidth = 1.f;
	private float stoolHeight = 1.2f;
	private float stoolLx = -2.f;
	private float stoolCx = 0.f;
	private float stoolRx = 2.f;
	private float stoolsY = 0.6f;
	private float highscoresY = 3.05f;
	private float highscoreWith = 1.8f;
	private float highscoreHeight = 1.7f;
	private float oldPlayerPosX = 0f;
	
	private float restartTime = 0.f;
	private float timeout = 0.f;
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
	private float elbowAngle = 0.0f;
	private float shoulderAngle = 0.0f;
	private float stepAngle = 80;
	private float activationTime = 0;
	private boolean waitedLongEnough = false;
	private float hintTimeout = 5.f;
	
	private String hintText = "";
	private static final String DEFAULT_TEXT = "What would you like to drink?";
	private static final String DRINK_TEXT = "Drink to select difficulty!";
	
	@Override
	public MainMenuState init(ProgramController programController) {
		this.programController = programController;
		super.init(programController);
		restartTime = programController.getProgramTime();
		player.posX = -0.8f;
		startLevel = false;
		
		
		//Get Highscores
		scoresEasy = highscores.highscoresEasy;
		scoresMedium = highscores.highscoresMedium;
		scoresHard = highscores.highscoresHard;
		
		shadowPlayer = new Player();
		shadowPlayer.init(programController);
		shadowSkeleton = (DrunkenSkeleton)shadowPlayer.getSkeleton();
		
		player.setArmAnglesByTracking(true);
		skeleton = (DrunkenSkeleton)player.getSkeleton();
		
		hintText = DEFAULT_TEXT;
		
		return this;
	}
	
	@Override
	public void onStep(float deltaTime) {
		//camera.set(0, 1, 2);
		camera.set(player.posX, 1.5f, 2.3f);
		oldPlayerPosX = player.posX;
		
		if (startLevel) {
			if (programController.getProgramTime() > restartTime + hintTimeout) {
				//start game
				super.programController.switchState(new GameState().init(programController));
			}
		}
		else {
			player.step(deltaTime);
		}
		//don't let the player walk out of the screen
		dontLeaveScreen ();
		updateActiveLevel ();
		updateHintText ();
		
		if (activeLevel != NONE && programController.getProgramTime() > activationTime+hintTimeout)
			waitedLongEnough = true;
		
		shadowPlayer.posX = player.posX;
		shadowPlayer.posY = player.posY;
		updateShadowPosition();
		
		if (waitedLongEnough)
			doDrinkingGesture ();
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
		
	}
	
	private void doDrinkingGesture () {
		//TODO: make text flash
		if (elbowAngle > 190 && shoulderAngle > 80)
			stepAngle *= -1;
		
		if (elbowAngle < 0 || shoulderAngle < 0) {
			elbowAngle = 0;
			shoulderAngle = 0;
			stepAngle *= -1;
			
			waitedLongEnough = false;
			activationTime = programController.getProgramTime();
		}
		elbowAngle += 190/stepAngle;
		shoulderAngle += 80/stepAngle;
		shadowSkeleton.mRightElbowJoint.setPosByAngle((float)Math.toRadians(shoulderAngle));
		shadowSkeleton.mRightHandJoint.setPosByAngle((float)Math.toRadians(elbowAngle));
		
		shadowSkeleton.setModColor(0.f, 0.f, 0.f, 0.9f);
		shadowSkeleton.setAddColor(0.1f, 0.1f, 0.15f);
		shadowSkeleton.mDrawContour = false;
		shadowSkeleton.mRightLowerArmBone.mVisible = true;
		shadowSkeleton.mRightUpperArmBone.mVisible = true;
	}
	
	private void updateHintText () {
		if (activeLevel != NONE) {
			hintText = DRINK_TEXT;
		}
		else {
			hintText = DEFAULT_TEXT;
		}
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		
		//drawBar
		graphics.bindTexture(StandardTextures.BAR);
		graphics2D.drawRectCentered(barPosX, barPosY, barWidth, barHeight);
		graphics.bindTexture(null);
		
		drawHighscores(stoolLx, highscoresY, LEFT, "Beer");
		drawHighscores(stoolCx, highscoresY, CENTER, "Wine");
		drawHighscores(stoolRx, highscoresY, RIGHT, "Vodka");
		
		drawStool(stoolLx, stoolsY, (activeLevel == 0), StandardTextures.BEER);
		drawStool(stoolCx, stoolsY, (activeLevel == 1), StandardTextures.WINE);
		drawStool(stoolRx, stoolsY, (activeLevel == 2), StandardTextures.VODKA);
		
		//floor
		graphics2D.setColor(0.5f, 0.5f, 0.5f);
		graphics2D.drawRectCentered(0,-5.0f, 20,10.0f, 0);
		graphics2D.setColor(0.7f, 0.7f, 0.7f);
		graphics2D.drawRectCentered(0,-0.1f, 20,0.2f, 0);
		
		//Draw columns
		graphics.bindTexture(StandardTextures.COLUMN);
		//draw left column
		graphics2D.drawRectCentered(-3.9f,1.9f, 0.6f,4.0f, 0);
		graphics2D.drawRectCentered(3.9f,1.9f, 0.6f,4.0f, 0);
		graphics.bindTexture(null);
		
		//Display bottom text
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.switchGameCoordinates(false);
		graphics2D.drawString(0, -0.85f, 0.13f, 0, 0, 0, hintText);
		graphics2D.switchGameCoordinates(true);
		graphics.bindTexture(null);
		
		shadowPlayer.draw();
		
		graphics2D.setWhite();
		player.draw();
	}
	
	private void drawStool (float posX, float posY, boolean active, Texture drink) {
		if (active) {
			graphics2D.setColor(1.f, 1.f, 1.f);
		}
		else {
			graphics2D.setColor(0.4f, 0.4f, 0.4f);
		}
		graphics.bindTexture(drink);
		graphics2D.drawRectCentered(posX, posY+0.68f,0.38f,0.48f);
		
		graphics.bindTexture(StandardTextures.STOOL);
		graphics2D.drawRectCentered(posX, posY-0.15f, stoolWidth/1.5f, stoolHeight-0.3f);
		graphics.bindTexture(null);
	}
	
	private void drawHighscores (float posX, float posY, int position, String title) {
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
		
		//Write highscores
		graphics2D.setDefaultFont();
		//graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawStringL(posX-0.75f, posY-0.05f, 0.23f, "1. ");
		graphics2D.drawStringL(posX-0.75f, posY-0.45f, 0.23f, "2. ");
		graphics2D.drawStringL(posX-0.75f, posY-0.85f, 0.23f, "3. ");
		graphics2D.drawStringR(posX+0.75f, posY-0.05f, 0.23f, ""+scores[0]);
		graphics2D.drawStringR(posX+0.75f, posY-0.45f, 0.23f, ""+scores[1]);
		graphics2D.drawStringR(posX+0.75f, posY-0.85f, 0.23f, ""+scores[2]);
		graphics.bindTexture(null);
		
		//Draw highscore portraits
		graphics.bindTexture(firstPic);
		graphics2D.drawRectCentered(posX-0.4f, posY+0.1f, 0.3f, 0.35f);
		graphics.bindTexture(secondPic);
		graphics2D.drawRectCentered(posX-0.4f, posY-0.3f, 0.3f, 0.35f);
		graphics.bindTexture(thirdPic);
		graphics2D.drawRectCentered(posX-0.4f, posY-0.7f, 0.3f, 0.35f);
		graphics.bindTexture(null);

		
		//Write blackboard title
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
		if (activeLevel == position) graphics2D.setColor(1.f, 0.2f, 0.4f);
		else graphics2D.setColor(0.8f, 0.8f, 0.8f);
		graphics2D.drawString(posX, posY+0.5f, 0.2f, 0, 0, 0, title);
	}
	
	private void dontLeaveScreen () {
		//Set player back if he walks out
		if ( player.posX < - 3 || player.posX > 3.4f )
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
	public void keyDown(int key) {
		//Enter the selected Stool
		if( key == Keys.UP ) {
			//Enter level
			if (activeLevel != NONE) {
				restartTime = programController.getProgramTime();
				startLevel = true;
				
				//set difficulty in gamesettings!
				super.gameSettings.difficulty = activeLevel;			
			}
		}
	}
	
	@Override
	public void onDrink() {
		//Enter the selected Stool
		//Enter level
		if (activeLevel != NONE && programController.getProgramTime() > restartTime + timeout) {
			//set difficulty in gamesettings!
			super.gameSettings.difficulty = activeLevel;			
			//start game
			super.programController.switchState(new GameState().init(programController));
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
			textures.add(0, graphics.createTexture(highscores.getPictureFromPos(level, 0), 60, 100, new TextureSettings()));
		else textures.add(0,getDefaultTexture (level, 0));
		if (highscores.getPictureFromPos(level, 1) != null)
			textures.add(1, graphics.createTexture(highscores.getPictureFromPos(level,1), 60, 100, new TextureSettings()));
		else textures.add(1, getDefaultTexture (level, 1));
		if (highscores.getPictureFromPos(level, 2) != null)
			textures.add(2, graphics.createTexture(highscores.getPictureFromPos(level, 2), 60, 100, new TextureSettings()));
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
