package control;

public class GameSettings {
	public static final int GAME_EASY = 0, GAME_MEDIUM = 1, GAME_HARD = 2;
	public int difficulty;
	public float maxSpeed = 0.7f;
	
	public float fallingAngle[] =
		{(float)Math.toRadians(90),
		(float)Math.toRadians(80),
		(float)Math.toRadians(70)};
	
	public GameSettings () {
		
	}
}
