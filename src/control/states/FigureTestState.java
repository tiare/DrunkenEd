package control.states;


public class FigureTestState extends WorldState{
	
	public FigureTestState() {
		super();
	}

	@Override
	public void onStep(float deltaTime) {
		camera.set(1, 1, 2);
		player.step(deltaTime);
	}

	@Override
	public void onDraw() {
		graphics.clear(0.3f, 0.3f, 0.3f);
		graphics2D.setWhite();
		player.draw();
	}
	
	@Override
	public void keyDown(int code) {
		//System.out.println(code);
		if(code == 100) {
			player.fallDown();
		}
	}

	@Override
	public void userLost() {
		// TODO Auto-generated method stub
		
	}

}
