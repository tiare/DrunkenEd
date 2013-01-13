package control;

public class GameSettings {
	public static final int GAME_EASY = 0, GAME_MEDIUM = 1, GAME_HARD = 2;
	public int difficulty;
	public float maxSpeed = 0.7f;
	
	public float fallingAngle[] =
		{(float)Math.toRadians(60),
		(float)Math.toRadians(50),
		(float)Math.toRadians(45)};
	
	public GameSettings () {
		
	}
}
