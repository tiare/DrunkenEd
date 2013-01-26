package tracking;

import java.nio.ByteBuffer;

import javax.vecmath.Point2d;

import jogamp.nativewindow.windows.BITMAPINFO;
import jogamp.opengl.util.av.impl.FFMPEGMediaPlayer.PixelFormat;

import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;

import control.ProgramController;

public class CameraTracking extends AbstractTracking {

	public UserTrackerMod app;
	private ByteBuffer userPicByteBuffer;
	private ByteBuffer picByteBuffer;
	public float headangle=0;

	public CameraTracking(ProgramController programController) {
		super(programController);
		userPicByteBuffer = ByteBuffer.allocateDirect(60 * 100 * 4);
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
		app.updateDepth();
	}

	public void step(float deltatime) {
		// p(deltatime);
		if (app != null)
			app.updateDepth();
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
				ImageGenerator img = ImageGenerator.create(app.context);
				picByteBuffer = img.getImageMap().createByteBuffer();
				if (getHeadPos().x != 0 && getHeadPos().y != 0) {
					userPicByteBuffer.rewind();
					for (int y = (int) (getHeadPos().y - 20); y < (int) (getHeadPos().y + 80); y++) {
						for (int x = (int) (getHeadPos().x - 40); x < ((int) getHeadPos().x + 20); x++) {
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
}
