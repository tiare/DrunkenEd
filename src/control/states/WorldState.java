package control.states;

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
		synchronized (camera) {
			graphics2D.switchGameCoordinates(true);
			graphics2D.setCamera(camera);
			super.draw();
		}
	}
	
	@Override
	public int getType() {
		return -1; // MENU = 0, GAME = 1, GAMEOVER = 2
	}
	
}
