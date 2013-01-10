package control.states;

import control.ProgramController;
import figure.DrunkenSkeleton;

public class MainMenuState extends WorldState {
	
//	private float clickPosX = 0.0f;
//	private float clickPosY = 0.0f;
	private int activeDoor = 0;

	@Override
	public void onStep(float deltaTime) {
		//camera.set(0, 1, 2);
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX, skeleton.mBreastJoint.mPosY, 2.3f);
		player.step(deltaTime);
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		
		drawDoor(0.f, 2.0f, false);
		drawDoor(-2.f, 2.0f, false);
		drawDoor(2.f, 2.0f, true);
		
		graphics2D.setWhite();
		player.draw();
	}
	
	private void drawDoor (float posX, float posY, boolean active) {
		if (active) {
			graphics2D.setColor(1.0f, 0.3f, 0.3f);
		}
		else {
			graphics2D.setColor(0.3f, 0.3f, 1.0f);
		}
		graphics2D.drawRectCentered(posX, posY, 1.f, 2.f);
	}
	
	//TODO: set difficulty in gamesettings!
	//TODO: programController.switchState(new GameState) ...
	

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
