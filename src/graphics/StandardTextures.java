package graphics;

import ninja.game.graphics.fonts.FontBelligerentMadnessBold;
import graphics.fonts.FontBelligerentMadnessChalk;
import graphics.shaders.ChalkShader;
import graphics.skeletons.Skeleton;
import graphics.translator.GraphicsTranslator;
import graphics.translator.Texture;
import graphics.translator.TextureSettings;
import graphics.translator.TextureWrap;

public class StandardTextures {

	public static Texture CUBE;
	public static Texture CIRCLE;
	public static Texture PERLIN;
	public static Texture STREET;

	public static Texture STOOL;
	public static Texture BAR;
	public static Texture TAP;
	public static Texture BLACKBOARD;
	public static Texture COLUMN;
	public static Texture WALL;
	public static Texture BRICK_LEFT;
	public static Texture BRICK_RIGHT;
	
	public static Texture ED;
	public static Texture NO_ED;
	
	public static Texture HOUSE1;
	public static Texture MOES;
	public static Texture LANTERN;
	
	public static Texture TREE1;
	public static Texture TREE2;

	public static AbstractFont FONT_BELLIGERENT_MADNESS_CHALK;
	public static AbstractFont FONT_BELLIGERENT_MADNESS_BOLD;
	
	public static ChalkShader CHALK_SHADER;

	
	public static void init(GraphicsTranslator graphics) {
		
		AbstractGFXLoader gfxLoader = graphics.mGFXLoader;
		CUBE = gfxLoader.getImage("cube");
		CIRCLE = gfxLoader.getImage("circle");
		PERLIN = gfxLoader.getImage("perlin1");
		STREET = gfxLoader.getImage("street",new TextureSettings(TextureWrap.REPEAT,TextureWrap.REPEAT));
		
		STOOL = gfxLoader.getImage("hocker");
		BAR = gfxLoader.getImage("bar");
		TAP = gfxLoader.getImage("tap");
		BLACKBOARD = gfxLoader.getImage("blackboard");
		COLUMN = gfxLoader.getImage("column");
		WALL = gfxLoader.getImage("lowerwall");
		BRICK_LEFT = gfxLoader.getImage("brickLeft");
		BRICK_RIGHT = gfxLoader.getImage("brickRight");
		
		ED = gfxLoader.getImage("ed");
		NO_ED = gfxLoader.getImage("ed_silhouette");
		
		HOUSE1 = gfxLoader.getImage("house1");
		MOES = gfxLoader.getImage("moes");
		LANTERN = gfxLoader.getImage("lantern");
		
		TREE1 = gfxLoader.getImage("tree1");
		TREE2 = gfxLoader.getImage("tree2");

		//FONT_BELLIGERENT_MADNESS_CLEAN = new FontBelligerentMadnessClean();
		//FONT_BELLIGERENT_MADNESS_CLEAN.init(graphics, 2, 6);
		//FONT_BELLIGERENT_MADNESS = new FontBelligerentMadness();
		//FONT_BELLIGERENT_MADNESS.init(graphics, 2, 6);
		FONT_BELLIGERENT_MADNESS_CHALK  = new FontBelligerentMadnessChalk();
		FONT_BELLIGERENT_MADNESS_CHALK.init(graphics, 2, 6);
		FONT_BELLIGERENT_MADNESS_BOLD = new FontBelligerentMadnessBold();
		FONT_BELLIGERENT_MADNESS_BOLD.init(graphics, 2, 6);

		CHALK_SHADER = new ChalkShader();
		CHALK_SHADER.init(graphics, gfxLoader);
		
		Skeleton.CURSOR_TEXTURE = CIRCLE;
	}
	
}
