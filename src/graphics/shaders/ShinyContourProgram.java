package graphics.shaders;

import ninja.game.model.StandardTextures;
import graphics.AbstractGFXLoader;
import graphics.defaults.DefaultProgram;
import graphics.programs.BasicProgram;
import graphics.programs.GLProgramFactory;
import graphics.translator.GraphicsTranslator;

public class ShinyContourProgram extends BasicProgram{
	
	public int fsNoiseTexSamplerHandle;

	@Override
	public void initHandles() {
		fsNoiseTexSamplerHandle = mProgram.getUniformLocation("texSamplerNoise");
	}
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return DefaultProgram.VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("shiny_contour_fragment");
	}
	
	@Override
	public void bindTexture(int texId, int level) {
		super.bindTexture(texId, level);
	}
	
	public void bindBuffers(GraphicsTranslator graphics) {
		super.bindBuffers(graphics);
		mProgram.setUniformInt(fsNoiseTexSamplerHandle, 1);
	}
	
	@Override
	public void activate() {
		super.activate();
		mGraphics.bindTexture(StandardTextures.PERLIN,1);
	}
	
}
