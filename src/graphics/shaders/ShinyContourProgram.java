package graphics.shaders;

import ninja.game.model.StandardTextures;
import graphics.AbstractGFXLoader;
import graphics.programs.BasicProgram;

public class ShinyContourProgram extends BasicProgram{
	
	public int fsNoiseTexSamplerHandle;

	@Override
	public void initHandles() {
		super.initHandles();
		fsNoiseTexSamplerHandle = mProgram.getUniformLocation("texSamplerNoise");
	}
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return BasicProgram.VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("shiny_contour_fragment");
	}
	
	@Override
	public void activate() {
		super.activate();
		mProgram.setUniformInt(fsNoiseTexSamplerHandle, 1);
		mGraphics.bindTexture(StandardTextures.PERLIN,1);
	}
	
}
