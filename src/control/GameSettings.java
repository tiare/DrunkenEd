package control;

public class GameSettings {
	public static final int GAME_EASY = 0, GAME_MEDIUM = 1, GAME_HARD = 2;
	public int difficulty;
	public float difficultyAddition = 0.25f;
	
	public boolean speedIsProportionalToBending = false;
	public float maxSpeed = 3.0f;
	public float speedAccelerationFactor = 0.5f;
	public float flailingArmsSpeedFactor = 0.9f;
	public float swingingArmsBendFactor = 0.9f;
	
	
	public boolean useGravity = true;
	public float gravityFactor = 0.02f;
	
	public float zoomFrequencyFactor = 1.3f;
	public float zoomIntensityFactor = 1.0f;
	
	public float drunkenBendingFactor = 1.4f;
	
	public float dyingTimeout = 2.f;
	
	public float fallingAngle[] =
		{(float)Math.toRadians(90),
		(float)Math.toRadians(80),
		(float)Math.toRadians(70)};
	
	public GameSettings () {
		
	}
}
