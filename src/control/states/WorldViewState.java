package control.states;

import yang.events.Keys;
import control.ProgramState;

public class WorldViewState extends LevelState {

	private float moveX,moveY;

	public WorldViewState() {
		super();
		moveX = 0;
		moveY = 0;
	}

	@Override
	public void onStart() {
		super.onStart();
		worldZoom = 2;
		camera.set(0, 0, worldZoom);
	}

	@Override
	public void onStep(float deltaTime) {
		super.onStep(deltaTime);
		camera.setZoom(worldZoom);
		camera.mTarPos.mX += moveX*deltaTime*worldZoom;
		camera.mTarPos.mY += moveY*deltaTime*worldZoom;
	}

	@Override
	public void onDraw() {
		super.onDraw();
	}

	@Override
	public void keyDown(int key) {
		float precMove = 0.2f;
		if(key == Keys.RIGHT)
			moveX = precMove;
		if(key == Keys.LEFT)
			moveX = -precMove;
		if(key == Keys.UP)
			moveY = precMove;
		if(key == Keys.DOWN)
			moveY = -precMove;
		if(key == 'd')
			moveX = 1;
		if(key == 'a')
			moveX = -1;
		if(key == 'w')
			moveY = 1;
		if(key == 's')
			moveY = -1;
	}

	@Override
	public void keyUp(int key) {
		if(key == Keys.RIGHT || key==Keys.LEFT || key=='d' || key=='a')
			moveX = 0;
		if(key == Keys.UP || key==Keys.DOWN || key=='w' || key=='s')
			moveY = 0;
	}

	@Override
	public int getType() {
		return ProgramState.GAME;
	}

	@Override
	public void zoom(float value) {
		worldZoom += value;
	}

}
