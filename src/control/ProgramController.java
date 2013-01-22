package control;


import tracking.AbstractTracking;
import tracking.CameraTracking;
import tracking.FakedTracking;
import control.states.MainMenuState;
import graphics.StandardTextures;
import graphics.defaults.DefaultSurface;
import graphics.events.Keys;
import graphics.events.PointerEvent;

public class ProgramController extends DefaultSurface {

	private ProgramState currentState;
	public AbstractTracking tracking;
	private float programTimer;
	public boolean started;
	private boolean running;
	public Highscores highscores;
	public GameSettings gameSettings;
	public static final int MENU = 0, GAME = 1, GAMEOVER = 2;
	
	public ProgramController() {
		super(true,false,true);
		started = false;
		programTimer = 0;

		if(Debug.FAKE_CONTROLS)
			tracking = new FakedTracking(this);
		else
			tracking = new CameraTracking(this);
		running = true;

		
		highscores = new Highscores();
		gameSettings = new GameSettings();
	}
	
	public void start() {
		
		StandardTextures.init(mGraphics);
		
		switchState( Config.getStartState(this).init(this) );
		programTimer = 0;
		tracking.init();
		
		
		
		new Thread() {
			@Override
			public void run() {
				while(running) {
					long startTime = System.currentTimeMillis();
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					float deltaTime = (System.currentTimeMillis()-startTime)*0.001f;
					tracking.step(deltaTime);
					ProgramController.super.step();
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
		tracking.restart();
		if(currentState!=null) {
			currentState.onStop();
		}
		currentState = newState;
		currentState.onStart();
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
	public void pointerDown(float x,float y,PointerEvent event) {
		if(currentState!=null)
			currentState.pointerDown(x, y, event.mId);
	}
	
	@Override
	public void pointerDragged(float x,float y,PointerEvent event) {
		if(currentState!=null)
			currentState.pointerDragged(x, y, event.mId);
	}
	
	@Override
	public void pointerUp(float x,float y,PointerEvent event) {
		if(currentState!=null)
			currentState.pointerUp(x, y, event.mId);
	}
	
	@Override
	public void keyDown(int key) {
		if(key == Keys.ESC) {
			//Only end game if we are in the menu
			if (currentState.getType() == MENU) {
				running = false;
				System.exit(0);
			}
			//Otherwise jump out to the menu
			else {
				switchState(new MainMenuState().init(this));
			}
		}
		if(currentState!=null)
			currentState.keyDown(key);
		if(tracking != null)
			tracking.keyDown(key);
	}
	
	@Override
	public void keyUp(int key) {
		
		if(currentState!=null)
			currentState.keyUp(key);
		
		if(tracking != null)
			tracking.keyUp(key);
	}
	
	public void test() {
		System.out.println("Testing switch");
	}
	
	
}
