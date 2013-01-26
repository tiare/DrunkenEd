package control;

import tracking.TrackingListener;
import graphics.Camera2D;
import graphics.defaults.Default2DGraphics;
import graphics.defaults.Default3DGraphics;
import graphics.translator.GraphicsTranslator;

public abstract class ProgramState implements TrackingListener {
	
	public static float DEFAULT_FADESPEED = 2.5f;
	protected GraphicsTranslator graphics;
	protected Default2DGraphics graphics2D;
	protected Default3DGraphics graphics3D;
	protected ProgramController programController;
	protected float stateTimer;
	protected Highscores highscores;
	protected GameSettings gameSettings;
	public static final int MENU = 0, GAME = 1, GAMEOVER = 2;
	protected Camera2D camera;
	private boolean initialized;
	private ProgramState fadeState;
	protected float fade;
	private float fadeSpeed;
	
	protected abstract void onStep(float deltaTime);
	protected abstract void onDraw();
	
	public ProgramState() {
		camera = new Camera2D();
		camera.mAdaption = 0.3f;
		initialized = false;
		fade = 1;
		fadeState = null;
	}
	
	public ProgramState init(ProgramController programController) {
		initialized = true;
		this.programController = programController;
		graphics = programController.mGraphics;
		graphics2D = programController.mGraphics2D;
		
		highscores = programController.highscores;
		gameSettings = programController.gameSettings;
		return this;
	}
	
	public static void p(String p) {
		System.out.println(p);
	}
	
	public static void p(String p, boolean print) {
		if(print){
			System.out.println(p);
		}
	}
	
	public void step(float deltaTime) {
		stateTimer += deltaTime;
		if(fadeState==null) {
			if(fade<1) {
				fade += deltaTime*fadeSpeed;
				if(fade>1)
					fade = 1;
			}
			onStep(deltaTime);
		}else{
			fade -= deltaTime*fadeSpeed;
			if(fade<0) {
				fade = 0;
				programController.switchState(fadeState);
				fadeState.setFadeIn();
			}
		}
	}
	
	private void setFadeIn() {
		fade = 0;
		fadeSpeed = DEFAULT_FADESPEED;
	}
	
	public void fadeToState(ProgramState state) {
		fade = 1;
		fadeSpeed = DEFAULT_FADESPEED;
		fadeState = state;
	}
	
	public void draw() {
		graphics.clear(0, 0, 0);
		graphics.setShaderProgram(graphics2D.getDefaultProgram());
		graphics.setAmbientColor(fade, fade, fade);
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
	
	public void startGraphics() {
		
	}
	public boolean isInitialized() {
		return initialized;
	}
}
