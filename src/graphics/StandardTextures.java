package graphics;

import sun.security.x509.EDIPartyName;
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
	public static Texture BEER;
	public static Texture WINE;
	public static Texture VODKA;
	public static Texture ED;
	public static Texture HOUSE1;
	
	public static void init(GraphicsTranslator graphics) {
		AbstractGFXLoader gfxLoader = graphics.mGFXLoader;
		CUBE = gfxLoader.getImage("cube");
		CIRCLE = gfxLoader.getImage("circle");
		STREET = gfxLoader.getImage("street",new TextureSettings(TextureWrap.REPEAT,TextureWrap.REPEAT));
		BEER = gfxLoader.getImage("beer");
		WINE = gfxLoader.getImage("wine");
		VODKA = gfxLoader.getImage("vodka");
		ED = gfxLoader.getImage("ed");
		HOUSE1 = gfxLoader.getImage("house1");
	}
	
}
