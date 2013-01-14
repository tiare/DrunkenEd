package control.states;

import org.OpenNI.ImageGenerator;

import control.ProgramController;
import control.ProgramState;
import figure.DrunkenSkeleton;
import figure.Player;

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
		
		//floor
		graphics2D.setColor(0.5f, 0.5f, 0.5f);
		graphics2D.drawRectCentered(0,-5.0f, 20,10.0f, 0);
		
		// draw left tree
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(-3.3f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(-3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(-3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/4.0f);
		graphics2D.drawRectCentered(-3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/7.0f);
		// draw right tree
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(3.3f,1.0f, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/2.0f);
		graphics2D.drawRectCentered(3.3f,2.5f, 1.0f,1.0f, (float)Math.PI/5.0f);
		
		graphics2D.setWhite();
		//graphics2D.s
		//player.draw();
	}
	
	@Override
	public int getType() {
		return super.GAMEOVER;
	}

}
