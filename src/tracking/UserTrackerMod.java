package tracking;

import java.nio.ShortBuffer;

import java.util.HashMap;

import javax.vecmath.Point2d;
import javax.vecmath.Vector3d;

import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.CalibrationProgressStatus;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMetaData;
import org.OpenNI.ImageGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.OutArg;
import org.OpenNI.Point3D;
import org.OpenNI.PoseDetectionCapability;
import org.OpenNI.PoseDetectionEventArgs;
import org.OpenNI.SceneMetaData;
import org.OpenNI.ScriptNode;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointPosition;
import org.OpenNI.SkeletonProfile;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;
import org.OpenNI.UserGenerator;

import control.ProgramController;

import tracking.UserTrackerMod.CalibrationCompleteObserver;
import tracking.UserTrackerMod.LostUserObserver;
import tracking.UserTrackerMod.NewUserObserver;
import tracking.UserTrackerMod.PoseDetectedObserver;


public class UserTrackerMod {
	
	class NewUserObserver implements IObserver<UserEventArgs>
	{
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("New user " + args.getId());
			try
			{
				if (skeletonCap.needPoseForCalibration())
				{
					poseDetectionCap.startPoseDetection(calibPose, args.getId());
				}
				else
				{
					skeletonCap.requestSkeletonCalibration(args.getId(), true);
				}
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class LostUserObserver implements IObserver<UserEventArgs>
	{
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("Lost user " + args.getId());
			joints.remove(args.getId());
		}
	}	
	class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs>
	{
		@Override
		public void update(IObservable<CalibrationProgressEventArgs> observable,
				CalibrationProgressEventArgs args)
		{
			System.out.println("Calibration complete: " + args.getStatus());
			try
			{
			if (args.getStatus() == CalibrationProgressStatus.OK)
			{
				System.out.println("starting tracking "  +args.getUser());
					skeletonCap.startTracking(args.getUser());
	                joints.put(new Integer(args.getUser()), new HashMap<SkeletonJoint, SkeletonJointPosition>());
			}
			else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT)
			{
				if (skeletonCap.needPoseForCalibration())
				{
					poseDetectionCap.startPoseDetection(calibPose, args.getUser());
				}
				else
				{
					skeletonCap.requestSkeletonCalibration(args.getUser(), true);
				}
			}
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs>
	{
		@Override
		public void update(IObservable<PoseDetectionEventArgs> observable,
				PoseDetectionEventArgs args)
		{
			System.out.println("Pose " + args.getPose() + " detected for " + args.getUser());
			try
			{
				poseDetectionCap.stopPoseDetection(args.getUser());
				skeletonCap.requestSkeletonCalibration(args.getUser(), true);
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public final String SAMPLE_XML_FILE = "SamplesConfig.xml";
	OutArg<ScriptNode> scriptNode;
	public Context context;
	public DepthGenerator depthGen;
	private int width;
	private int height;
	private UserGenerator userGen;
	private SkeletonCapability skeletonCap;
	private PoseDetectionCapability poseDetectionCap;
	String calibPose = null;
    public HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints;
	private ProgramController programController;
	public float bendingangle=0;
	public boolean hasDrinkingPose=false;
	public boolean makesStep=false;
	public Point2d headpos;
	public Point2d righthandpos;
	public Point2d lefthandpos;
	private float BENDINGANGLEFACTOR=1;
	public float schulterwinkel=0;
	public int activeuser=0;
	
	public UserTrackerMod(ProgramController programController){
		try {
            scriptNode = new OutArg<ScriptNode>();
            context = Context.createFromXmlFile(SAMPLE_XML_FILE, scriptNode);
            this.programController=programController;

            depthGen = DepthGenerator.create(context);
            
           
           // DepthMetaData depthMD = depthGen.getMetaData();

            //histogram = new float[10000];
          //  width = depthMD.getFullXRes();
          //  height = depthMD.getFullYRes();
            
            //imgbytes = new byte[width*height*3];

            userGen = UserGenerator.create(context);
            skeletonCap = userGen.getSkeletonCapability();
            poseDetectionCap = userGen.getPoseDetectionCapability();
            
            userGen.getNewUserEvent().addObserver(new NewUserObserver());
            userGen.getLostUserEvent().addObserver(new LostUserObserver());
            skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObserver());
            poseDetectionCap.getPoseDetectedEvent().addObserver(new PoseDetectedObserver());
            
            calibPose = skeletonCap.getSkeletonCalibrationPose();
            joints = new HashMap<Integer, HashMap<SkeletonJoint,SkeletonJointPosition>>();
            
            skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);
			
			context.startGeneratingAll();
			headpos=new Point2d(0,0);
			lefthandpos=new Point2d(0,0);
			righthandpos=new Point2d(0,0);
        } catch (GeneralException e) {
            e.printStackTrace();
            System.exit(1);
        }
	}

	public void getJoint(int user, SkeletonJoint joint) throws StatusException
    {
        SkeletonJointPosition pos = skeletonCap.getSkeletonJointPosition(user, joint);
        //System.out.println(user+" ");
		if (pos.getPosition().getZ() != 0)
		{
			joints.get(user).put(joint, new SkeletonJointPosition(depthGen.convertRealWorldToProjective(pos.getPosition()), pos.getConfidence()));
		}
		else
		{
			joints.get(user).put(joint, new SkeletonJointPosition(new Point3D(), 0));
		}
		System.out.print(joints.get(user).get(SkeletonJoint.LEFT_ELBOW).getPosition().toString());
		System.out.println(" :: "+joints.get(user).get(SkeletonJoint.LEFT_SHOULDER).getPosition().toString());
    }
    public void getJoints(int user) throws StatusException
    {	
    	System.out.println(SkeletonJoint.HEAD.toString());
    	getJoint(user, SkeletonJoint.HEAD);
    	getJoint(user, SkeletonJoint.NECK);
    	
    	getJoint(user, SkeletonJoint.LEFT_SHOULDER);
    	getJoint(user, SkeletonJoint.LEFT_ELBOW);
    	getJoint(user, SkeletonJoint.LEFT_HAND);

    	getJoint(user, SkeletonJoint.RIGHT_SHOULDER);
    	getJoint(user, SkeletonJoint.RIGHT_ELBOW);
    	getJoint(user, SkeletonJoint.RIGHT_HAND);

    	getJoint(user, SkeletonJoint.TORSO);

    	getJoint(user, SkeletonJoint.LEFT_HIP);
        getJoint(user, SkeletonJoint.LEFT_KNEE);
        getJoint(user, SkeletonJoint.LEFT_FOOT);

    	getJoint(user, SkeletonJoint.RIGHT_HIP);
        getJoint(user, SkeletonJoint.RIGHT_KNEE);
        getJoint(user, SkeletonJoint.RIGHT_FOOT);

    }
	
	public void updateDepth() {
		try {
        	
            //context.waitAnyUpdateAll();
            context.waitNoneUpdateAll();
            DepthMetaData depthMD = depthGen.getMetaData();
           // SceneMetaData sceneMD = userGen.getUserPixels(0);
            
           // ShortBuffer scene = sceneMD.getData().createShortBuffer();
            ShortBuffer depth = depthMD.getData().createShortBuffer();
           // calcHist(depth);
           // depth.rewind();
            int[] users = userGen.getUsers();
			for (int i = 0; i < users.length; ++i)
			{
				if (skeletonCap.isSkeletonTracking(users[i])){				
					//getJoints(users[i]);
					getActiveUser();
					checkTriggers();
					}
			}
            
            
            
        } catch (GeneralException e) {
            e.printStackTrace();
        }
		
	}

	private void checkTriggers() {
		
		
		try {
			hasDrinkingPose=false;
			makesStep=false;
			if (userGen.getUsers().length>0 && skeletonCap.isSkeletonTracking(userGen.getUsers()[0]))
//			System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.HEAD).getPosition().getY()+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getY()+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_SHOULDER).getPosition().getY());
//			System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_KNEE).getPosition().getY()-skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HIP).getPosition().getY());
//			System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getX()
//					+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getY()
//					+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getZ());
			if (skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.HEAD).getPosition().getY()
					>=skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.RIGHT_HAND).getPosition().getY()
				&&	skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.RIGHT_HAND).getPosition().getY()
					>=skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.RIGHT_SHOULDER).getPosition().getY()){
				System.out.println("TRINKbewegung erkannt");
				hasDrinkingPose=true;
			}
			if (skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.RIGHT_KNEE).getPosition().getY()
					-skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.RIGHT_HIP).getPosition().getY()>-330){
				//System.out.println("STEPbewegung erkannt");
				makesStep=true;
			}
			Point3D temp=depthGen.convertRealWorldToProjective(skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.HEAD).getPosition());
			headpos=new Point2d(temp.getX(), temp.getY());
			temp=depthGen.convertRealWorldToProjective(skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.LEFT_HAND).getPosition());
			lefthandpos=new Point2d(temp.getX(), temp.getY());
			temp=depthGen.convertRealWorldToProjective(skeletonCap.getSkeletonJointPosition(activeuser, SkeletonJoint.RIGHT_HAND).getPosition());
			righthandpos=new Point2d(temp.getX(), temp.getY());
			calculateBendingAngle();
			calculateShoulderAngle();
			
		} catch (StatusException e) {
			e.printStackTrace();
		}
		TrackingListener listener = programController.getCurrentState();
		if(listener!=null) {
			if (hasDrinkingPose){
				listener.onDrink();
				
				}
			listener.onBend(bendingangle);
		}
	}

	private void calculateShoulderAngle(){
		try {
			Point3D rightelbow3D = skeletonCap.getSkeletonJointPosition(activeuser,SkeletonJoint.RIGHT_ELBOW).getPosition();
			Point3D rightshoulder3D = skeletonCap.getSkeletonJointPosition(activeuser,SkeletonJoint.RIGHT_SHOULDER).getPosition();
			//Point3D head3D = skeletonCap.getSkeletonJointPosition(1,SkeletonJoint.HEAD).getPosition();
			//Point3D torso3D = skeletonCap.getSkeletonJointPosition(1,SkeletonJoint.TORSO).getPosition();
			Vector3d v1= new Vector3d(rightelbow3D.getX()-rightshoulder3D.getX(),rightelbow3D.getY()-rightshoulder3D.getY(),rightelbow3D.getZ()-rightshoulder3D.getZ());
			//Vector3d v2=new Vector3d(torso3D.getX()-head3D.getX(),torso3D.getY()-head3D.getY(),torso3D.getZ()-head3D.getZ());
			float winkel1=(float) Math.atan2(v1.x,v1.y);
			//float winkel2=(float) Math.atan2(v2.x,v2.y);
			//schulterwinkel=winkel2-winkel1;
			schulterwinkel=bendingangle-winkel1;
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void calculateBendingAngle() {
		try {
			Point3D head3d = skeletonCap.getSkeletonJointPosition(activeuser,SkeletonJoint.HEAD).getPosition();
			Point3D torso3d = skeletonCap.getSkeletonJointPosition(activeuser,SkeletonJoint.TORSO).getPosition();
			Point3D temp=new Point3D(head3d.getX()-torso3d.getX(),head3d.getY()-torso3d.getY(),head3d.getZ()-torso3d.getZ());
			//System.out.println(temp.getX()+"::"+temp.getY());
			float test=(float) Math.atan2(temp.getX(),temp.getY());
//			Point2d head2d=new Point2d(head3d.getX(),head3d.getY());
//			Point2d torso2d=new Point2d(head3d.getX(),head3d.getY());
//			Point2d temp2d=new Point2d();
			bendingangle=test/BENDINGANGLEFACTOR;
			//System.out.println(test*180/Math.PI+"::"+bendingangle);
			
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private int getActiveUser() {
		// TODO Auto-generated method stub
		return 0;
	}
}
