package control.states;

import java.text.DecimalFormat;

import model.Obstacle;

import graphics.StandardTextures;
import graphics.background.HorizontalDrawablePool;
import graphics.background.HorizontalRow;
import graphics.background.TexturedObject;

public class LevelState extends WorldState {

	private final static int OBSTACLE_COUNT = 128;
	protected float worldZoom;
	protected HorizontalRow houseRow;
	protected HorizontalRow streetRow;
	protected HorizontalRow streetItemRow;
	protected Obstacle[] obstacles;
	protected boolean useObstacles = true;
	
	protected TexturedObject moes;

	protected DecimalFormat df;
	protected float brightness;
	
	public LevelState() {
		worldZoom = 2;
		df = new DecimalFormat(",#0.0");
	}
	
	public void onStart() {
		brightness = 1;
	}
	
	public void derivedInit() {
		// configure random houses
		HorizontalDrawablePool pool = new HorizontalDrawablePool();
		
		TexturedObject to;
		pool.add(new TexturedObject(StandardTextures.HOUSE1));
		to = new TexturedObject(StandardTextures.HOUSE1);
		to.setColor(0.25f, 0.2f, 0.2f);
		pool.add(to);

		to = new TexturedObject(StandardTextures.HOUSE1);
		to.setColor(0.2f, 0.2f, 0.2f);
		pool.add(to);

		to = new TexturedObject(StandardTextures.HOUSE1);
		to.setColor(0.2f, 0.25f, 0.2f);
		pool.add(to);

		to = new TexturedObject(StandardTextures.HOUSE1);
		to.setColor(0.2f, 0.2f, 0.25f);
		pool.add(to);

		houseRow = new HorizontalRow(pool);
		houseRow.setStart(-2.5f);
		
		// add moes tavern only once
		moes = new TexturedObject(StandardTextures.MOES);
		
		houseRow.add(moes);
		

		// configure random trees
		float yOffset = +0.17f;
		pool = new HorizontalDrawablePool();
		//Tree t = new Tree();
		// t.setColor(0.2f, 0.2f, 0.2f);
		//pool.add(t);
		to = new TexturedObject(StandardTextures.TREE1);
		to.setYOffset(yOffset);
		pool.add(to);
		

		to = new TexturedObject(StandardTextures.TREE2);
		to.setYOffset(yOffset);
		pool.add(to);
		
		// add Lantern to pool
		to = new TexturedObject(StandardTextures.LANTERN);
		to.setColor(0.2f, 0.2f, 0.2f);
		to.setYOffset(yOffset+0.1f);
		pool.add(to);

		streetItemRow = new HorizontalRow(pool);
		streetItemRow.setSpacerWidth(0.3f, 2.7f);
		streetItemRow.setStart(3.0f);
		
		
		// street stuff
		pool = new HorizontalDrawablePool();
		
		to = new TexturedObject(StandardTextures.STREET);
		to.setYOffset(-4.16f);
		pool.add(to);
		streetRow = new HorizontalRow(pool);
		streetRow.setSpacerWidth(0, 0);
		streetRow.setStart(-10);
	}
	
	@Override
	protected void onStep(float deltaTime) {
		
	}

	@Override
	protected void onDraw() {
		float c = 0.82f*brightness*programController.getBrightness();
		c = 0.01f;
		graphics.clear(c, c, c);
		
		graphics.bindTexture(null);
		graphics2D.setWhite();

		graphics.setAmbientColor(programController.getBrightness()*brightness);
		
		super.drawBackground(2,0.35f);
		
		//graphics2D.setShaderProgram(StandardTextures.DRUNKEN_SHADER);
		// draw houses
		
		houseRow.draw(graphics, graphics2D, camera.getX(), worldZoom);
		
		// draw street
		streetRow.draw(graphics, graphics2D, camera.getX(), worldZoom);
		/*graphics2D.setWhite();
		graphics.bindTexture(StandardTextures.STREET);
		float streetWidth = 10;
		graphics2D.drawRectCentered(player.posX, -1.0f, streetWidth, 2.75f,
				0.0f, 4 * (2 * player.posX / streetWidth - 1), 1, 4 * (2
						* player.posX / streetWidth + 1), 0);
		graphics.bindTexture(null);*/
		
		
		// draw trees lanterns and banks
		streetItemRow.draw(graphics, graphics2D, camera.getX(), worldZoom);
	}
	
	protected void placeObstacles(float offset,float factor) {
		if(useObstacles) {
			obstacles = new Obstacle[OBSTACLE_COUNT];
			float x = offset/4;
			for(int i=0;i<OBSTACLE_COUNT;i++) {
				x += (float)Math.random() * factor + offset;
				obstacles[i] = new Obstacle(x);
			}
		}
	}
	
	protected void drawObstacles(boolean drawHint) {
		if(useObstacles) {
			graphics.bindTexture(StandardTextures.BRICKS);
			graphics2D.setWhite();
			graphics2D.setDefaultProgram();
			boolean first = true;
			for(Obstacle obstacle:obstacles) {
				if(obstacle.posX > camera.getX()+graphics.mRatioX*camera.getZoom()*1.5f)
					break;
				if(obstacle.posX > camera.getX()-graphics.mRatioX*camera.getZoom()*1.5f) {
					graphics2D.drawRectCentered(obstacle.posX, 0, obstacle.height);
					if(first && drawHint) {
						graphics2D.setColor(MainMenuState.HELP_COLOR2);
						graphics2D.drawStringC(obstacle.posX, 0.9f, 0.4f+pulse(5,0.1f), "Jump!");
						graphics.bindTexture(StandardTextures.BRICKS);
					}
				}
				first = false;
			}
		}
	}

}
