package control.states;

import control.ProgramController;
import control.ProgramState;
import figure.DrunkenSkeleton;
import figure.Player;

public class GameOverState extends ProgramState{

//	private float clickPosX = 0.0f;
//	private float clickPosY = 0.0f;
	
	Player player;
	
	public GameOverState(ProgramController programController) {
		player.init(programController);
	}

	@Override
	public void onStep(float deltaTime) {
		//camera.set(0, 1, 2);
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		
	}

	@Override
	public void onDraw() {
		graphics.clear(1.0f, 0.3f, 0.3f);
		graphics2D.setWhite();
		////graphics2D.setColor(1.0f, 0.3f, 0.3f);
		//graphics2D.drawRect(5.f, 5.f, 50.f, 50.f);
	}

//	@Override
//	public void pointerDown(float x,float y,int pId) {
//		clickPosX = x;
//		clickPosY = y;
//		System.out.println("Clicked");
//	}
//	
//	@Override
//	public void pointerDragged(float x,float y,int pId) {
//		//player.posX = clickPosX-x;
//		//player.posY = clickPosY-y;
//		System.out.println("Dragged");
//	}
//	
//	@Override
//	public void pointerUp(float x,float y,int pId) {
//		
//	}

}
