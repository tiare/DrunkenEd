package control;


import tracking.AbstractTracking;
import tracking.CameraTracking;
import tracking.FakedTracking;
import yang.events.Keys;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.defaults.DefaultSurface;
import yang.math.Geometry;
import yang.math.MathConst;
import yang.model.enums.UpdateMode;
import control.states.MainMenuState;
import graphics.StandardTextures;

public class ProgramController extends DefaultSurface {

	public static float DEFAULT_FADESPEED = 2.5f;
	private ProgramState currentState;
	public AbstractTracking tracking;
	private float programTimer;
	public boolean started;
	//private boolean running;
	public Highscores highscores;
	public GameSettings gameSettings;
	private boolean mFirstDraw;
	public static final int MENU = 0, GAME = 1, GAMEOVER = 2;
	private boolean mChangingState;
	private ProgramState fadeState;
	public boolean markWarning;
	private float fade;
	private float fadeSpeed;

	public ProgramController() {
		super(true,false);
		started = false;
		programTimer = 0;

		if(Debug.FAKE_CONTROLS)
			tracking = new FakedTracking(this);
		else
			tracking = new CameraTracking(this);
		//running = true;
		mChangingState = false;

		highscores = new Highscores();
		gameSettings = new GameSettings();
		super.setUpdatesPerSecond(60);
		super.setUpdateMode(UpdateMode.MANUALLY);
		fade = 1;
		fadeState = null;
	}

	public void start() {

		StandardTextures.init(mGraphics);

		switchState( Config.getStartState(this).init(this) );
		programTimer = 0;
		tracking.init();

//		new Thread() {
//			@Override
//			public void run() {
//				while(running) {
//					long startTime = System.currentTimeMillis();
//					try {
//						Thread.sleep(20);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					float deltaTime = (System.currentTimeMillis()-startTime)*0.001f;
//					step(deltaTime);
//
//				}
//			}
//		}.start();

		started = true;
	}

	@Override
	public void step(float deltaTime) {
		if(markWarning) {
			if(!tracking.trackedUser)
				markWarning = false;
		}
		if(fadeState==null) {
			if(fade<1) {
				fade += deltaTime*fadeSpeed;
				if(fade>1)
					fade = 1;
			}
			tracking.step(deltaTime);
			super.step(deltaTime);
			if(currentState.showMarkWarnings) {
				float limit = markWarning?0.6f:0.98f;
				markWarning = tracking.trackedUser && (Math.abs(tracking.gpareax)>limit || Math.abs(tracking.gpareaz)>limit);
			}
			if(markWarning) {

			}else
				currentState.step(deltaTime);
		}else{
			fade -= deltaTime*fadeSpeed;
			if(fade<0) {
				fade = 0;
				switchState(fadeState);
				fadeState = null;
			}
		}
		programTimer += deltaTime;
	}

	public void fadeToState(ProgramState state) {
		fade = 1;
		fadeSpeed = DEFAULT_FADESPEED;
		fadeState = state;
	}

	@Override
	public void postInitGraphics() {
		start();
	}

	public void switchState(ProgramState newState) {
		if(!newState.isInitialized())
			newState.init(this);
		mChangingState = true;
		tracking.restart();
		mFirstDraw = true;
		if(currentState!=null) {
			currentState.stop();
		}
		currentState = newState;
		currentState.onStart();
		mChangingState = false;
	}

	public ProgramState getCurrentState() {
		return currentState;
	}

	public float getProgramTime() {
		return programTimer;
	}

	@Override
	public void draw() {
		super.catchUp();
		//super.proceed();
		if(currentState!=null && !mChangingState) {
			if(mFirstDraw) {
				currentState.startGraphics();
				mFirstDraw = false;
			}
			mGraphics2D.setTime((int)(programTimer*100));
			mGraphics2D.setColorFactor(getBrightness());
			currentState.draw();
		}

		if(markWarning) {
			mGraphics2D.switchGameCoordinates(false);
			mGraphics2D.setLegacyFont(StandardTextures.FONT_BELLIGERENT_MADNESS_BOLD);
			mGraphics2D.setColorFactor(1);
			mGraphics2D.setColor(1, 0.1f, 0);
			mGraphics2D.drawStringLegacyC(0, 0.74f, 0.15f+(float)Math.abs(Math.sin(programTimer*10))*0.018f, "Step onto the mark!");
			float yOffset = 0.1f;
			mGraphics.bindTexture(StandardTextures.CROSS);
			mGraphics2D.drawRectCentered(0,-yOffset,0.15f);
			float fac = 0.36f;
			mGraphics2D.setColor(0.2f, 0.8f, 0);
			mGraphics.bindTexture(StandardTextures.CIRCLE);
			float x = tracking.gpareax*fac;
			float y = -tracking.gpareaz*fac;
			float a = Geometry.getAngle(x, y);
			y -= yOffset;
			mGraphics2D.drawRectCentered(x,y,0.17f);
			mGraphics.bindTexture(StandardTextures.ARROW);
			float r = 0.135f+(float)Math.abs(Math.sin(programTimer*10)*0.06f);
			float dX = -(float)Math.cos(a) * r;
			float dY = -(float)Math.sin(a) * r;
			mGraphics2D.drawRectCentered(x+dX, y+dY, 0.1f, 0.1f, a-MathConst.PI/2);
			mGraphics2D.setColor(1,1,1);
			mGraphics2D.drawStringLegacyC(x, y, 0.12f, "You");

		}

		mGraphics.flush();
	}

	@Override
	public void pointerDown(float x,float y,SurfacePointerEvent event) {
		if(currentState!=null)
			currentState.pointerDown(x, y, event.mId);
	}

	@Override
	public void pointerDragged(float x,float y,SurfacePointerEvent event) {
		if(currentState!=null)
			currentState.pointerDragged(x, y, event.mId);
	}

	@Override
	public void pointerUp(float x,float y,SurfacePointerEvent event) {
		if(currentState!=null)
			currentState.pointerUp(x, y, event.mId);
	}

	@Override
	public void keyDown(int key) {
		if(key == 'c') {
			Debug.DRAW_SKELETON ^= true;
		}
		if(key == Keys.ESC) {
			//Only end game if we are in the menu
			if (currentState.getType() == MENU) {
				//running = false;
				System.exit(0);
			}
			//Otherwise jump out to the menu
			else {
				switchState(new MainMenuState());
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

	public float getBrightness() {
		if(markWarning)
			return fade * 0.18f;
		else
			return fade;
	}

	@Override
	public void zoom(float value) {
		if(currentState!=null)
			currentState.zoom(value);
	}

}
