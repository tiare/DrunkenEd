package graphics.background;


public abstract class ColorableHorizontalDrawable implements HorizontalDrawable {

	
	public float offset;
	protected float width;
	protected float yOffset = 0.2f;
	protected float color[] = {0.3f, 0.3f, 0.3f};

	public HorizontalDrawable copyInto( ColorableHorizontalDrawable drawable) {
		drawable.offset = offset;
		drawable.width = width;
		drawable.yOffset = yOffset;
		drawable.color[0] = color[0];
		drawable.color[1] = color[1];
		drawable.color[2] = color[2];
		return drawable;
	}

	public void setColor(float r, float g, float b){
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}
	
	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public void setOffset(float offset) {
		this.offset = offset;
	}
}
