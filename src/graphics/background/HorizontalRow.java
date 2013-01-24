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
	private float startOffset = 0;
	private float start;
	private boolean useSpacer = true;
	public HorizontalRow(HorizontalDrawablePool pool) {
		this.pool = pool;
		row = new LinkedList<HorizontalDrawableInterface>();
	}
	
	public void setStart(float start){
		this.startOffset = start;
		this.start = startOffset;
	}

	public void setSpacerWidth(float min, float max){
		if( min == 0 && max == 0){
			useSpacer = false;
			return;
		}
		spacerWidthMin = min;
		spacerWidthRange = max-min;
		useSpacer = true;
	}
	
	
	public void add(HorizontalDrawableInterface drawable){
		drawable.setOffset(start);
		row.add(drawable);
		start += drawable.getWidth();
		
		// add new spacer
		if( useSpacer ){
			HorizontalDrawableInterface item = new Spacer((float) (spacerWidthMin + Math.random() *spacerWidthRange));
			row.add(item);
			start += item.getWidth();
		}
		
	}
	
	public void draw(GraphicsTranslator graphics, Default2DGraphics graphics2D, float offset){
		
		start = startOffset;
		for( HorizontalDrawableInterface item : row){
			if( start + item.getWidth() > offset - drawingWidth && start < offset + drawingWidth ){
				item.draw(graphics, graphics2D);
			}
			
			start += item.getWidth();
			
		}
		while( start < offset + drawingWidth ){
			// add and draw new house
			HorizontalDrawableInterface item = pool.getRandom().copy();
			add(item);
			item.draw(graphics, graphics2D);
		}
	}

}
