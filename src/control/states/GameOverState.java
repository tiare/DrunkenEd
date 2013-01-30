package control.states;

import org.OpenNI.ImageGenerator;

import tracking.CameraTracking;
import control.Debug;
import control.ProgramState;
import graphics.FloatColor;
import graphics.StandardTextures;
import graphics.events.Keys;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;

public class GameOverState extends ProgramState {

	 int TIMEOUT = 20; // in seconds
	 int SECONDTIMEOUT = 2;
	static boolean DEBUG = false;

	ImageGenerator imgGen;

	private static final FloatColor GAMEOVER_CL1 = new FloatColor(1,0.4f,0.1f);
	private static final FloatColor GAMEOVER_CL2 = new FloatColor(1,0.55f,0.1f);
	//private static final FloatColor TAKEPHOTO_CL1 = new FloatColor(1,1,0);
	//private static final FloatColor TAKEPHOTO_CL2 = new FloatColor(1,1,0.8f);
	private static final FloatColor TAKEPHOTO_CL1 = new FloatColor(0.6f,0.6f,0.9f);
	private static final FloatColor TAKEPHOTO_CL2 = new FloatColor(0.8f,0.8f,1);
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
	boolean returnWithoutHighscore;
	
	boolean noPicture;

	public GameOverState(GameState gameState, float distance, float time) {
		// player.init(programController);
		this.gameState = gameState;
		this.score = (int) getScore(distance, time);
		this.distance = distance;
		this.time = time;
		range = 0.1f;
	}
	
	public void derivedInit() {
		this.isHighScore = programController.highscores.getHighScorePos(programController.gameSettings.difficulty, (int) getScore(distance, time)) < 4;
		if (!isHighScore) {
			TIMEOUT = 20;
		}
		if (DEBUG) {
			this.isHighScore = true;
			TIMEOUT = 50;
			SECONDTIMEOUT = 15;
		}
		TIMEOUT += 2;
		p("highscore pos is: " + programController.highscores.getHighScorePos(programController.gameSettings.difficulty, (int) getScore(distance, time)));
		countdownTime = System.currentTimeMillis();
		initialMeasure = true;
		playerCenterX = 0.0f;
		tookPicture = false;
		noPicture = false;
		returnWithoutHighscore = false;
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
			timeLeft = SECONDTIMEOUT - (int) ((System.currentTimeMillis() - secondCountdownTime)/1000L);
		} else {
			timeLeft = TIMEOUT - (int) ((System.currentTimeMillis() -  countdownTime) / 1000L);	
		}
		if (timeLeft < 0 && !tookPicture && isHighScore) {
			programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), null);
		}
		if (timeLeft < 0 || noPicture || returnWithoutHighscore) {
			p("Countdown expired, returning to main menu");
			programController.fadeToState(new MainMenuState());
		}
		camera.set(0.f, 2.f, worldZoom);
	}
	
	protected float appear(float time,float speed,float over) {
		time=stateTimer-time;
		if(time<0)
			return 0;
		else{
			float uTime = (time/speed)>1?1:time/speed;
			return (float)Math.sin(uTime*Math.PI/2*over);
		}
	}
	
	protected float appear(float time,float speed) {
		return appear(time,speed,1.4f);
	}

	@Override
	public void onDraw() {
		if (noPicture) return;
		
		if (gameState != null)
			gameState.draw();
		
		if (programController.markWarning)
			return;

		graphics.setAmbientColor(programController.getBrightness());
		graphics2D.switchGameCoordinates(false);

		float startAnim = 0.5f;
		if(stateTimer<startAnim)
			return;
		
		float scoreAppear = 0.7f;
		final float pulseFreq = 5.5f;
		float a = (float)Math.sin(stateTimer*6)*0.04f;
		
		float pos = (stateTimer-startAnim)<scoreAppear?((float)Math.pow(((stateTimer-startAnim))/scoreAppear,1.0f)):1;
		graphics2D.setFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
		graphics2D.setColorWeighted(GAMEOVER_CL1,GAMEOVER_CL2,pulse(pulseFreq/2,1));
		graphics2D.drawString(0, 0.05f+pos*0.7f, 0.44f*appear(startAnim,1/scoreAppear/2,1.4f), 0, 0, a, "Game Over!");

		scoreAppear += 0.1f+startAnim;
		graphics2D.setColor(0.8f, 0.8f, 1f);
		graphics2D.drawString(-0.25f-pulse(pulseFreq,0.015f), 0.49f, 0.1f*appear(scoreAppear,0.45f,1), 0, 0, 0, "Distance: "); 
		graphics2D.setWhite();
		graphics2D.drawString(0.2f, 0.49f, 0.15f*appear(scoreAppear+0.15f,0.5f)*(1+pulse(pulseFreq,0.12f)), 0, 0, 0, (int)score + "m");
		
		float returnTime;
		float helpCl = pulse(pulseFreq/2,1);
		
		if(stateTimer>scoreAppear) {
			if (isHighScore) {
				//graphics2D.setColor(0.8f, 0.8f, 0f);

				//graphics2D.draws
				graphics2D.setColor(1f,1f,0f);
				//graphics2D.setColorWeighted(TAKEPHOTO_CL1,TAKEPHOTO_CL2,pulse(pulseFreq/2,1));
				graphics2D.drawString(0f, 0.34f, (0.24f+pulse(pulseFreq,0.035f))*appear(scoreAppear+0.5f,0.6f,1.7f), 0, 0, 0, "New Highscore!");
				scoreAppear += 0.35f;
				a = 0.37f+0.08f*appear(scoreAppear+1.4f,0.4f,1);
				float height = 0.122f*appear(scoreAppear+1.4f,0.5f,1.1f);
				float s = appear(scoreAppear+1.45f,0.6f,1)*1.15f;
				float x = -0.05f-pulse(pulseFreq,0.05f)-0.66f*appear(scoreAppear+1.4f,0.4f,1);
				float shiftY = 0.2f;
				if(!tookPicture) {
					//graphics2D.setColor(1,1,0);
					graphics2D.setColorWeighted(TAKEPHOTO_CL1,TAKEPHOTO_CL2,helpCl);
					graphics2D.drawString(x, -shiftY, height, 0, 0, a, "Drink to take");
					graphics2D.drawString(x+0.05f, -0.13f-shiftY, height, 0, 0, a, "a photo!");
				}else{
					graphics2D.setColor(0,1,0);
					graphics2D.drawString(-0.65f, -shiftY-0.06f, height*1.5f, 0, 0, a, "OK!");
				}
					
				float DISTANCE_TO_MIDDLE = 1.2f;
				if (!tookPicture) {
					//graphics2D.drawString(0, 0.45f, 0.1f, 0, 0, 0, "Photo");
					//graphics2D.drawString(DISTANCE_TO_MIDDLE, 0.5f, 0.1f, 0, 0, 0, "Exit");
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
					
	
					if (!tookPicture && diffX < range)
						//graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Drink to take a picture!");
	
					//graphics.bindTexture(StandardTextures.CUBE);
						graphics2D.setWhite();
					if (playerImageTexture != null) {
						if (!tookPicture)
							playerImageTexture.update(((CameraTracking) programController.tracking).getColorImageByteBuffer());
					} else {
						playerImageTexture = new Texture(graphics, ((CameraTracking) programController.tracking).getColorImageByteBuffer(), 80, 125, new TextureSettings());
					}
					
					graphics.bindTexture(playerImageTexture);
					graphics2D.drawRectCentered(0f, -0.25f, 0.45f*s, 0.7f*s);
	
				} else {
					//programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), null);
					//noPicture = true;
				}
	
				graphics.bindTexture(null);
	
				if (!tookPicture) {
					//graphics2D.setColor(0f, 1f, 0f);
					//drawSquareAround(0, 0.07f, 0.5f, 0.7f);
					//graphics2D.setColor(0f, diffX > DISTANCE_TO_MIDDLE - 0.3f ? 1f : 0f, 0f);
					//drawSquareAround(DISTANCE_TO_MIDDLE, 0.07f, 0.5f, 0.7f);
					//graphics2D.setColor(1, 0, 0);
					// red cross
					//graphics2D.drawLine(DISTANCE_TO_MIDDLE - 0.25f + 0.05f, 0.3f, DISTANCE_TO_MIDDLE + 0.25f - 0.05f, -0.3f, 0.01f);
					//graphics2D.drawLine(DISTANCE_TO_MIDDLE + 0.25f - 0.05f, 0.3f, DISTANCE_TO_MIDDLE - 0.25f + 0.05f, -0.3f, 0.01f);
				}
				
				graphics2D.setWhite();
				if(Debug.NO_PHOTO_CAMERA)
					graphics.bindTexture(null);
				else
					graphics.bindTexture(playerImageTexture);
				
				graphics2D.drawRectCentered(0f, -0.25f, 0.45f*s, 0.7f*s);
				
				graphics.bindTexture(StandardTextures.PHOTO_FRAME);
				float c = pulse(pulseFreq/2,1);
				if(tookPicture)
					graphics2D.setColor(0.1f,0.9f,0.1f);
				else
					graphics2D.setColor(c*0.18f,c*0.18f,c*0.4f);//graphics2D.setColor(0.25f,0.25f,0.8f);
				graphics2D.drawRectCentered(0f, -0.25f, 0.45f*s*1.15f, 0.7f*s*1.1f);
				
				returnTime = 2.5f;
			} else {

				//graphics2D.setColor(0.8f, 0.8f, 1);
				graphics2D.setColorWeighted(TAKEPHOTO_CL1,TAKEPHOTO_CL2,helpCl);
				graphics2D.drawString(0, 0.0f, (0.15f+pulse(pulseFreq,0.022f))*appear(scoreAppear+0.6f,0.3f,1), 0, 0, 0, "Drink to try again!");
	
				//graphics2D.drawString(1.44f, -0.8f, 0.1f, 0, 0, 0, (timeLeft > 0 ? String.valueOf((int) timeLeft) : "0"));
				returnTime = 1.4f;
			}
		
			graphics2D.setColor(0.8f, 0.8f, 1);
			graphics2D.drawString(0f, -0.83f, 0.115f*appear(scoreAppear+returnTime,0.3f), 0, 0, 0, "Returning in "+(timeLeft > 0 ? String.valueOf((int) timeLeft) : "0")+"s");

		}

	}

	private void drawSquareAround(float around, float thickness, float width, float height) {
		graphics2D.drawRectCentered(around - width / 2, -0.05f, thickness, height);
		graphics2D.drawRectCentered(around + width / 2, -0.05f, thickness, height);
		graphics2D.drawRectCentered(around, height / 2-0.05f, width + thickness, thickness);
		graphics2D.drawRectCentered(around, -height / 2-0.05f, width + thickness, thickness);
	}

	public void keyDown(int key) {
		// Enter the selected door
		if (key == Keys.UP) {
			// switch to menu
			programController.highscores.addHighscore(programController.gameSettings.difficulty, (int) getScore(distance, time), null);
			programController.fadeToState(new MainMenuState());
		}
		
		if (key == 'p') 
			tookPicture = true;
	}

	long drinkWait = -1L;

	@Override
	public void onDrink() {
		if(stateTimer<4)
			return;
		if (!tookPicture && isHighScore && diffX <= range) {
			tookPicture = true;
			secondCountdownTime = System.currentTimeMillis();
			p("took a picture & returning");
			// super.programController.switchState(new
			// MainMenuState().init(programController));
		}
		if (!isHighScore) {
			returnWithoutHighscore = true;
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
