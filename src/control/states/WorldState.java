package control.states;

import figure.Player;
import control.ProgramController;
import control.ProgramState;

public abstract class WorldState extends ProgramState {

	protected Player player;
	protected ProgramController programController;
	public static final float SPEED_FACTOR = 2;
	
	public WorldState() {
		super();
		player = new Player();
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
		player.steeredBending = bending;
		player.setSpeedX( (float)((player.steeredBending + player.drunkenBending) / (Math.PI/4.0) / 2.0) * SPEED_FACTOR );
	}
	
	@Override
	public int getType() {
		return -1; // MENU = 0, GAME = 1, GAMEOVER = 2
	}
	
}
