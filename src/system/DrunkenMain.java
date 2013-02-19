package system;

import pc.fileio.IOCommon;
import pc.gles.GLESFrame;
import control.Config;
import control.Debug;
import control.ProgramController;

public class DrunkenMain {

	public static void main(String[] args) {
		
		IOCommon.IMAGE_PATH = "textures/";
		IOCommon.SHADER_PATH = "shaders/";
		
		Config.preInitialize();
		
		GLESFrame frame = new GLESFrame("Drunken Ed");
		if(Debug.FULLSCREEN)
			frame.initFullScreen(true);
		else
			frame.init(Debug.RESOLUTION_X,Debug.RESOLUTION_Y,true,!Debug.NO_FRAME_DECORATOR);
		
		ProgramController mainControl = new ProgramController();

		frame.setSurface(mainControl);
		//frame.setIconImage(new ImageIcon(IOCommon.ICON_PATH).getImage());
		frame.run();
	}
	
}
