package graphics.background;

import graphics.defaults.Default2DGraphics;
import graphics.translator.GraphicsTranslator;
import graphics.translator.Texture;

public class House implements HorizontalDrawable{

	public float offset;
	public Texture texture;
	private float height;
	private float width;
	private float yOffset = 0.2f;
	private float color[] = {0.3f, 0.3f, 0.3f};
	
	
	public House copy( ){
		House house = new House(texture);
		house.offset = offset;
		house.height = height;
		house.width = width;
		house.yOffset = yOffset;
		house.color[0] = color[0];
		house.color[1] = color[1];
		house.color[2] = color[2];
		return house;
	}
	
	public House(Texture tex) {
		this.texture = tex;
		height = tex.getHeight() / 60;
		width = tex.getWidth() / 60;
		
	}
	
	@Override
	public float getWidth(){
		return width;
	}
	
	public void setColor(float r, float g, float b){
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}
	
	@Override
	public void setOffset(float offset){
		this.offset = offset;
	}
	
	@Override
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D){
		graphics.bindTexture(texture);
		graphics2D.setColor(color[0], color[1], color[2]);
		graphics2D.drawRect(offset, yOffset, offset+width, yOffset+height, 1, 1, 0, 0);
		graphics.bindTexture(null);
	}
	

}
