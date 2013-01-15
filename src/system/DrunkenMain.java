package system;

import javax.swing.ImageIcon;

import pc.fileio.IOCommon;
import pc.gles.GLESFrame;
import app.App;
import control.Config;
import control.ProgramController;

public class DrunkenMain {

	public static void main(String[] args) {
		
		IOCommon.IMAGE_PATH = "textures/";
		
		Config.preInitialize();
		
		GLESFrame frame = new GLESFrame("Drunken Ed");
		frame.init();
		
		App.gfxLoader = frame.mGraphics.mGFXLoader;
		App.exit = frame;
		
		ProgramController mainControl = new ProgramController();

		frame.setSurface(mainControl);
		frame.setEventListener(mainControl);
		frame.setIconImage(new ImageIcon(IOCommon.ICON_PATH).getImage());
		frame.run();
	}
	
}
