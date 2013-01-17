package graphics;

import graphics.AbstractGFXLoader;
import graphics.translator.GraphicsTranslator;
import graphics.translator.Texture;
import graphics.translator.TextureFilter;
import graphics.translator.TextureSettings;
import graphics.translator.TextureWrap;

public class StandardTextures {

	public static Texture CUBE;
	public static Texture CIRCLE;
	public static Texture STREET;
	
	public static void init(GraphicsTranslator graphics) {
		AbstractGFXLoader gfxLoader = graphics.mGFXLoader;
		CUBE = gfxLoader.getImage("cube");
		CIRCLE = gfxLoader.getImage("circle");
		STREET = gfxLoader.getImage("street",new TextureSettings(TextureWrap.REPEAT,TextureWrap.REPEAT));
	}
	
}
