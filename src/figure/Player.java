package figure;

import graphics.defaults.Default2DGraphics;
import graphics.skeletons.Skeleton;
import graphics.skeletons.SkeletonCarrier;
import graphics.translator.GraphicsTranslator;

public class Player implements SkeletonCarrier {

	private float speed;
	private DrunkenSkeleton skeleton;
	private GraphicsTranslator graphics;
	private Default2DGraphics graphics2D;
	public float posX,posY;
	
	public Player() {
		
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void draw() {
		
	}

	@Override
	public Default2DGraphics getGraphics() {
		return null;
	}

	@Override
	public int getLookDirection() {
		return 1;
	}

	@Override
	public float getScale() {
		return 1;
	}

	@Override
	public Skeleton getSkeleton() {
		return null;
	}

	@Override
	public float getWorldX() {
		return posX;
	}

	@Override
	public float getWorldY() {
		return posY;
	}
	
	public void setWorldPosition(float x,float y) {
		posX = x;
		posY = y;
	}

	@Override
	public void setSkeleton(Skeleton skeleton) {
		
	}
	
}
