package control.states;


import java.nio.ByteBuffer;

import org.OpenNI.ImageGenerator;

import tracking.CameraTracking;

import control.ProgramController;
import control.ProgramState;
import figure.DrunkenSkeleton;
import figure.Player;
import graphics.StandardTextures;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;

public class GameOverState extends ProgramState {

//	private float clickPosX = 0.0f;
//	private float clickPosY = 0.0f
	
	Player player;
	ImageGenerator imgGen;
	
	
	public GameOverState(ProgramController programController) {
		//player.init(programController);
	}
	
	private static void p(String p) {
		System.out.println(p);
	}

	@Override
	public void onStep(float deltaTime) {
		p("pos: "+programController.tracking.getHeadPos());
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		

		graphics2D.setWhite();
		
		graphics.bindTexture(StandardTextures.CUBE);
		Texture t = new Texture(graphics);
		graphics.bindTexture(new Texture(graphics, programController.tracking.getColorImage(), 640, 480, new TextureSettings()));
		graphics2D.drawRectCentered(0,0.15f, 1,0.9f, stateTimer);
		
		graphics2D.setColor(0.5f, 0.5f, 0.85f);
		graphics2D.drawStringL(graphics2D.getScreenLeft()+0.03f, 0.9f, 0.1f, "Time:  "+(int)(stateTimer*10)/10f+"sec");
		//player.draw();
	}
	
	@Override
	public int getType() {
		return super.GAMEOVER;
	}

}
