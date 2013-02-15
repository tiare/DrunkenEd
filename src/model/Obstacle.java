package model;

public class Obstacle {

	public float posX;
	public float width;
	public float height;
	
	public Obstacle(float x,float width,float height) {
		this.posX = x;
		this.width = width;
		this.height = height;
	}
	
	public Obstacle(float x) {
		this(x,0.85f,0.85f);
	}

	public float getLeft() {
		return posX-width/2;
	}
	
	public float getRight() {
		return posX+width/2;
	}
	
}
