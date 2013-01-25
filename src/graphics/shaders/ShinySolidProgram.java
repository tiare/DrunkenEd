package graphics.shaders;

import graphics.AbstractGFXLoader;
import graphics.defaults.DefaultProgram;
import graphics.programs.BasicProgram;
import graphics.programs.GLProgramFactory;
import graphics.translator.GraphicsTranslator;

public class ShinySolidProgram extends BasicProgram{
	
	public int fsNoiseTexSamplerHandle;

	@Override
	public void init(GLProgramFactory programFactory,AbstractGFXLoader gfxLoader) {
		super.init(programFactory, gfxLoader);
		fsNoiseTexSamplerHandle = mProgram.getUniformLocation("texSamplerNoise");
	}
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return DefaultProgram.VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("shiny_solid_fragment");
	}
	
	@Override
	public void bindTexture(int texId, int level) {
		super.bindTexture(texId, level);
	}
	
	public void bindBuffers(GraphicsTranslator graphics) {
		super.bindBuffers(graphics);
		mProgram.setUniformInt(fsNoiseTexSamplerHandle, 1);
	}
	
}
