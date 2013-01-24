package graphics.background;

import graphics.defaults.Default2DGraphics;
import graphics.translator.GraphicsTranslator;

public class Spacer implements HorizontalDrawableInterface {

	private float width;
	public Spacer(float width) {
		this.width = width;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public void setOffset(float offset) {
	}

	@Override
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2d) {
	}

	@Override
	public Spacer copy() {
		return new Spacer(this.width);
	}
}
