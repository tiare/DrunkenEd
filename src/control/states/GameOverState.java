package control.states;

import org.OpenNI.ImageGenerator;

import tracking.CameraTracking;
import control.ProgramController;
import control.ProgramState;
import graphics.StandardTextures;
import graphics.events.Keys;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;

public class GameOverState extends ProgramState {

	static int TIMEOUT = 50; // in seconds
	static int SECONDTIMEOUT = 10;
	static boolean DEBUG = true;

	ImageGenerator imgGen;

	ProgramController programController;
	float distance, time;
	int score;
	long countdownTime;
	long secondCountdownTime;
	boolean initialMeasure;

	boolean isHighScore;
	// initial player x position
	float playerCenterX;
	// how much player moved from initial position
	float diffX;
	final float range;
	Texture playerImageTexture;

	private float worldZoom = 2.3f;
	private float timeLeft = 0;
	boolean tookPicture;
	private GameState gameState;
	
	boolean noPicture;

	public GameOverState(ProgramController programController, GameState gameState, float distance, float time) {
		// player.init(programController);
		this.programController = programController;
		this.gameState = gameState;
		this.score = (int) getScore(distance, time);
		this.distance = distance;
		this.time = time;
		this.isHighScore = programController.highscores.getHighScorePos(programController.gameSettings.difficulty, (int) getScore(distance, time)) < 4;
		isHighScore = DEBUG;
		p("highscore pos is: " + programController.highscores.getHighScorePos(programController.gameSettings.difficulty, (int) getScore(distance, time)));
		countdownTime = System.currentTimeMillis();
		initialMeasure = true;
		playerCenterX = 0.0f;
		range = 0.1f;
		tookPicture = false;
		noPicture = false;
	}

	private float getScore(float distance, float time) {
		return distance;
	}

	public static void p(String p) {
		if (control.Debug.GAME_OVER_SYSTEM_OUT_PRINTLN)
			System.out.println(p);
	}

	@Override
	public void onStep(float deltaTime) {
		if (gameState != null) {
			gameState.gameOverOverlay = true;
			gameState.step(deltaTime);
		}
		if (tookPicture) {
			if ((int) ((System.currentTimeMillis() - secondCountdownTime) / 1000) > SECONDTIMEOUT) {
				programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), ((CameraTracking) programController.tracking).getColorImageByteBuffer());
			}
		}
		if (tookPicture) {
			timeLeft = SECONDTIMEOUT - (int) ((System.currentTimeMillis() - secondCountdownTime)/1000L);
		} else 
			timeLeft = TIMEOUT - (int) ((System.currentTimeMillis() -  countdownTime) / 1000L);
		
		if (timeLeft < 0 || noPicture) {
			p("Countdown expired, returning to main menu");
			p("noPicture = "+noPicture);
			programController.fadeToState(new MainMenuState());
		}
		camera.set(0.f, 2.f, worldZoom);
	}

	private void drawBackground() {

		if (gameState != null)
			gameState.draw();

		graphics.setAmbientColor(programController.fade);
		graphics2D.switchGameCoordinates(false);
		graphics2D.setWhite();

		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(0, 0.8f, 0.5f, 0, 0, 0, "Game Over");
	}

	@Override
	public void onDraw() {
		if (noPicture) return;

		drawBackground();

		graphics2D.setWhite();

		if (isHighScore) {

			if (!tookPicture)
				graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Drink to take a picture!");

			graphics2D.drawString(-1f, 0.25f, 0.1f, 0, 0, 0, "Score: " + score + " point" + (score > 1 ? "s" : ""));
			graphics2D.drawString(-1f, 0.15f, 0.1f, 0, 0, 0, "New Highscore!");
				graphics2D.drawString(-1f, 0.05f, 0.1f, 0, 0, 0, "Returning in " + (timeLeft > 0 ? (int) timeLeft : "0") + " seconds");


			float DISTANCE_TO_MIDDLE = 1.2f;
			if (!tookPicture) {
				graphics2D.drawString(0, 0.45f, 0.1f, 0, 0, 0, "Photo");
				graphics2D.drawString(DISTANCE_TO_MIDDLE, 0.5f, 0.1f, 0, 0, 0, "Exit");
			}
			graphics.bindTexture(null);

			// left & right white squares
			// graphics2D.setColor(1f, 1f, 1f);
			// graphics2D.drawRectCentered(0, 0, 0.5f, 0.6f);
			// graphics2D.drawRectCentered(DISTANCE_TO_MIDDLE, 0, 0.5f, 0.6f);

			if (!control.Debug.FAKE_CONTROLS && ((CameraTracking) programController.tracking).app.users.length > 0) {
				if (initialMeasure || playerCenterX == 0.0f) {
					initialMeasure = false;
					playerCenterX = (float) ((CameraTracking) programController.tracking).getHeadPos().x;
				}
				float playerX = ((float) ((CameraTracking) programController.tracking).getHeadPos().x);
				if (!tookPicture)
					diffX = (playerX - playerCenterX) / 110f;
				if (diffX >= DISTANCE_TO_MIDDLE - 0.01f) {
					// p("not taking a picture & returning");
					programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), null);
					noPicture = true;
				}
				if (diffX < 0.0f)
					diffX = 0.0f;

				graphics.bindTexture(StandardTextures.CUBE);
				if (playerImageTexture != null) {
					if (!tookPicture)
						playerImageTexture.update(((CameraTracking) programController.tracking).getColorImageByteBuffer());
				} else {
					playerImageTexture = new Texture(graphics, ((CameraTracking) programController.tracking).getColorImageByteBuffer(), 60, 100, new TextureSettings());
				}
				graphics.bindTexture(playerImageTexture);
				graphics2D.drawRectCentered(diffX, 0f, 0.45f, 0.7f);

			} else {
				programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), null);
				//noPicture = true;
			}

			graphics.bindTexture(null);

			if (!tookPicture) {
				graphics2D.setColor(0f, diffX < 0.3f ? 1f : 0f, 0f);
				drawSquareAround(0, 0.07f, 0.5f, 0.7f);
				graphics2D.setColor(0f, diffX > DISTANCE_TO_MIDDLE - 0.3f ? 1f : 0f, 0f);
				drawSquareAround(DISTANCE_TO_MIDDLE, 0.07f, 0.5f, 0.7f);
				graphics2D.setColor(1, 0, 0);
				// red cross
				graphics2D.drawLine(DISTANCE_TO_MIDDLE - 0.25f + 0.05f, 0.3f, DISTANCE_TO_MIDDLE + 0.25f - 0.05f, -0.3f, 0.01f);
				graphics2D.drawLine(DISTANCE_TO_MIDDLE + 0.25f - 0.05f, 0.3f, DISTANCE_TO_MIDDLE - 0.25f + 0.05f, -0.3f, 0.01f);
			}
		} else {
			graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Drink to restart!");
			graphics2D.drawString(-0f, 0.3f, 0.1f, 0, 0, 0, "You only scored " + score + " points :-(");
			graphics2D.drawString(-0f, 0.2f, 0.1f, 0, 0, 0, "Try again!");
		}

	}

	private void drawSquareAround(float around, float thickness, float width, float height) {
		graphics2D.drawRectCentered(around - width / 2, 0, thickness, height);
		graphics2D.drawRectCentered(around + width / 2, 0, thickness, height);
		graphics2D.drawRectCentered(around, height / 2, width + thickness, thickness);
		graphics2D.drawRectCentered(around, -height / 2, width + thickness, thickness);
	}

	public void keyDown(int key) {
		// Enter the selected door
		if (key == Keys.UP) {
			// switch to menu
			programController.fadeToState(new MainMenuState());
		}
	}

	long drinkWait = -1L;

	@Override
	public void onDrink() {
		if (Long.valueOf(drinkWait).equals(-1L)) {
			drinkWait = System.currentTimeMillis();
		}
		// start game
		if (!tookPicture && isHighScore && diffX <= range && System.currentTimeMillis() - drinkWait > 50L) {
			tookPicture = true;
			secondCountdownTime = System.currentTimeMillis();
			p("took a picture & returning");
			// super.programController.switchState(new
			// MainMenuState().init(programController));
		}
	}

	@Override
	public int getType() {
		return super.GAMEOVER;
	}

	@Override
	public void userLost() {

	}

}
