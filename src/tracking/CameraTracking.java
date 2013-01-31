package tracking;

import java.nio.ByteBuffer;

import javax.vecmath.Point2d;

import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;
import org.OpenNI.Point3D;

import control.ProgramController;

public class CameraTracking extends AbstractTracking {

	public UserTrackerMod app;
	private ByteBuffer userPicByteBuffer;
	private ByteBuffer picByteBuffer;
	public float headangle=0;
	public float gpareaz=0;
	public float gpareax=0;
	public float drinking=0;
	public ImageGenerator img;

	public CameraTracking(ProgramController programController) {
		super(programController);
		userPicByteBuffer = ByteBuffer.allocateDirect(80 * 125 * 4);
		picByteBuffer = ByteBuffer.allocateDirect(640 * 480 * 4);
	}

	@Override
	public Point2d getHeadPos() {
		// p("getheadpos triggered");
		if (app != null) {
			return app.headpos;
		} else {
			return new Point2d();
		}
	}

	@Override
	public void init() {
		start();
	}

	public void start() {
		app = new UserTrackerMod(programController);
		app.updateDepth(0);
		try {
			img = ImageGenerator.create(app.context);
		} catch (GeneralException e) {
			e.printStackTrace();
			img = null;
		}
	}

	public void step(float deltatime) {
		// p(deltatime);
		if (app != null)
			app.updateDepth(deltatime);
	}

	@Override
	public float getTorsoBending() {
		if (app != null && app != null)
			return app.bendingangle;
		else {
			// p("gettorsobending triggered");
			// p("somethin is NULL");
			return 0;
		}
	}

	private static void p(Object p) {
		System.out.println(p.toString());
	}

	public void restart() {
		// app.context.release();
		// start();
		// app.activeUser=-1;
	}

	@Override
	public ByteBuffer getColorImageByteBuffer() {
		if (app == null) {
			p("app was null!");
		}
		if (app != null) {
			try {
				//picByteBuffer = img.getImageMap().createByteBuffer();
				if(img==null)
					return null;
				img.getImageMap().copyToBuffer(picByteBuffer, picByteBuffer.capacity());
				if (getHeadPos().x != 0 && getHeadPos().y != 0) {
					userPicByteBuffer.rewind();
					for (int y = (int) (getHeadPos().y - 25); y < (int) (getHeadPos().y + 100); y++) {
						for (int x = (int) (getHeadPos().x - 40); x < ((int) getHeadPos().x + 40); x++) {
							int index = y * 640 * 3 + x * 3;
							try {
								userPicByteBuffer.put(picByteBuffer.get(index));
								userPicByteBuffer.put(picByteBuffer.get(index + 1));
								userPicByteBuffer.put(picByteBuffer.get(index + 2));
								userPicByteBuffer.put((byte) 255);
							} catch (Exception o) {
								return null;
							}
						}
					}
					userPicByteBuffer.rewind();
				}
				return userPicByteBuffer;
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Point3D[] getSkeletonPoints() {
		// TODO Auto-generated method stub
		return app.skeletonpoints;
	}
}
