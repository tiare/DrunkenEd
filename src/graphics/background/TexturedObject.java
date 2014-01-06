package graphics.background;

import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;

public class TexturedObject extends HorizontalDrawable{

	public Texture texture;
	protected float height;
	protected float color[] = {1f, 1f, 1f};

	public TexturedObject copyInto(TexturedObject drawable ){
		super.copyInto(drawable);
		drawable.height = height;

		drawable.color[0] = color[0];
		drawable.color[1] = color[1];
		drawable.color[2] = color[2];

		return drawable;
	}

	@Override
	public HorizontalDrawableInterface copy( ){
		return copyInto(new TexturedObject(texture));
	}

	public void setColor(float r, float g, float b){
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}

	public TexturedObject(Texture tex) {
		this.texture = tex;
		height = tex.getHeight() / 80.0f;
		width = tex.getWidth() / 80.0f;
	}

	@Override
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D){
		graphics.bindTexture(texture);
		graphics2D.setColor(color[0], color[1], color[2]);
		graphics2D.drawRect(offset, yOffset, offset+width, yOffset+height, 0, 1, 1, 0);
	}
}
