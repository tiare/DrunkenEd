package control.states;

import control.ProgramState;
import graphics.StandardTextures;

public class TestState extends ProgramState{

	@Override
	public void onStep(float deltaTime) {
		
	}

	@Override
	public void onDraw() {
		graphics2D.setWhite();
		
		graphics.bindTexture(StandardTextures.CUBE);
		graphics2D.drawRectCentered(0,0.15f, 1,0.9f, stateTimer);
		
		graphics2D.setColor(0.5f, 0.5f, 0.85f);
		graphics2D.drawStringL(graphics2D.getScreenLeft()+0.03f, 0.9f, 0.1f, "Time:  "+(int)(stateTimer*10)/10f+"sec");
	}

}
