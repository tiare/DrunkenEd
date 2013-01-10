package control;

import control.states.*;

public class Config {
	
	/**
	 * Called before initialization of program. Can be used for settings.
	 */
	public static void preInitialize() {
		
	}
	
	/**
	 * Called after initialization
	 * @return the state to start
	 */
	public static ProgramState getStartState(ProgramController programController) {
		//return new FigureTestState();
		//return new MainMenuState();
		return new GameState();
	}
	
}
