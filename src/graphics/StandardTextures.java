package graphics;

import graphics.AbstractGFXLoader;
import graphics.translator.GraphicsTranslator;
import graphics.translator.Texture;

public class StandardTextures {

	public static Texture CUBE;
	public static Texture CIRCLE;
	
	public static void init(GraphicsTranslator graphics) {
		AbstractGFXLoader gfxLoader = graphics.mGFXLoader;
		CUBE = gfxLoader.getImage("cube");
		CIRCLE = gfxLoader.getImage("circle");
	}
	
}
