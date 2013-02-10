package tracking;

public interface TrackingListener {

	public void onDrink();
	
	public void onBend(float bending);
	
	public void onJump(float velocity);

	public void userLost();
}
