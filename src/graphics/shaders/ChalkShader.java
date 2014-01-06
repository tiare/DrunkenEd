package graphics.shaders;


import yang.graphics.programs.BasicProgram;
import yang.graphics.translator.AbstractGFXLoader;
import graphics.StandardTextures;

public class ChalkShader extends BasicProgram{

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
		return gfxLoader.getShader("chalk_fragment");
	}

	@Override
	public void activate() {
		super.activate();
		mProgram.setUniformInt(fsNoiseTexSamplerHandle, 1);
		mGraphics.bindTexture(StandardTextures.PERLIN,1);
	}

}
