package control;

import tracking.Tracking;
import graphics.StandardTextures;
import graphics.defaults.DefaultSurface;

public class ProgramController extends DefaultSurface {

	private ProgramState currentState;
	public Tracking tracking;
	private float programTimer;
	public boolean started;
	
	public ProgramController() {
		super(true,false,true);
		started = false;
		programTimer = 0;
		tracking = new Tracking(this);
	}
	
	public void start() {
		currentState = Config.getStartState(this).init(this);
		programTimer = 0;
		tracking.init();
		
		StandardTextures.init(mGraphics);
		
		new Thread() {
			@Override
			public void run() {
				while(true) {
					long startTime = System.currentTimeMillis();
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					float deltaTime = (System.currentTimeMillis()-startTime)*0.001f;
					tracking.step(deltaTime);
					currentState.step(deltaTime);
					programTimer += deltaTime;
				}
			}
		}.start();
		
		started = true;
	}
	
	@Override
	public void initializationFinished() {
		start();
	}
	
	public void switchState(ProgramState newState) {
		if(currentState!=null) {
			currentState.onStop();
		}
		currentState = newState;
		newState.onStart();
	}
	
	public ProgramState getCurrentState() {
		return currentState;
	}
	
	public float getProgramTime() {
		return programTimer;
	}

	@Override
	public void draw() {
		if(currentState!=null) {
			currentState.draw();
			mGraphics.flush();
		}
	}
	
	@Override
	public void pointerDown(float x,float y,int pId) {
		if(currentState!=null)
			currentState.pointerDown(x, y, pId);
	}
	
	@Override
	public void pointerDragged(float x,float y,int pId) {
		if(currentState!=null)
			currentState.pointerDragged(x, y, pId);
	}
	
	@Override
	public void pointerUp(float x,float y,int pId) {
		if(currentState!=null)
			currentState.pointerUp(x, y, pId);
	}
	
	@Override
	public void keyDown(int key) {
		
		if(currentState!=null)
			currentState.keyDown(key);
	}
	
	@Override
	public void keyUp(int key) {
		
		if(currentState!=null)
			currentState.keyUp(key);
	}
	
	
}
