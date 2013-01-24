package graphics.background;

import java.util.LinkedList;

import control.Debug;
import control.GameSettings;

import graphics.defaults.Default2DGraphics;
import graphics.translator.GraphicsTranslator;

public class HorizontalRow {

	private HorizontalDrawablePool pool;
	private float drawingWidth = 4.0f;
	private float spacerWidthMin = 0.4f;
	private float spacerWidthRange = 0.4f;
	private LinkedList<HorizontalDrawableInterface> row;
	public HorizontalRow(HorizontalDrawablePool pool) {
		this.pool = pool;
		row = new LinkedList<HorizontalDrawableInterface>();
	}

	public void setSpacerWidth(float min, float max){
		spacerWidthMin = min;
		spacerWidthRange = max-min;
	}
	
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D, float offset){
		
		float start = 0;
		for( HorizontalDrawableInterface item : row){
			if( start + item.getWidth() > offset - drawingWidth && start < offset + drawingWidth ){
				item.draw(graphics, graphics2D);
			}
			
			start += item.getWidth();
			
		}
		while( start < offset + drawingWidth ){
			// add and draw new house
			HorizontalDrawableInterface item = pool.getRandom().copy();
			item.setOffset(start);
			row.add(item);
			item.draw(graphics, graphics2D);
			start += item.getWidth();
			
			// add new spacer
			item = new Spacer((float) (spacerWidthMin + Math.random() *spacerWidthRange));
			row.add(item);
			start += item.getWidth();
		}
	}

}
