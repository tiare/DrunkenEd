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
	public static Texture STOOL;
	public static Texture BAR;
	public static Texture BLACKBOARD;
	public static Texture ED;
	public static Texture NO_ED;
	public static Texture HOUSE1;
	public static Texture LANTERN;
	
	public static Texture TREE1;

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
		STOOL = gfxLoader.getImage("hocker");
		BAR = gfxLoader.getImage("theke");
		BLACKBOARD = gfxLoader.getImage("blackboard");
		ED = gfxLoader.getImage("ed");
		NO_ED = gfxLoader.getImage("ed_silhouette");
		HOUSE1 = gfxLoader.getImage("house1");
		LANTERN = gfxLoader.getImage("lantern");
		
		TREE1 = gfxLoader.getImage("tree1");

		//FONT_BELLIGERENT_MADNESS_CLEAN = new FontBelligerentMadnessClean();
		//FONT_BELLIGERENT_MADNESS_CLEAN.init(graphics, 2, 6);
		//FONT_BELLIGERENT_MADNESS = new FontBelligerentMadness();
		//FONT_BELLIGERENT_MADNESS.init(graphics, 2, 6);
		FONT_BELLIGERENT_MADNESS_BOLD = new FontBelligerentMadnessBold();
		FONT_BELLIGERENT_MADNESS_BOLD.init(graphics, 2, 6);

		Skeleton.CURSOR_TEXTURE = CIRCLE;
	}
	
}
