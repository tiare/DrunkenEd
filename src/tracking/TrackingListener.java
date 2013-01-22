package tracking;

public interface TrackingListener {

	public void onDrink();
	
	public void onBend(float bending);

	public void userLost();
}
