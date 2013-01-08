package system;

import javax.swing.ImageIcon;

import pc.fileio.IOCommon;
import pc.gles.GLESFrame;
import app.App;
import control.ProgramController;

public class DrunkenMain {

	public static void main(String[] args) {
		
		IOCommon.IMAGE_PATH = "textures/";
		
		GLESFrame frame = new GLESFrame("A Touch of Ninja");
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
