package figure;

import control.ProgramController;
import graphics.defaults.Default2DGraphics;
import graphics.skeletons.Skeleton;
import graphics.skeletons.SkeletonCarrier;
import graphics.translator.GraphicsTranslator;

public class Player implements SkeletonCarrier {

	private float velX,velY;
	private ProgramController programController;
	private DrunkenSkeleton skeleton;
	private GraphicsTranslator graphics;
	private Default2DGraphics graphics2D;
	public float posX,posY;
	
	public float bending;
	
	public Player() {
		skeleton = new DrunkenSkeleton();
		bending = 0;
	}
	
	public Player init(ProgramController programController) {
		this.programController = programController;
		this.graphics = programController.mGraphics;
		this.graphics2D = programController.mGraphics2D;
		skeleton.init(this);
		return this;
	}
	
	public void setSpeedX(float speed) {
		this.velX = speed;
	}
	
	public float getSpeed() {
		return velX;
	}
	
	public void draw() {
		skeleton.refreshVisualVars();
		skeleton.draw();
	}

	@Override
	public Default2DGraphics getGraphics() {
		return graphics2D;
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
		return skeleton;
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

	public void step(float deltaTime) {
		posX += velX*deltaTime;
		posY += velY*deltaTime;
		
		//skeleton.mBreastJoint.mPosX = skeleton.mHipJoint.mPosX + (float)(skeleton.mBodyBone.mDistance*Math.sin(bending));
		//skeleton.mBreastJoint.mPosY = skeleton.mHipJoint.mPosY + (float)(skeleton.mBodyBone.mDistance*Math.cos(bending));
		skeleton.mBreastJoint.setPosByAngle(skeleton.mHipJoint, skeleton.mBodyBone, bending);
		skeleton.mHipJoint.mFixed = true;
		for(int i=0;i<3;i++)
			skeleton.applyConstraints();
		
	}
	
}
