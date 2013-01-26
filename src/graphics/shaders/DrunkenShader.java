package graphics.shaders;


import graphics.AbstractGFXLoader;
import graphics.defaults.DefaultProgram;
import graphics.programs.BasicProgram;

public class DrunkenShader extends BasicProgram{
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return DefaultProgram.VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("drunken_fragment");
	}
	
}
