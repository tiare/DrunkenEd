package control;

import tracking.TrackingListener;
import graphics.defaults.Default2DGraphics;
import graphics.translator.GraphicsTranslator;

public abstract class ProgramState implements TrackingListener {
	
	protected GraphicsTranslator graphics;
	protected Default2DGraphics graphics2D;
	protected ProgramController programController;
	protected float stateTimer;
	protected Highscores highscores;
	protected GameSettings gameSettings;
	public static final int MENU = 0, GAME = 1, GAMEOVER = 2;
	
	protected abstract void onStep(float deltaTime);
	protected abstract void onDraw();
	
	public ProgramState init(ProgramController programController) {
		this.programController = programController;
		graphics = programController.mGraphics;
		graphics2D = programController.mGraphics2D;
		
		highscores = programController.highscores;
		gameSettings = programController.gameSettings;
		return this;
	}
	
	public void step(float deltaTime) {
		stateTimer += deltaTime;
		onStep(deltaTime);
	}
	
	public void draw() {
		graphics.clear(0, 0, 0);
		onDraw();
	}
	
	protected void onStop() {
		
	}
	
	public final void stop() {
		onStop();
	}
	
	protected void onStart() {
		
	}
	
	public final void start() {
		stateTimer = 0;
		onStart();
	}
	
	public void pointerDown(float x,float y,int pId) {
		
	}
	
	public void pointerDragged(float x,float y,int pId) {
		
	}
	
	public void pointerUp(float x,float y,int pId) {
		
	}
	
	public void keyDown(int key) {
		
	}
	
	public void keyUp(int key) {
		
	}
	
	@Override
	public void onDrink() {
		
	}
	
	@Override
	public void onBend(float bending) {
		
	}
	
	public int getType() {
		return -1;
	}
}