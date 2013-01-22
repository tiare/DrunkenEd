package graphics.background;

import graphics.defaults.Default2DGraphics;
import graphics.translator.GraphicsTranslator;

public interface HorizontalDrawable {
	public HorizontalDrawable copy();
	public float getWidth();
	public void setOffset(float offset);
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D);
}
