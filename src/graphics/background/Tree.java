package graphics.background;

import graphics.defaults.Default2DGraphics;
import graphics.translator.GraphicsTranslator;

public class Tree extends ColorableHorizontalDrawable {

	public Tree() {
		this.width = 1.4f;
	}

	@Override
	public HorizontalDrawable copy() {
		Tree t = new Tree();
		super.copyInto(t);
		return t;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public void setOffset(float offset) {
		this.offset = offset;

	}

	@Override
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D) {
		
		graphics2D.setColor(0.3f, 0.1f, 0.0f);
		graphics2D.drawRectCentered(offset,yOffset+1, 0.2f,2.0f, 0);
		graphics2D.setColor(0.0f, 0.66f, 0.0f);
		graphics2D.drawRectCentered(offset,yOffset+2.5f, 1.0f,1.0f, (float)Math.PI/3.0f);
		graphics2D.drawRectCentered(offset,yOffset+2.5f, 1.0f,1.0f, (float)Math.PI/2.0f);
		graphics2D.drawRectCentered(offset,yOffset+2.5f, 1.0f,1.0f, (float)Math.PI/5.0f);
	}
}
