package graphics;

import ninja.game.graphics.fonts.FontBelligerentMadnessBold;
import graphics.fonts.FontBelligerentMadnessChalk;
import graphics.shaders.ChalkShader;
import graphics.shaders.DrunkenShader;
import graphics.skeletons.Skeleton;
import graphics.translator.GraphicsTranslator;
import graphics.translator.Texture;
import graphics.translator.TextureFilter;
import graphics.translator.TextureSettings;
import graphics.translator.TextureWrap;

public class StandardTextures {

	
	public static Texture CUBE;
	public static Texture CIRCLE;
	public static Texture PERLIN;
	public static Texture ARROW;
	
	public static Texture CROSS;
	public static Texture STREET;
	public static Texture PHOTO_FRAME;
	public static Texture GAME_BACKGROUND;
	public static Texture STOOL;
	public static Texture ED_SKELETON;
	public static Texture BAR;
	public static Texture TAP;
	public static Texture BLACKBOARD;
	public static Texture WALL;
	public static Texture BRICK_LEFT;
	public static Texture BRICK_RIGHT;
	
	public static Texture DART;
	public static Texture PICTURE1;
	public static Texture PICTURE2;
	public static Texture PICTURE3;
	public static Texture FLAG1;
	public static Texture FLAG2;
	
	public static Texture ARROW_L;
	public static Texture ARROW_R;
	
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
	public static DrunkenShader DRUNKEN_SHADER;
	
	public static void init(GraphicsTranslator graphics) {
		
		TextureSettings defSettings = new TextureSettings(TextureWrap.CLAMP,TextureWrap.CLAMP,TextureFilter.LINEAR_MIP_LINEAR);
		
		AbstractGFXLoader gfxLoader = graphics.mGFXLoader;
		CUBE = gfxLoader.getImage("cube");
		CIRCLE = gfxLoader.getImage("circle",defSettings);
		PERLIN = gfxLoader.getImage("perlin1",TextureFilter.LINEAR_MIP_LINEAR);
		
		ARROW = gfxLoader.getImage("arrow",defSettings);
		PHOTO_FRAME = gfxLoader.getImage("photoFrame",defSettings);
		STREET = gfxLoader.getImage("street",new TextureSettings(TextureWrap.REPEAT,TextureWrap.REPEAT,TextureFilter.LINEAR_MIP_LINEAR));
		GAME_BACKGROUND = gfxLoader.getImage("nightsky",defSettings);
		CROSS = gfxLoader.getImage("cross",defSettings);
		STOOL = gfxLoader.getImage("hocker",defSettings);
		BAR = gfxLoader.getImage("bar",defSettings);
		TAP = gfxLoader.getImage("tap",defSettings);
		BLACKBOARD = gfxLoader.getImage("blackboard",defSettings);
		WALL = gfxLoader.getImage("lowerwall",defSettings);
		BRICK_LEFT = gfxLoader.getImage("brickLeft",defSettings);
		BRICK_RIGHT = gfxLoader.getImage("brickRight",defSettings);
		
		DART = gfxLoader.getImage("dart",defSettings);
		PICTURE1 = gfxLoader.getImage("picture1",defSettings);
		PICTURE2 = gfxLoader.getImage("picture2",defSettings);
		PICTURE3 = gfxLoader.getImage("picture3",defSettings);
		FLAG1 = gfxLoader.getImage("flag1",defSettings);
		FLAG2 = gfxLoader.getImage("flag2",defSettings);
		
		ARROW_L = gfxLoader.getImage("arrow_left", defSettings);
		ARROW_R = gfxLoader.getImage("arrow_right", defSettings);
				
		ED = gfxLoader.getImage("ed",defSettings);
		NO_ED = gfxLoader.getImage("ed_silhouette",defSettings);
		ED_SKELETON = gfxLoader.getImage("skeleton_ed",defSettings);
		
		HOUSE1 = gfxLoader.getImage("house1",defSettings);
		MOES = gfxLoader.getImage("moes",defSettings);
		LANTERN = gfxLoader.getImage("lantern",defSettings);
		
		TREE1 = gfxLoader.getImage("tree1",defSettings);
		TREE2 = gfxLoader.getImage("tree2",defSettings);

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
		DRUNKEN_SHADER = new DrunkenShader();
		DRUNKEN_SHADER.init(graphics, gfxLoader);
		
		Skeleton.CURSOR_TEXTURE = CIRCLE;
	}
	
}
