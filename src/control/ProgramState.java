package control;

import org.OpenNI.Point3D;

import tracking.AbstractTracking;
import tracking.TrackingListener;
import graphics.Camera2D;
import graphics.defaults.Default2DGraphics;
import graphics.defaults.Default3DGraphics;
import graphics.translator.GraphicsTranslator;

public abstract class ProgramState implements TrackingListener {

	public static final float PI = 3.1415926535f;
	
	protected GraphicsTranslator graphics;
	protected Default2DGraphics graphics2D;
	protected Default3DGraphics graphics3D;
	protected ProgramController programController;
	protected boolean showMarkWarnings;
	protected float stateTimer;
	protected Highscores highscores;
	protected GameSettings gameSettings;
	public static final int MENU = 0, GAME = 1, GAMEOVER = 2;
	protected Camera2D camera;
	private boolean initialized;
	
	protected abstract void onStep(float deltaTime);
	protected abstract void onDraw();
	
	public ProgramState() {
		camera = new Camera2D();
		camera.mAdaption = 0.3f;
		initialized = false;
		showMarkWarnings = true;
	}
	
	protected void derivedInit() { };
	
	public ProgramState init(ProgramController programController) {
		initialized = true;
		this.programController = programController;
		graphics = programController.mGraphics;
		graphics2D = programController.mGraphics2D;
		
		highscores = programController.highscores;
		gameSettings = programController.gameSettings;
		derivedInit();
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
		onStep(deltaTime);
	}
	
	public void draw() {
		graphics.clear(0, 0, 0);
		graphics.setShaderProgram(graphics2D.getDefaultProgram());
		graphics2D.setWhite();
		onDraw();
		
		if(Debug.DRAW_USER_SKELETON) {
			graphics2D.switchGameCoordinates(false);
			graphics.bindTexture(null);
			graphics2D.setColor(0.7f, 0, 0, 0.5f);
			Point3D[] points = programController.tracking.getSkeletonPoints();
			if(points!=null) {
				for(Point3D point:points)
					if(point!=null){
						//System.out.println(point.getX()+" "+point.getY());
						graphics2D.drawRectCentered((point.getX()/640f*2-0.5f), (-point.getY()/480f*2+0.5f), 0.1f);
					
					}
			}
		}
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
	
	protected void onAbort() {
		
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
	
	protected float pulse(float frequency, float intensity) {
		return (float)Math.abs(Math.sin(stateTimer*frequency)*intensity);
	}
}
