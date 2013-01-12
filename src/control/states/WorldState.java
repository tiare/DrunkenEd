package control.states;

import figure.Player;
import control.ProgramController;
import control.ProgramState;
import graphics.Camera2D;

public abstract class WorldState extends ProgramState {

	protected Player player;
	protected Camera2D camera;
	protected ProgramController programController;
	
	public WorldState() {
		player = new Player();
		camera = new Camera2D();
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
	
	@Override
	public void onBend(float bending){
		player.steeredBending += bending;
		player.setSpeedX( (float)((player.steeredBending + player.drunkenBending) / (Math.PI/4.0) / 2.0) );
	}
	
	@Override
	public int getType() {
		return -1; // MENU = 0, GAME = 1, GAMEOVER = 2
	}
	
}
