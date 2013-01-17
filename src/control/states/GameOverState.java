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

public class GameOverState extends ProgramState{

//	private float clickPosX = 0.0f;
//	private float clickPosY = 0.0f
	
	ImageGenerator imgGen;
	
	ProgramController programController;
	float distance, time;
	
	public GameOverState(ProgramController programController, float distance, float time) {
		//player.init(programController);
		this.programController = programController;
		this.distance = distance;
		this.time = time;
	}
	
	private float getScore(float distance, float time) {
		return distance*time;
	}
	
	private static void p(String p) {
		if (control.Debug.GAME_OVER_SYSTEM_OUT_PRINTLN) System.out.println(p);
	}
	private float worldZoom = 2.3f;
	
	//public GameOverState() {
		//player.init(programController);
	//}

	@Override
	public void onStep(float deltaTime) {
		//p("head pos projected: "+programController.tracking.getHeadPos());
		//DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		//camera.set(0.f, 1.f, worldZoom);
		//player.step(deltaTime);
		
		//if (!player.gameOver)
			//player.fallDown();
		//p("pos: "+programController.tracking.getHeadPos());
		camera.set(0.f, 2.f, worldZoom);
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);

		graphics2D.switchGameCoordinates(false);
		graphics2D.setWhite();


		

		graphics.bindTexture(null);
		
		//if (programController.highscores.isNewHighScoreAndAdd(programController.gameSettings.difficulty, (int)getScore(distance, time))) {
			
		//}
		
		/*graphics2D.setColor(0.5f, 0.5f, 0.85f);
		graphics2D.drawStringL(graphics2D.getScreenLeft()+0.03f, 0.9f, 0.1f, "Time:  "+(int)(stateTimer*10)/10f+"sec");
		//Texture t = new Texture(graphics);
		//graphics.bindTexture(new Texture(graphics, programController.tracking.getColorImage(), 640, 480, new TextureSettings()));
		//graphics2D.drawRectCentered(0,0.15f, 1,0.9f, stateTimer);
		
		//graphics2D.setColor(0.5f, 0.5f, 0.85f);
		//graphics2D.drawStringL(graphics2D.getScreenLeft()+0.03f, 0.9f, 0.1f, "Time:  "+(int)(stateTimer*10)/10f+"sec");
		//player.draw();
		////graphics2D.setColor(1.0f, 0.3f, 0.3f);
		//graphics2D.drawRect(5.f, 5.f, 50.f, 50.f);*/
		
		
		//floor
		graphics2D.setColor(0.5f, 0.5f, 0.5f);
		graphics2D.drawRectCentered(0,-5.0f, 20,10.0f, 0);
		graphics2D.setColor(0.7f, 0.7f, 0.7f);
		graphics2D.drawRectCentered(0,-0.05f, 20,0.1f, 0);
		
		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(0, 0.8f, 0.5f, 0, 0, 0, "Game Over");
		graphics2D.setColor(0.f, 0.f, 0.f);
		graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "Drink (or press up) to restart!");
		
		graphics2D.setWhite();
		

		graphics2D.drawString(-0.5f, 0.3f, 0.1f, 0, 0, 0, "Photo");
		graphics2D.drawString(0.5f, 0.3f, 0.1f, 0, 0, 0, "No Photo");
		//graphics2D.drawString(0, -0.5f, 0.2f, 0, 0, 0, "No Photo");

		graphics.bindTexture(null);

		//photo
		graphics2D.setColor(0f, 0f, 0f);
		graphics2D.drawRectCentered(-0.5f,0, 0.45f,0.45f);
		graphics2D.setColor(1f, 1f, 1f);
		graphics2D.drawRectCentered(-0.5f,0, 0.35f,0.35f);
		//no photo
		graphics2D.setColor(0f, 0f, 0f);
		graphics2D.drawRectCentered(0.5f,0, 0.45f,0.45f);
		graphics2D.setColor(1f, 1f, 1f);
		graphics2D.drawRectCentered(0.5f,0, 0.35f,0.35f);
		//grap
		
		if(!control.Debug.FAKE_CONTROLS){
			graphics.bindTexture(StandardTextures.CUBE);
			graphics.bindTexture(new Texture(graphics, ((CameraTracking) programController.tracking).getColorImage(), 60, 120, new TextureSettings()));
			graphics2D.drawRectCentered(getFloatX( (float)((CameraTracking) programController.tracking).getHeadPos().x),0.5f, 0.45f,0.6f);
			
		}
		
		
		
		//graphics2D.setWhite();
	}
	
	private float getFloatY(float y) {
		return graphics2D.normY(-(y - 480f / 2) / 480f * 2);
	}	
	private float getFloatX(float x) {
		p("x before: "+x);
		x= ((x - 640f / 2f) / 640f * 2)*1.2f;
		p("x after: "+x);
		p("");
		return x;
	}
	
	public void keyDown(int key) {
		//Enter the selected door
		if( key == Keys.UP ) {	
			//switch to menu
			super.programController.switchState(new MainMenuState().init(programController));
		}
	}
	
	@Override
	public void onDrink() {	
		//start game
		//super.programController.switchState(new MainMenuState().init(programController));
	}
	
	@Override
	public int getType() {
		return super.GAMEOVER;
	}

}
