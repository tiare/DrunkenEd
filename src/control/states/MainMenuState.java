package control.states;

import ninja.game.model.Keys;
import figure.DrunkenSkeleton;

public class MainMenuState extends WorldState {
	
	public static final int NONE = -1, LEFT = 0, CENTER = 1, RIGHT = 2;
	private int activeDoor = NONE;
	private float doorWith = 1.f;
	private float doorHeight = 2.5f;
	private float doorLx = -2.f;
	private float doorCx = 0.f;
	private float doorRx = 2.f;
	private float doorsY = 1.f;
	private float oldPlayerPosX = 0f;

	public MainMenuState () {
	
	}
	
	@Override
	public void onStep(float deltaTime) {
		//camera.set(0, 1, 2);
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX, skeleton.mBreastJoint.mPosY, 2.3f);
		oldPlayerPosX = player.posX;
		player.step(deltaTime);
		//TODO: don't let the player walk out of the screen
		dontLeaveScreen ();
		updateActiveDoor();
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		
		drawDoor(doorLx, doorsY, (activeDoor == 0));
		drawDoor(doorCx, doorsY, (activeDoor == 1));
		drawDoor(doorRx, doorsY, (activeDoor == 2));
		
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
	
	private void dontLeaveScreen () {
		//Set player back if he walks out
		if ( player.posX < -3 || player.posX > 3 )
			player.posX = oldPlayerPosX;
	}
	
	private void updateActiveDoor () {
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

	@Override
	public void keyDown(int key) {
		//Enter the selected door
		if( key == Keys.UP ) {
			//Enter level
			if (activeDoor != NONE) {
				//set difficulty in gamesettings!
				super.gameSettings.difficulty = activeDoor;			
				//start game
				super.programController.switchState(new GameState().init(programController));
			}
		}
	}
	
	@Override
	public int getType() {
		return super.MENU;
	}

}
