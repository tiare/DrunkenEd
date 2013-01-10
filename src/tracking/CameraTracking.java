
package tracking;

import javax.vecmath.Point2d;

import control.ProgramController;

public class CameraTracking extends AbstractTracking {

	public CameraTracking(ProgramController programController) {
		super(programController);
	}

	@Override
	public Point2d getHeadPos() {
		return null;
	}

	@Override
	public void init() {
	
	}

	@Override
	public float getTorsoBending() {
		return 0;
	}

}
