package control.states;

import java.nio.ByteBuffer;

import org.OpenNI.SkeletonJoint;

import control.ProgramController;
import figure.DrunkenSkeleton;
import figure.Player;
import graphics.StandardTextures;
import graphics.events.Keys;
import graphics.skeletons.Skeleton;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;

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
	private float timeout = 1.f;
	private boolean startLevel;
	
	private int[] scoresEasy;
	private int[] scoresMedium;
	private int[] scoresHard;
	
	private Player shadowPlayer;
	private float elbowAngle = 0.0f;
	private float shoulderAngle = 0.0f;
	private float stepAngle = 80;
	private float activationTime = 0;
	private boolean waitedLongEnough = false;
	
	@Override
	public MainMenuState init(ProgramController programController) {
		this.programController = programController;
		super.init(programController);
		restartTime = programController.getProgramTime();
		player.posX = -1;
		startLevel = false;
		
		
		//Get Highscores
		scoresEasy = highscores.highscoresEasy;
		scoresMedium = highscores.highscoresMedium;
		scoresHard = highscores.highscoresHard;
		
		shadowPlayer = new Player();
		shadowPlayer.init(programController);
		
		player.setArmAnglesByTracking(true);
		
		return this;
	}
	
	@Override
	public void onStep(float deltaTime) {
		//camera.set(0, 1, 2);
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX, 1.5f, 2.3f);
		oldPlayerPosX = player.posX;
		
		if (startLevel) {
			if (programController.getProgramTime() > restartTime + timeout) {
				//start game
				super.programController.switchState(new GameState().init(programController));
			}
		}
		else {
			player.step(deltaTime);
		}
		//don't let the player walk out of the screen
		dontLeaveScreen ();
		updateActiveDoor();
		
//		if (activeDoor != NONE && waitedLongEnough)
//		doDrinkingGesture ((DrunkenSkeleton)shadowPlayer.getSkeleton());
//		shadowPlayer.posX = player.posX;
//		shadowPlayer.posY = player.posY;
	}
	
	private void doDrinkingGesture (DrunkenSkeleton shadowSkeleton) {
		if (elbowAngle > 190 && shoulderAngle > 80 || elbowAngle < 0 && shoulderAngle < 0)
			stepAngle *= -1;
		elbowAngle += 190/stepAngle;
		shoulderAngle += 80/stepAngle;
		shadowSkeleton.mRightHandJoint.setPosByAngle((float)Math.toRadians(elbowAngle));
		shadowSkeleton.mRightElbowJoint.setPosByAngle((float)Math.toRadians(shoulderAngle));
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
		
		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(doorLx, highscoresY+0.5f, 0.28f, 0, 0, 0, "Easy");
		graphics2D.drawString(doorCx, highscoresY+0.5f, 0.28f, 0, 0, 0, "Medium");
		graphics2D.drawString(doorRx, highscoresY+0.5f, 0.28f, 0, 0, 0, "Hard");
		graphics2D.setColor(0.f, 0.f, 0.f);
		graphics2D.drawString(0, -0.3f, 0.3f, 0, 0, 0, "Drink (or press up) to select!");
		graphics.bindTexture(null);
		
//		shadowPlayer.draw();
		
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
		
		int[] scores;
		Texture firstPic = StandardTextures.ED;
		Texture secondPic = StandardTextures.ED;
		Texture thirdPic = StandardTextures.ED;
		if (position == LEFT) {
			scores = scoresEasy;
			
			//Get Highscore Pictures
			if (highscores.getPictureFromPos(LEFT, 0) != null) {
				ByteBuffer bb = highscores.getPictureFromPos(LEFT, 0);
				bb.rewind();
				firstPic = new Texture(graphics, bb, 60,100, new TextureSettings());
//				firstPic = graphics.createTexture(highscores.getPictureFromPos(LEFT, 0), 60, 100, new TextureSettings());
			}
			if (highscores.getPictureFromPos(LEFT, 1) != null) {
				secondPic = graphics.createTexture(highscores.getPictureFromPos(LEFT, 1), 60, 100, new TextureSettings());
			}
			if (highscores.getPictureFromPos(LEFT, 2) != null) {
				thirdPic = graphics.createTexture(highscores.getPictureFromPos(LEFT, 2), 60, 100, new TextureSettings());
			}
		}
		else if (position == CENTER) {
			scores = scoresMedium;
			
			if(highscores.getPictureFromPos(CENTER, 0) != null)
				firstPic = graphics.createTexture(highscores.getPictureFromPos(CENTER, 0), 60, 100, new TextureSettings());
			if(highscores.getPictureFromPos(CENTER, 1) != null)	
				secondPic = graphics.createTexture(highscores.getPictureFromPos(CENTER, 1), 60, 100, new TextureSettings());
			if(highscores.getPictureFromPos(CENTER, 2) != null)
				thirdPic = graphics.createTexture(highscores.getPictureFromPos(CENTER, 2), 60, 100, new TextureSettings());
		}
		else {
			scores = scoresHard;
			
			if(highscores.getPictureFromPos(RIGHT, 0) != null)
				firstPic = graphics.createTexture(highscores.getPictureFromPos(RIGHT, 0), 60, 100, new TextureSettings());
			if(highscores.getPictureFromPos(RIGHT, 1) != null)
				secondPic = graphics.createTexture(highscores.getPictureFromPos(RIGHT, 1), 60, 100, new TextureSettings());
			if(highscores.getPictureFromPos(RIGHT, 2) != null)
				thirdPic = graphics.createTexture(highscores.getPictureFromPos(RIGHT, 2), 60, 100, new TextureSettings());
		}
		
		//Write highscores
		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(posX-0.7f, posY+0.15f, 0.2f, 0, 0, 0, "1. ");
		graphics2D.drawString(posX-0.7f, posY-0.25f, 0.2f, 0, 0, 0, "2. ");
		graphics2D.drawString(posX-0.7f, posY-0.65f, 0.2f, 0, 0, 0, "3. ");
		graphics2D.drawString(posX+0.5f, posY+0.15f, 0.2f, 0, 0, 0, ""+scores[0]);
		graphics2D.drawString(posX+0.5f, posY-0.25f, 0.2f, 0, 0, 0, ""+scores[1]);
		graphics2D.drawString(posX+0.5f, posY-0.65f, 0.2f, 0, 0, 0, ""+scores[2]);
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
		
		activeDoor = NONE;
		
		float playerLeft = player.posX-0.3f;
		float playerRight = player.posX+0.3f;
		
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
//				//start game
//				super.programController.switchState(new GameState().init(programController));
				
				//TODO: trigger player animation!
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

}
