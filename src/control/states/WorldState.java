package control.states;

import figure.Player;
import control.ProgramController;
import control.ProgramState;
import graphics.Camera2D;

public abstract class WorldState extends ProgramState {

	protected Player player;
	protected Camera2D camera;
	
	public WorldState() {
		player = new Player();
		camera = new Camera2D();
	}
	
	public WorldState init(ProgramController programController) {
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
		player.bending += bending;
		player.setSpeedX( (float)(player.bending / (Math.PI/4.0) / 2.0) );
	}
	
}
