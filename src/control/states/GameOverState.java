package control.states;

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
		/*
		 * programController.highscores.isNewHighScoreAndAdd(
		 * programController.gameSettings.difficulty, (int) getScore(distance,
		 * time));
		 */
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
		int timeLeft = 20-(int)((System.currentTimeMillis() - countdownTime)/1000L);

		if (timeLeft < 0) {
			super.programController.switchState(new MainMenuState().init(programController));
		}
		
		if (initialMeasure) {
			initialMeasure = false;

			if (!control.Debug.FAKE_CONTROLS) {
				playerCenterX = 1.5f*getFloatX((float)((CameraTracking) programController.tracking).getHeadPos().x);
			}
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
		graphics2D.setColor(0.f, 0.f, 0.f);
		graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Move left/right to take a highscore-picture!");
//		graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Drink (or press up) to restart!");

		graphics2D.setWhite();
		


		if (isHighScore) {
			//TODO:
			/*
			 * measure players initial position and from there take the center
			 */

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

			if (!control.Debug.FAKE_CONTROLS) {
				float playerX = 1.5f*getFloatX((float)((CameraTracking) programController.tracking).getHeadPos().x);
				p("playerCenterX = "+playerCenterX);
				p("playerX = "+playerX);
				if (playerX >= playerCenterX + 0.75f) {
					//take picture & return
					p("would have taken a picture now!");
					super.programController.switchState(new MainMenuState().init(programController));
				}
				if (playerX <= playerCenterX-0.75f) {
					//don't take picture & return
					//TODO: standart picture
					p("would not have taken a picture now");
					super.programController.switchState(new MainMenuState().init(programController));
				}
				graphics.bindTexture(StandardTextures.CUBE);
				graphics.bindTexture(new Texture(graphics, ((CameraTracking) programController.tracking).getColorImageByteBuffer(), 60, 100, new TextureSettings()));
				graphics2D.drawRectCentered(playerX-playerCenterX, 0f, 0.45f, DISTANCE_TO_MIDDLE);

			}

			graphics.bindTexture(null);

			drawSquareAround(-DISTANCE_TO_MIDDLE, 0.07f, 0.5f, 0.6f);
			drawSquareAround(DISTANCE_TO_MIDDLE, 0.07f, 0.5f, 0.6f);
		} else {
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

	private float getFloatX(float x) {
		x= ((x - 640f / 2f) / 640f * 2) * 1.5f;
		if (x> 0.5f) {
			return 0.5f;
		}
		if (x<-0.5f) {
			return -0.5f;
		}
		return x;
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
