package system;

import yang.pc.gles.YangGLESFrame;
import control.Config;
import control.Debug;
import control.ProgramController;

public class DrunkenMain {

	public static void main(String[] args) {

		Config.preInitialize();

		YangGLESFrame frame = new YangGLESFrame("Drunken Ed");
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
