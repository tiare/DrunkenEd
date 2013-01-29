package tracking;

import java.nio.ShortBuffer;

import java.util.HashMap;
import java.util.TreeSet;

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

import control.Debug;
import control.ProgramController;
import control.states.MainMenuState;

import tracking.UserTrackerMod.CalibrationCompleteObserver;
import tracking.UserTrackerMod.LostUserObserver;
import tracking.UserTrackerMod.NewUserObserver;
import tracking.UserTrackerMod.PoseDetectedObserver;
 

public class UserTrackerMod {
	private TreeSet<Integer> regusers=new TreeSet<Integer>();
	class NewUserObserver implements IObserver<UserEventArgs>
	{
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("New user " + args.getId());
			regusers.add(args.getId());
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
			//joints.remove(args.getId());
			regusers.remove(args.getId());
			if (activeUser==args.getId()) {
				activeUser=-1;
				p("RESTART TRIGGERED");
				// RESTART???
				//programController.switchState(new MainMenuState().init(programController));
				TrackingListener listener = programController.getCurrentState();
				if(listener!=null) {
					listener.userLost();
				}
			}
			//setNextUser();
			
			
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
	                
	                if (activeUser==-1){
	                	activeUser=args.getUser();
	                	nottrackedsince=0;
	                	programController.tracking.trackedUser=true;
	                	}
	               
	                //p("args.getUser()::"+args.getUser());
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
	public int activeUser=-1;
	public float nottrackedsince=0;
	public int users[];
	private boolean drinkAlreadyCalled=false;
	public float headangle=0;
	private float deltatime=0;
	public SkeletonJoint[] body= {SkeletonJoint.HEAD,SkeletonJoint.LEFT_SHOULDER,SkeletonJoint.LEFT_ELBOW,SkeletonJoint.LEFT_HAND,
			SkeletonJoint.RIGHT_SHOULDER,SkeletonJoint.RIGHT_ELBOW,SkeletonJoint.RIGHT_HAND,SkeletonJoint.NECK,SkeletonJoint.TORSO,
			SkeletonJoint.LEFT_HIP,SkeletonJoint.LEFT_KNEE,SkeletonJoint.LEFT_FOOT,SkeletonJoint.RIGHT_HIP,SkeletonJoint.RIGHT_KNEE,SkeletonJoint.RIGHT_FOOT};
	public Point3D[] skeletonpoints=new Point3D[body.length];
	
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
    
	public void setNextUser() {
		try {
			//p(1);
			users = userGen.getUsers();
			if (0<users.length && activeUser==-1){// p(2);
				for (int i=0;i<users.length;i++){ //p(3);
					if (skeletonCap.isSkeletonTracking(users[i])){
					//	p("endlich drin");
						activeUser=users[i];
						programController.tracking.trackedUser=true;
						//activeUser=i;
					//	p("we are tracking: ");
						p("activeuser:: "+activeUser );
						break;
						}
					}
				}
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public void updateDepth(float deltatime) {
		try {
        	this.deltatime=deltatime;
            //context.waitAnyUpdateAll();
            context.waitNoneUpdateAll();
            DepthMetaData depthMD = depthGen.getMetaData();
           // SceneMetaData sceneMD = userGen.getUserPixels(0);
            
           // ShortBuffer scene = sceneMD.getData().createShortBuffer();
            ShortBuffer depth = depthMD.getData().createShortBuffer();
           // calcHist(depth);
           // depth.rewind();
           // users = userGen.getUsers();
           //float nearestx=1000;
//			for (int i = 0; i < users.length; ++i)
//			{
//				if (skeletonCap.isSkeletonTracking(users[i])){				
//					//getJoints(users[i]);
//					
//					
//					}
//			}
            if (nottrackedsince>1) {
				activeUser=-1;
				p("RESTART TRIGGERED");
				// RESTART???
				//programController.switchState(new MainMenuState().init(programController));
				TrackingListener listener = programController.getCurrentState();
				if(listener!=null) {
					listener.userLost();
				}
			}
            
            
            if (activeUser==-1) {
            	programController.tracking.trackArms=false;
            	setNextUser();
            	
            	}
            //p("users checked for skeleton tracking, if 1 then checkingtriggers");
			//setActiveUser();
			//if (activeuser!=0 )
            if (activeUser!=-1 && skeletonCap.isSkeletonTracking(activeUser)){ 
            	checkTriggers();
            	nottrackedsince=0;
            	}
            else if (activeUser !=-1 && !skeletonCap.isSkeletonTracking(activeUser)) {
            	nottrackedsince+=deltatime;
            	}
           // if (Debug.TRACKING_SYSTEM_OUT_PRINTLN)System.out.println(activeuser);
            if (nottrackedsince>0){
            	programController.tracking.trackedUser=false;
            }
            
        } catch (GeneralException e) {
            e.printStackTrace();
        }
		
	}

	private void checkTriggers() {
		try {
			//hasDrinkingPose=false;
			//makesStep=false;
			users = userGen.getUsers();
			float drinkbuffer=Math.abs(skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_SHOULDER).getPosition().getX()-skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.LEFT_SHOULDER).getPosition().getX());
			if (users.length>0 && skeletonCap.isSkeletonTracking(activeUser)){
//			System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.HEAD).getPosition().getY()+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getY()+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_SHOULDER).getPosition().getY());
//			System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_KNEE).getPosition().getY()-skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HIP).getPosition().getY());
//			System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getX()
//					+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getY()
//					+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getZ());
			if (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.HEAD).getPosition().getY()
					>=skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HAND).getPosition().getY()
				&&	skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HAND).getPosition().getY()
					>=skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_SHOULDER).getPosition().getY()
					&& skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HAND).getPosition().getX()
					<= skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_SHOULDER).getPosition().getX()+drinkbuffer
					&& skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HAND).getPosition().getX()
					>= skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.HEAD).getPosition().getX()
					){
				//if (Debug.TRACKING_SYSTEM_OUT_PRINTLN)System.out.println("TRINKbewegung erkannt");
				hasDrinkingPose=true;
				programController.tracking.drinking+=deltatime;
				p(programController.tracking.drinking);
			}
			
			if (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.HEAD).getPosition().getY()
					<skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HAND).getPosition().getY()
				||	skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HAND).getPosition().getY()
					<skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_SHOULDER).getPosition().getY()){
				//if (Debug.TRACKING_SYSTEM_OUT_PRINTLN)System.out.println("ANTI-TRINKbewegung erkannt");
				hasDrinkingPose=false;
				drinkAlreadyCalled=false;
				programController.tracking.drinking=0;
			}
			
			if (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_KNEE).getPosition().getY()
					-skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HIP).getPosition().getY()>-330){
				//if (Debug.TRACKING_SYSTEM_OUT_PRINTLN)System.out.println("STEPbewegung erkannt");
				makesStep=true;
			}
			Point3D temp=depthGen.convertRealWorldToProjective(skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.HEAD).getPosition());
			headpos=new Point2d(temp.getX(), temp.getY());
			temp=depthGen.convertRealWorldToProjective(skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.LEFT_HAND).getPosition());
			lefthandpos=new Point2d(temp.getX(), temp.getY());
			temp=depthGen.convertRealWorldToProjective(skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HAND).getPosition());
			righthandpos=new Point2d(temp.getX(), temp.getY());
			
			for (int i =0;i<body.length;i++){
				skeletonpoints[i]=depthGen.convertRealWorldToProjective(skeletonCap.getSkeletonJointPosition(activeUser, body[i]).getPosition());
			}
			
			
			calculateBendingAngle();
			//calculateShoulderAngle();
			calculateArmsAngle();
			} else {
				programController.tracking.trackArms=false;
			}
		} catch (StatusException e) {
			e.printStackTrace();
		}
		TrackingListener listener = programController.getCurrentState();
		if(listener!=null) {
			if (hasDrinkingPose && !drinkAlreadyCalled && programController.tracking.drinking>0.5){
				listener.onDrink();
				drinkAlreadyCalled=true;
				
				}
			listener.onBend(bendingangle);
		}
	}

	private void calculateArmsAngle() {
		
		//OR depthGen.convertRealWorldToProjective()
		try {
			Point3D lefthand = (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.LEFT_HAND).getPosition());
			Point3D leftelbow = (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.LEFT_ELBOW).getPosition());
			Point3D leftshoulder = (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.LEFT_SHOULDER).getPosition());
			Point3D righthand = (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_HAND).getPosition());
			Point3D rightelbow = (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_ELBOW).getPosition());
			Point3D rightshoulder = (skeletonCap.getSkeletonJointPosition(activeUser, SkeletonJoint.RIGHT_SHOULDER).getPosition());
			Vector3d vecLeftUpperArmAngle=new Vector3d(leftelbow.getX()-leftshoulder.getX(),leftelbow.getY()-leftshoulder.getY(),leftelbow.getZ()-leftshoulder.getZ());
			Vector3d vecRightUpperArmAngle=new Vector3d(rightelbow.getX()-rightshoulder.getX(),rightelbow.getY()-rightshoulder.getY(),rightelbow.getZ()-rightshoulder.getZ());
			Vector3d vecLeftLowerArmAngle=new Vector3d(lefthand.getX()-leftelbow.getX(),lefthand.getY()-leftelbow.getY(),lefthand.getZ()-leftelbow.getZ());
			Vector3d vecRightLowerArmAngle=new Vector3d(righthand.getX()-rightelbow.getX(),righthand.getY()-rightelbow.getY(),righthand.getZ()-rightelbow.getZ());
			programController.tracking.leftUpperArmAngle=(float) (Math.PI-Math.atan2(vecLeftUpperArmAngle.x,vecLeftUpperArmAngle.y));//-bendingangle;
			programController.tracking.rightUpperArmAngle=(float) (-Math.PI-Math.atan2(vecRightUpperArmAngle.x,vecRightUpperArmAngle.y));//-bendingangle;
			programController.tracking.leftLowerArmAngle=(float) (Math.PI-Math.atan2(vecLeftLowerArmAngle.x,vecLeftLowerArmAngle.y));//-programController.tracking.rightUpperArmAngle;
			programController.tracking.rightLowerArmAngle=(float) (-Math.PI-Math.atan2(vecRightLowerArmAngle.x,vecRightLowerArmAngle.y));//-programController.tracking.leftUpperArmAngle;
			programController.tracking.trackArms=true;
		//	p("leftupper: "+(programController.tracking.leftUpperArmAngle*180/Math.PI)+"||leftlower: "+(programController.tracking.leftLowerArmAngle*180/Math.PI)
		//			+"||rightupper: "+(programController.tracking.rightUpperArmAngle*180/Math.PI)+"||rightlower: "+(programController.tracking.rightLowerArmAngle*180/Math.PI)+"||"+(bendingangle*180/Math.PI));
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void calculateShoulderAngle(){
		try {
			Point3D rightelbow3D = skeletonCap.getSkeletonJointPosition(activeUser,SkeletonJoint.RIGHT_ELBOW).getPosition();
			Point3D rightshoulder3D = skeletonCap.getSkeletonJointPosition(activeUser,SkeletonJoint.RIGHT_SHOULDER).getPosition();
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
			Point3D head3d = skeletonCap.getSkeletonJointPosition(activeUser,SkeletonJoint.HEAD).getPosition();
			Point3D torso3d = skeletonCap.getSkeletonJointPosition(activeUser,SkeletonJoint.TORSO).getPosition();
			Point3D neck3d=skeletonCap.getSkeletonJointPosition(activeUser,SkeletonJoint.NECK).getPosition();
			Point3D headtorso=new Point3D(neck3d.getX()-torso3d.getX(),neck3d.getY()-torso3d.getY(),neck3d.getZ()-torso3d.getZ());
			Point3D headneck=new Point3D(head3d.getX()-neck3d.getX(),head3d.getY()-neck3d.getY(),head3d.getZ()-neck3d.getZ());
			//System.out.println(temp.getX()+"::"+temp.getY());
			float test=(float) Math.atan2(headtorso.getX(),headtorso.getY());
			headangle =(float) Math.atan2(headneck.getX(),headneck.getY());
			programController.tracking.headangle=headangle;
//			Point2d head2d=new Point2d(head3d.getX(),head3d.getY());
//			Point2d torso2d=new Point2d(head3d.getX(),head3d.getY());
//			Point2d temp2d=new Point2d();
			bendingangle=test/BENDINGANGLEFACTOR;
			//System.out.println(test*180/Math.PI+"::"+bendingangle);
			boolean wrongz =false;
			programController.tracking.gpareaz=(torso3d.getZ()-2250)/750;
			programController.tracking.gpareax=(4*torso3d.getX()/torso3d.getZ());
			//p("kopfwinkel::"+programController.tracking.headangle*180/Math.PI+"|| bendingangle: "+bendingangle*180/Math.PI);
//			if (programController.tracking.gpareaz<=-1){p("TOO CLOSE, step " +Math.floor((2000-torso3d.getZ()))/1000 +"m BACK");wrongz = true;}
//        	if (programController.tracking.gpareaz>=1){p("TOO FAR, step " +Math.floor((torso3d.getZ()-3000))/1000+"m IN FRONT");wrongz=true;}
//        	if (programController.tracking.gpareax>=1){p("STEP LEFT,more TO THE MIDDLE");}
//        	if (programController.tracking.gpareax<=-1){p("STEP RIGHT, more TO THE MIDDLE");}
//			if (torso3d.getZ()<1500){System.out.println("TOO CLOSE, step " +Math.floor((2000-torso3d.getZ()))/1000 +"m BACK");wrongz = true;}
//        	if (torso3d.getZ()>3000){System.out.println("TOO FAR, step " +Math.floor((torso3d.getZ()-3000))/1000+"m IN FRONT");wrongz=true;}
//        	if (!wrongz && torso3d.getX()/torso3d.getZ()>0.25){System.out.println("STEP LEFT,more TO THE MIDDLE");}
//        	if (!wrongz && torso3d.getX()/torso3d.getZ()<-0.25){System.out.println("STEP RIGHT, more TO THE MIDDLE");}
			
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private int setActiveUser() {
		int[] users;
		int mostmiddleuser=-1;
		float nearestx=1000000;
		try {
			users = userGen.getUsers();
			p("useranzahl:"+users.length);
			for (int i = 0; i < users.length; ++i)
			{
				if (skeletonCap.isSkeletonTracking(users[i])){
					float temp=Math.abs(skeletonCap.getSkeletonJointPosition(users[i], SkeletonJoint.TORSO).getPosition().getX());
					//p(temp+"::user"+i);
					if (temp<nearestx){
						mostmiddleuser=i;
						//p("activeuser set to:"+i+" :: "+temp);
						nearestx=temp;
					}
					
					}
			}
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mostmiddleuser!=-1){
			activeUser= mostmiddleuser;
			p("activeuser set to:"+mostmiddleuser);
			return mostmiddleuser;
		} else {
			activeUser= 0;
			p("activeuser set to:0");
			return 0;
		}
		
	}
	
	private static void p(Object p) {
		if (control.Debug.TRACKING_SYSTEM_OUT_PRINTLN) System.out.println(p.toString());
	}
}
