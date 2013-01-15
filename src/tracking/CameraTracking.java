
package tracking;


import java.nio.ByteBuffer;

import javax.vecmath.Point2d;

import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;


import control.ProgramController;

public class CameraTracking extends AbstractTracking {

	public UserTrackerMod app;
	
	public CameraTracking(ProgramController programController) {
		super(programController);
	}

	@Override
	public Point2d getHeadPos() {
		p("getheadpos triggered");
		if (app!=null)		return app.headpos; 
		else return new Point2d();
	}

	@Override
	public void init() {
		
		 start();
	}

	public void start() {
		
        app = new UserTrackerMod(programController);
        app.updateDepth();
		
	}
	
	
	
	public void step(float deltatime){
		//p(deltatime);
		if (app!=null)		app.updateDepth();
	}
	
	@Override
	public float getTorsoBending() {
		if (app!=null && app!=null)
		return app.bendingangle; else {
//		p("gettorsobending triggered");
			//p("somethin is NULL");
		return 0;
		}
	}
	
	private static void p(Object p) {
		System.out.println(p.toString());
	}

	@Override
	public ByteBuffer getColorImage() {
		if (app!=null) {
		try {
			ImageGenerator img = ImageGenerator.create(app.context);
			return img.createDataByteBuffer();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return null;
	}
}
