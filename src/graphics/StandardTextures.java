package graphics;

import graphics.skeletons.Skeleton;
import graphics.translator.GraphicsTranslator;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;
import graphics.translator.TextureWrap;

public class StandardTextures {

	public static Texture CUBE;
	public static Texture CIRCLE;
	public static Texture STREET;
	public static Texture BEER;
	public static Texture WINE;
	public static Texture VODKA;
	public static Texture ED;
	
	public static void init(GraphicsTranslator graphics) {
		AbstractGFXLoader gfxLoader = graphics.mGFXLoader;
		CUBE = gfxLoader.getImage("cube");
		CIRCLE = gfxLoader.getImage("circle");
		STREET = gfxLoader.getImage("street",new TextureSettings(TextureWrap.REPEAT,TextureWrap.REPEAT));
		BEER = gfxLoader.getImage("beer");
		WINE = gfxLoader.getImage("wine");
		VODKA = gfxLoader.getImage("vodka");
		ED = gfxLoader.getImage("ed");
		
		Skeleton.CURSOR_TEXTURE = CIRCLE;
	}
	
}
