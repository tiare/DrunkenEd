package control;

public class GameSettings {
	public static final int GAME_EASY = 0, GAME_MEDIUM = 1, GAME_HARD = 2;
	public int difficulty;
	public float difficultyAddition = 0.25f;
	
	public boolean speedIsProportionalToBending = false;
	public float speedFactor = 9.0f;
	public float maxSpeed = 5.5f;
	public float speedAccelerationFactor = 0.7f;
	public float flailingArmsSpeedFactor = 0.86f;
	public float swingingArmsBendFactor = 0.8f;
	
	
	public boolean useGravity = true;
	public float gravityFactor = 0.015f;
	
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
