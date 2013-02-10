package control.states;

import graphics.StandardTextures;
import figure.Player;
import control.ProgramController;
import control.ProgramState;

public abstract class WorldState extends ProgramState {

	protected Player player;
	protected ProgramController programController;
	
	public WorldState() {
		super();
		player = new Player(true);
	}
	
	public WorldState init(ProgramController programController) {
		this.programController = programController;
		super.init(programController);
		player.init(programController);
		return this;
	}
	
	@Override
	public void step(float deltaTime) {
		camera.update();
		super.step(deltaTime);
	}
	
	public void draw() {
		graphics2D.switchGameCoordinates(true);
		graphics2D.setCamera(camera);
		super.draw();
	}
	
	protected void drawBackground(float zoom,float offset) {
		float fac = zoom*camera.getZoom()*1.3f;
		graphics.bindTexture(StandardTextures.GAME_BACKGROUND);
		graphics2D.drawRect(camera.getX()-fac,offset,camera.getX()+fac,fac*1.1f);
	}
	
	@Override
	public void onJump(float velocity) {
		if(player!=null)
			player.jump(velocity);
	}
	
	@Override
	public int getType() {
		return -1; // MENU = 0, GAME = 1, GAMEOVER = 2
	}
	
}
