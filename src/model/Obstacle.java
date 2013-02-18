package model;

public class Obstacle {

	public static float collFactor = 0.2f;
	public static float collShift = 0.3f;
	public float posX;
	public float width;
	public float height;
	public float collLeft;
	public float collRight;
	
	public Obstacle(float x,float width,float height) {
		this.posX = x;
		this.width = width;
		this.height = height;
		collLeft = x-width*collFactor+collShift;
		collRight = x+width*collFactor+collShift;
	}
	
	public Obstacle(float x) {
		this(x,0.85f,0.95f);
	}

	public float getLeft() {
		return collLeft;
	}
	
	public float getRight() {
		return collRight;
	}
	
}
