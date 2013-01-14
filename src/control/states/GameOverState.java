package control.states;

import ninja.game.model.Keys;
import control.ProgramController;
import control.ProgramState;
import figure.DrunkenSkeleton;
import figure.Player;

public class GameOverState extends WorldState{

//	private float clickPosX = 0.0f;
//	private float clickPosY = 0.0f;
	
	//Player player;
	private float worldZoom = 2.3f;
	
	public GameOverState() {
		//player.init(programController);
	}

	@Override
	public void onStep(float deltaTime) {
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(0.f, 1.f, worldZoom);
		player.step(deltaTime);
		
		if (!player.gameOver)
			player.fallDown();
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		
		////graphics2D.setColor(1.0f, 0.3f, 0.3f);
		//graphics2D.drawRect(5.f, 5.f, 50.f, 50.f);
		
		
		//floor
		graphics2D.setColor(0.5f, 0.5f, 0.5f);
		graphics2D.drawRectCentered(0,-5.0f, 20,10.0f, 0);
		graphics2D.setColor(0.7f, 0.7f, 0.7f);
		graphics2D.drawRectCentered(0,-0.1f, 20,0.1f, 0);
		
		graphics2D.setColor(1.f, 1.f, 1.f);
		graphics2D.drawString(0, 2.f, 1.f, 0, 0, 0, "Game Over");
		graphics2D.setColor(0.f, 0.f, 0.f);
		graphics2D.drawString(0, -0.8f, 0.3f, 0, 0, 0, "Drink (or press up) to restart!");
		
		graphics2D.setWhite();
		player.draw();
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
		super.programController.switchState(new MainMenuState().init(programController));
	}
	
	@Override
	public int getType() {
		return super.GAMEOVER;
	}

}
