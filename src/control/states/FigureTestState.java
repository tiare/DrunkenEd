package control.states;


public class FigureTestState extends WorldState{
	
	public FigureTestState() {
		super();
	}

	@Override
	public void onStep(float deltaTime) {
		camera.set(1, 1, 2);
		camera.setRotation(stateTimer);
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();
		player.draw();
	}
	
	@Override
	public void onDrink() {
		System.out.println("fnelgnerk.ge,r");
	}

}
