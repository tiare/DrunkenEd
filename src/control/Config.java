package control;

import control.states.TestState;

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
		return new TestState();
	}
	
}
