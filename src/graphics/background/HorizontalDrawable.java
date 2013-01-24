package graphics.background;


public abstract class HorizontalDrawable implements HorizontalDrawableInterface {

	
	public float offset;
	protected float width;
	protected float yOffset = 0.2f;
	protected String name = "horizontalDrawable";
	
	public HorizontalDrawable copyInto( HorizontalDrawable drawable) {
		drawable.offset = offset;
		drawable.width = width;
		drawable.yOffset = yOffset;
		return drawable;
	}

	
	public void setName(String name){
		this.name = name;
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
