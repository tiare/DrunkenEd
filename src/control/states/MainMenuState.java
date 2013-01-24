package control.states;

import java.util.LinkedList;

import figure.DrunkenSkeleton;
import figure.Player;
import graphics.StandardTextures;
import graphics.events.Keys;
import graphics.skeletons.elements.Bone;
import graphics.skeletons.elements.Joint;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;
import control.ProgramController;

public class MainMenuState extends WorldState {
	
	public static final int NONE = -1, LEFT = 0, CENTER = 1, RIGHT = 2;
	private int activeDoor = NONE;
	private float doorWith = 1.f;
	private float doorHeight = 1.2f;
	private float doorLx = -2.f;
	private float doorCx = 0.f;
	private float doorRx = 2.f;
	private float doorsY = 0.6f;
	private float highscoresY = 3.1f;
	private float highscoreWith = 1.8f;
	private float highscoreHeight = 1.5f;
	private float oldPlayerPosX = 0f;
	
	private float restartTime = 0.f;
	private float timeout = 0.f;
	private boolean startLevel;
	
	private int[] scoresEasy;
	private int[] scoresMedium;
	private int[] scoresHard;
	
	//highscore pictures
	private Texture easy1;
	private Texture easy2;
	private Texture easy3;
	private Texture medium1;
	private Texture medium2;
	private Texture medium3;
	private Texture hard1;
	private Texture hard2;
	private Texture hard3;
	
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
		camera.set(skeleton.mHipJoint.mPosX+player.posX, 1.5f, 2.3f);
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
		updateActiveDoor ();
		updateHintText ();
		
		if (activeDoor != NONE && programController.getProgramTime() > activationTime+hintTimeout)
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
		
		shadowSkeleton.setModColor(0.f, 0.f, 0.f, 1.f);
		shadowSkeleton.setAddColor(0.1f, 0.1f, 0.15f);
		shadowSkeleton.mDrawContour = false;
		shadowSkeleton.mRightLowerArmBone.mVisible = true;
		shadowSkeleton.mRightUpperArmBone.mVisible = true;
	}
	
	private void updateHintText () {
		if (activeDoor != NONE) {
			hintText = "Drink to select difficulty!";
		}
		else {
			hintText = "What would you like to drink?";
		}
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		
		drawHighscores(doorLx, highscoresY, LEFT);
		drawHighscores(doorCx, highscoresY, CENTER);
		drawHighscores(doorRx, highscoresY, RIGHT);
		
		drawDoor(doorLx, doorsY, (activeDoor == 0));
		drawDoor(doorCx, doorsY, (activeDoor == 1));
		drawDoor(doorRx, doorsY, (activeDoor == 2));
		
		//floor
		graphics2D.setColor(0.5f, 0.5f, 0.5f);
		graphics2D.drawRectCentered(0,-5.0f, 20,10.0f, 0);
		graphics2D.setColor(0.7f, 0.7f, 0.7f);
		graphics2D.drawRectCentered(0,-0.1f, 20,0.2f, 0);
		
		// draw left tree
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(-3.5f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(-3.5f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(-3.5f,2.5f, 1.0f,1.0f, (float)Math.PI/4.0f);
		graphics2D.drawRectCentered(-3.5f,2.5f, 1.0f,1.0f, (float)Math.PI/7.0f);
		// draw right tree
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(3.5f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(3.5f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(3.5f,2.5f, 1.0f,1.0f, (float)Math.PI/2.0f);
		graphics2D.drawRectCentered(3.5f,2.5f, 1.0f,1.0f, (float)Math.PI/5.0f);
		
		//draw drinks
		graphics2D.setWhite();
		graphics.bindTexture(StandardTextures.BEER);
		graphics2D.drawRectCentered(doorLx, doorsY+0.8f,0.38f,0.4f);
		graphics.bindTexture(StandardTextures.WINE);
		graphics2D.drawRectCentered(doorCx, doorsY+0.8f,0.38f,0.6f);
		graphics.bindTexture(StandardTextures.VODKA);
		graphics2D.drawRectCentered(doorRx, doorsY+0.8f,0.3f,0.7f);
		graphics.bindTexture(null);
		
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
		if (activeDoor == 0) graphics2D.setColor(1.f, 0.f, 0.f);
		else graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(doorLx, highscoresY+0.45f, 0.2f, 0, 0, 0, "Beer");
		
		if (activeDoor == 1) graphics2D.setColor(1.f, 0.f, 0.f);
		else graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(doorCx, highscoresY+0.45f, 0.2f, 0, 0, 0, "Wine");
		
		if (activeDoor == 2) graphics2D.setColor(1.f, 0.f, 0.f);
		else graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(doorRx, highscoresY+0.45f, 0.2f, 0, 0, 0, "Vodka");
		
		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(0, -0.4f, 0.3f, 0, 0, 0, hintText);
		graphics.bindTexture(null);
		
		shadowPlayer.draw();
		
		graphics2D.setWhite();
		player.draw();
	}
	
	private void drawDoor (float posX, float posY, boolean active) {
		if (active) {
			graphics2D.setColor(0.2f, 0.2f, 0.4f);
		}
		else {
			graphics2D.setColor(0.6f, 0.6f, 0.8f);
		}
		graphics2D.drawRectCentered(posX, posY, doorWith, doorHeight);
	}
	
	private void drawHighscores (float posX, float posY, int position) {
		if (position == activeDoor) {
			graphics2D.setColor(0.2f, 0.2f, 0.4f);
		}
		else {
			graphics2D.setColor(0.6f, 0.6f, 0.8f);
		}
		graphics.bindTexture(null);
		graphics2D.drawRectCentered(posX, posY-0.15f, highscoreWith, highscoreHeight);
		graphics.flush();
		
		int[] scores;
		Texture firstPic;
		Texture secondPic;
		Texture thirdPic;
		if (position == LEFT) {
			scores = scoresEasy;
			
			//Get Highscore Pictures
			firstPic = easy1;
			secondPic = easy2;
			thirdPic = easy3;
		}
		else if (position == CENTER) {
			scores = scoresMedium;
			
			firstPic = medium1;
			secondPic = medium2;
			thirdPic = medium3;
		}
		else {
			scores = scoresHard;
			
			firstPic = hard1;
			secondPic = hard2;
			thirdPic = hard3;
		}
		
		//Write highscores
		graphics2D.setDefaultFont();
		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawStringL(posX-0.7f, posY+0.f, 0.23f, "1. ");
		graphics2D.drawStringL(posX-0.7f, posY-0.4f, 0.23f, "2. ");
		graphics2D.drawStringL(posX-0.7f, posY-0.8f, 0.23f, "3. ");
		graphics2D.drawStringR(posX+0.8f, posY+0.f, 0.23f, ""+scores[0]);
		graphics2D.drawStringR(posX+0.8f, posY-0.4f, 0.23f, ""+scores[1]);
		graphics2D.drawStringR(posX+0.8f, posY-0.8f, 0.23f, ""+scores[2]);
		graphics.bindTexture(null);
		
		//Draw highscore portraits
		graphics.bindTexture(firstPic);
		graphics2D.drawRectCentered(posX-0.3f, posY+0.15f, 0.3f, 0.35f);
		graphics.bindTexture(secondPic);
		graphics2D.drawRectCentered(posX-0.3f, posY-0.25f, 0.3f, 0.35f);
		graphics.bindTexture(thirdPic);
		graphics2D.drawRectCentered(posX-0.3f, posY-0.65f, 0.3f, 0.35f);
		graphics.bindTexture(null);

	}
	
	private void dontLeaveScreen () {
		//Set player back if he walks out
		if ( player.posX < -3 || player.posX > 3 )
			player.posX = oldPlayerPosX;
	}
	
	private void updateActiveDoor () {
		
		int lastDoor = activeDoor;
		activeDoor = NONE;
		
		float playerLeft = player.posX-0.4f;
		float playerRight = player.posX;
		
		//Check if we're to the left of the center door
		if (playerRight < doorCx-doorWith/2) {
			//Then check with left door only
			if (playerRight > doorLx-doorWith/2 && playerLeft < doorLx + doorWith/2)
				activeDoor = LEFT;
		}
		//Check if we're to the right of the center door
		else if (playerLeft > doorCx+doorWith/2) {
			//Then check with right door only
			if (playerRight > doorRx-doorWith/2 && playerLeft < doorRx + doorWith/2)
				activeDoor = RIGHT;
		}
		else {
			activeDoor = CENTER;
		}
		
		if (lastDoor == NONE && activeDoor != NONE)
			activationTime = programController.getProgramTime();
	}

	@Override
	public void keyDown(int key) {
		//Enter the selected door
		if( key == Keys.UP ) {
			//Enter level
			if (activeDoor != NONE) {
				restartTime = programController.getProgramTime();
				startLevel = true;
				
				//set difficulty in gamesettings!
				super.gameSettings.difficulty = activeDoor;			
			}
		}
	}
	
	@Override
	public void onDrink() {
		//Enter the selected door
		//Enter level
		if (activeDoor != NONE && programController.getProgramTime() > restartTime + timeout) {
			//set difficulty in gamesettings!
			super.gameSettings.difficulty = activeDoor;			
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
		//Get Highscore Pictures
		if (highscores.getPictureFromPos(LEFT, 0) != null)
			easy1 = graphics.createTexture(highscores.getPictureFromPos(LEFT, 0), 60, 100, new TextureSettings());
		else easy1 = StandardTextures.ED;
		if (highscores.getPictureFromPos(LEFT, 1) != null)
			easy2 = graphics.createTexture(highscores.getPictureFromPos(LEFT, 1), 60, 100, new TextureSettings());
		else easy2 = StandardTextures.ED;
		if (highscores.getPictureFromPos(LEFT, 2) != null)
			easy3 = graphics.createTexture(highscores.getPictureFromPos(LEFT, 2), 60, 100, new TextureSettings());
		else easy3 = StandardTextures.ED;
		
		if(highscores.getPictureFromPos(CENTER, 0) != null)
			medium1 = graphics.createTexture(highscores.getPictureFromPos(CENTER, 0), 60, 100, new TextureSettings());
		else medium1 = StandardTextures.ED;
		if(highscores.getPictureFromPos(CENTER, 1) != null)	
			medium2 = graphics.createTexture(highscores.getPictureFromPos(CENTER, 1), 60, 100, new TextureSettings());
		else medium2 = StandardTextures.ED;
		if(highscores.getPictureFromPos(CENTER, 2) != null)
			medium3 = graphics.createTexture(highscores.getPictureFromPos(CENTER, 2), 60, 100, new TextureSettings());
		else medium3 = StandardTextures.ED;
		
		if(highscores.getPictureFromPos(RIGHT, 0) != null)
			hard1 = graphics.createTexture(highscores.getPictureFromPos(RIGHT, 0), 60, 100, new TextureSettings());
		else hard1 = StandardTextures.ED;
		if(highscores.getPictureFromPos(RIGHT, 1) != null)
			hard2 = graphics.createTexture(highscores.getPictureFromPos(RIGHT, 1), 60, 100, new TextureSettings());
		else hard2 = StandardTextures.ED;
		if(highscores.getPictureFromPos(RIGHT, 2) != null)
			hard3 = graphics.createTexture(highscores.getPictureFromPos(RIGHT, 2), 60, 100, new TextureSettings());
		else hard3 = StandardTextures.ED;
	}

}
