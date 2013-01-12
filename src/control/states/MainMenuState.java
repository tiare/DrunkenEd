package control.states;

import control.ProgramController;
import figure.DrunkenSkeleton;

public class MainMenuState extends WorldState {
	
	public static final int NONE = -1, LEFT = 0, CENTER = 1, RIGHT = 2;
	private int activeDoor = NONE;
	private float doorWith = 1.f;
	private float doorHeight = 2.f;
	private float doorLx = -2.f;
	private float doorCx = 0.f;
	private float doorRx = 2.f;
	private float doorsY = 1.5f;
	

	@Override
	public void onStep(float deltaTime) {
		//camera.set(0, 1, 2);
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX, skeleton.mBreastJoint.mPosY, 2.3f);
		player.step(deltaTime);
		UpdateActiveDoor();
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		
		drawDoor(doorLx, doorsY, (activeDoor == 0));
		drawDoor(doorCx, doorsY, (activeDoor == 1));
		drawDoor(doorRx, doorsY, (activeDoor == 2));
		
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
		graphics2D.drawRectCentered(posX, posY, doorWith, doorHeight);
	}
	
	//TODO: set difficulty in gamesettings!
	//TODO: programController.switchState(new GameState) ...
	private void UpdateActiveDoor () {
		activeDoor = NONE;
		
		float playerLeft = player.posX-0.5f;
		float playerRight = player.posX+0.5f;
		
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
