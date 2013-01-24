package graphics;

import ninja.game.graphics.fonts.FontBelligerentMadnessBold;
import graphics.fonts.FontBelligerentMadness;
import graphics.fonts.FontBelligerentMadnessClean;
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
	public static Texture HOUSE1;
	public static AbstractFont FONT_BELLIGERENT_MADNESS_CLEAN;
	public static AbstractFont FONT_BELLIGERENT_MADNESS;
	public static AbstractFont FONT_BELLIGERENT_MADNESS_BOLD;
	
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
		
		//FONT_BELLIGERENT_MADNESS_CLEAN = new FontBelligerentMadnessClean();
		//FONT_BELLIGERENT_MADNESS_CLEAN.init(graphics, 2, 6);
		//FONT_BELLIGERENT_MADNESS = new FontBelligerentMadness();
		//FONT_BELLIGERENT_MADNESS.init(graphics, 2, 6);
		FONT_BELLIGERENT_MADNESS_BOLD = new FontBelligerentMadnessBold();
		FONT_BELLIGERENT_MADNESS_BOLD.init(graphics, 2, 6);

		Skeleton.CURSOR_TEXTURE = CIRCLE;
	}
	
}
