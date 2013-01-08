package control;

import control.states.TestState;

public class Config {

	public static ProgramState getStartState(ProgramController programController) {
		return new TestState();
	}
	
}
