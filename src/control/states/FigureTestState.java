package control.states;


public class FigureTestState extends WorldState{
	
	public FigureTestState() {
		super();
	}

	@Override
	public void onStep(float deltaTime) {
		camera.set(0, 1, 2);
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();
		player.draw();
	}

}
