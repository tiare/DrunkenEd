package control.states;

import figure.DrunkenSkeleton;

public class MainMenuState extends WorldState {
	
//	private float clickPosX = 0.0f;
//	private float clickPosY = 0.0f;

	@Override
	public void onStep(float deltaTime) {
		//camera.set(0, 1, 2);
		DrunkenSkeleton skeleton = (DrunkenSkeleton)player.getSkeleton();
		camera.set(skeleton.mHipJoint.mPosX, skeleton.mHeadJoint.mPosY, 2);
	}

	@Override
	public void onDraw() {
		graphics.clear(1.0f, 0.3f, 0.3f);
		graphics2D.setWhite();
		player.draw();
		////graphics2D.setColor(1.0f, 0.3f, 0.3f);
		//graphics2D.drawRect(5.f, 5.f, 50.f, 50.f);
	}

//	@Override
//	public void pointerDown(float x,float y,int pId) {
//		clickPosX = x;
//		clickPosY = y;
//		System.out.println("Clicked");
//	}
//	
//	@Override
//	public void pointerDragged(float x,float y,int pId) {
//		//player.posX = clickPosX-x;
//		//player.posY = clickPosY-y;
//		System.out.println("Dragged");
//	}
//	
//	@Override
//	public void pointerUp(float x,float y,int pId) {
//		
//	}
	
}
