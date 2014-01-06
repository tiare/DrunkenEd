package graphics.background;

import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.translator.GraphicsTranslator;

public interface HorizontalDrawableInterface {
	public HorizontalDrawableInterface copy();
	public float getWidth();
	public void setOffset(float offset);
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D);
}
