package control.states;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import ninja.game.model.Keys;

import org.OpenNI.ImageGenerator;

import com.sun.org.apache.xml.internal.resolver.helpers.Debug;

import tracking.CameraTracking;

import ninja.game.model.Keys;
import control.Highscores;
import control.ProgramController;
import control.ProgramState;
import graphics.StandardTextures;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;

public class GameOverState extends ProgramState {
	

	static int TIMEOUT = 200; //in seconds

	// private float clickPosX = 0.0f;
	// private float clickPosY = 0.0f

	ImageGenerator imgGen;

	ProgramController programController;
	float distance, time;
	int score;
	long countdownTime;
	boolean initialMeasure;

	public GameOverState(ProgramController programController, float distance, float time) {
		// player.init(programController);
		this.programController = programController;
		this.score = (int) getScore(distance, time);
		this.distance = distance;
		this.time = time;
		this.isHighScore = programController.highscores.getHighScorePos(programController.gameSettings.difficulty, (int) getScore(distance, time)) < 4;
		p("highscore pos is: " + programController.highscores.getHighScorePos(programController.gameSettings.difficulty, (int) getScore(distance, time)));
		countdownTime = System.currentTimeMillis();
		initialMeasure = true;
		playerCenterX = 0.0f;
	}

	private float getScore(float distance, float time) {
		return distance * time;
	}

	private static void p(String p) {
		if (control.Debug.GAME_OVER_SYSTEM_OUT_PRINTLN)
			System.out.println(p);
	}

	private float worldZoom = 2.3f;

	@Override
	public void onStep(float deltaTime) {
		// p("head pos projected: "+programController.tracking.getHeadPos());
		// DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		// camera.set(0.f, 1.f, worldZoom);
		// player.step(deltaTime);

		// if (!player.gameOver)
		// player.fallDown();
		// p("pos: "+programController.tracking.getHeadPos());
		camera.set(0.f, 2.f, worldZoom);
	}

	boolean isHighScore;
	float playerCenterX;

	@Override
	public void onDraw() {
		int timeLeft = TIMEOUT-(int)((System.currentTimeMillis() - countdownTime)/1000L);

		if (timeLeft < 0) {
			super.programController.switchState(new MainMenuState().init(programController));
		}
		

		
		graphics2D.drawString(0f, 0.35f, 0.1f, 0, 0, 0, ""+timeLeft);
		
		graphics.clear(0.3f, 0.3f, 0.3f);

		graphics2D.switchGameCoordinates(false);
		graphics2D.setWhite();

		graphics.bindTexture(null);

		// floor
		graphics2D.setColor(0.5f, 0.5f, 0.5f);
		graphics2D.drawRectCentered(0, -5.0f, 20, 10.0f, 0);
		graphics2D.setColor(0.7f, 0.7f, 0.7f);
		graphics2D.drawRectCentered(0, -0.05f, 20, 0.1f, 0);

		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(0, 0.8f, 0.5f, 0, 0, 0, "Game Over");
		graphics2D.setColor(0.f, 0.f, 0.f);//		graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Drink (or press up) to restart!");

		graphics2D.setWhite();
		


		if (isHighScore) {

			graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Move left/right to take a highscore-picture!");

			
			graphics2D.drawString(-0f, 0.55f, 0.1f, 0, 0, 0, "New Highscore!");
			graphics2D.drawString(-0f, 0.45f, 0.1f, 0, 0, 0, "You scored " + score + " point"+(score>1?"s":"")+"!");
			
			
			float DISTANCE_TO_MIDDLE = 0.7f;
			graphics2D.drawString(-DISTANCE_TO_MIDDLE, 0.4f, 0.1f, 0, 0, 0, "Photo");
			graphics2D.drawString(DISTANCE_TO_MIDDLE, 0.4f, 0.1f, 0, 0, 0, "No Photo");

			graphics.bindTexture(null);

			// left & right white squares
			graphics2D.setColor(1f, 1f, 1f);
			graphics2D.drawRectCentered(-DISTANCE_TO_MIDDLE, 0, 0.5f, 0.6f);
			graphics2D.drawRectCentered(DISTANCE_TO_MIDDLE, 0, 0.5f, 0.6f);
			
			
			if (!control.Debug.FAKE_CONTROLS && ((CameraTracking) programController.tracking).app.users.length>0) {
				
				if (initialMeasure || playerCenterX == 0.0f) {
					initialMeasure = false;
					playerCenterX = (float)((CameraTracking) programController.tracking).getHeadPos().x;	
				}
				float playerX = ((float)((CameraTracking) programController.tracking).getHeadPos().x);	
				float diffX = (playerX - playerCenterX)/110f;
				if (diffX >= DISTANCE_TO_MIDDLE-0.01f) {
					//p("not taking a picture & returning");
					programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), null); 
					super.programController.switchState(new MainMenuState().init(programController));
				}
				if (diffX <= -DISTANCE_TO_MIDDLE+0.01f) {
					//p("took a picture & returning");
					programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), ((CameraTracking) programController.tracking).getColorImageByteBuffer()); 
					super.programController.switchState(new MainMenuState().init(programController));
				}
				graphics.bindTexture(StandardTextures.CUBE);
				graphics.bindTexture(new Texture(graphics, ((CameraTracking) programController.tracking).getColorImageByteBuffer(), 60, 100, new TextureSettings()));
				graphics2D.drawRectCentered(diffX, 0f, 0.45f, DISTANCE_TO_MIDDLE);

			} else {
				programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), null); 
			}

			graphics.bindTexture(null);

			drawSquareAround(-DISTANCE_TO_MIDDLE, 0.07f, 0.5f, 0.6f);
			drawSquareAround(DISTANCE_TO_MIDDLE, 0.07f, 0.5f, 0.6f);
		} else {
			graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Drink to restart!");
			graphics2D.drawString(-0f, 0.3f, 0.1f, 0, 0, 0, "You only scored " + score + " points :-(");
			graphics2D.drawString(-0f, 0.2f, 0.1f, 0, 0, 0, "Try again!");
		}

	}

	private void drawSquareAround(float around, float thickness, float width, float height) {
		graphics2D.setColor(0f, 0f, 0f);
		graphics2D.drawRectCentered(around - width / 2, 0, thickness, height);
		graphics2D.drawRectCentered(around + width / 2, 0, thickness, height);
		graphics2D.drawRectCentered(around, height / 2, width + thickness, thickness);
		graphics2D.drawRectCentered(around, -height / 2, width + thickness, thickness);
	}

	public void keyDown(int key) {
		// Enter the selected door
		if (key == Keys.UP) {
			// switch to menu
			super.programController.switchState(new MainMenuState().init(programController));
		}
	}

	@Override
	public void onDrink() {
		// start game
		// super.programController.switchState(new
		// MainMenuState().init(programController));
	}

	@Override
	public int getType() {
		return super.GAMEOVER;
	}

}
