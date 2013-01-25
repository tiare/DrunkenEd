package system;

import javax.swing.ImageIcon;

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
		frame.init(Debug.RESOLUTION_X,Debug.RESOLUTION_Y);
		
		ProgramController mainControl = new ProgramController();

		frame.setSurface(mainControl);
		frame.setIconImage(new ImageIcon(IOCommon.ICON_PATH).getImage());
		frame.run();
	}
	
}
