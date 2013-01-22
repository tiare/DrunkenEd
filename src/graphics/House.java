package graphics;

import graphics.defaults.Default2DGraphics;
import graphics.translator.GraphicsTranslator;
import graphics.translator.Texture;

public class House {

	public float offset;
	public Texture texture;
	private float height;
	private float width;
	private float yOffset = 0.2f;
	private float color[] = {0.3f, 0.3f, 0.3f};
	public House(float offset, Texture tex) {
		this.offset = offset;
		this.texture = tex;
		height = tex.getHeight() / 60;
		width = tex.getWidth() / 60;
		
	}
	
	public void setColor(float r, float g, float b){
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}
	
	
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D){
		graphics.bindTexture(texture);
		graphics2D.setColor(0.3f, 0.3f, 0.3f);
		graphics2D.drawRect(offset, yOffset, offset+width, height, 1, 1, 0, 0);
		graphics.bindTexture(null);
	}
	

}
