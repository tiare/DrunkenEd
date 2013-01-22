package graphics.background;

import graphics.defaults.Default2DGraphics;
import graphics.translator.GraphicsTranslator;
import graphics.translator.Texture;

public class House extends ColorableHorizontalDrawable{

	public Texture texture;
	private float height;
	
	
	public House copy( ){
		
		House house = new House(texture);
		super.copyInto(house);
		house.height = height;
		
		return house;
	}
	
	public House(Texture tex) {
		this.texture = tex;
		height = tex.getHeight() / 60;
		width = tex.getWidth() / 60;
		
	}
	
	@Override
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D){
		graphics.bindTexture(texture);
		graphics2D.setColor(color[0], color[1], color[2]);
		graphics2D.drawRect(offset, yOffset, offset+width, yOffset+height, 1, 1, 0, 0);
		graphics.bindTexture(null);
	}
	

}
