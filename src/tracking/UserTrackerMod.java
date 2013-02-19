package tracking;

//import java.nio.ShortBuffer;

import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.TreeSet;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.CalibrationProgressStatus;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
//import org.OpenNI.DepthMetaData;
//import org.OpenNI.ImageGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.OutArg;
import org.OpenNI.Point3D;
import org.OpenNI.PoseDetectionCapability;
import org.OpenNI.PoseDetectionEventArgs;
//import org.OpenNI.SceneMetaData;
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
import control.ProgramState;
//import control.states.MainMenuState;
//import control.Debug;
//import tracking.UserTrackerMod.CalibrationCompleteObserver;
//import tracking.UserTrackerMod.LostUserObserver;
//import tracking.UserTrackerMod.NewUserObserver;
//import tracking.UserTrackerMod.PoseDetectedObserver;
//import tracking.UserTrackerMod.ExitUserCallback;
 

public class UserTrackerMod {
	private static final float DRINKINGTIME = 0.35f;
	private TreeSet<Integer> regusers=new TreeSet<Integer>();
	
	class NewUserObserver implements IObserver<UserEventArgs>
	{
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			p("New user " + args.getId());
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
			p("Lost user " + args.getId());
			//joints.remove(args.getId());
			regusers.remove(args.getId());
			if (activeUser==args.getId()) {
				activeUser=-1;
				programController.tracking.trackedUser=false;
				p("RESTART TRIGGERED - in lostuserobserver");
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
			p("Calibration complete: " + args.getStatus());
			try
			{
			if (args.getStatus() == CalibrationProgressStatus.OK)
			{
				p("starting tracking "  +args.getUser());
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
			p("Pose " + args.getPose() + " detected for " + args.getUser());
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
	
	class ExitUserCallback implements  IObserver<UserEventArgs>
	{
		public void update(IObservable<UserEventArgs> observable,UserEventArgs args){
			regusers.remove(args.getId());
			if (activeUser==args.getId()) {
				activeUser=-1;
				programController.tracking.trackedUser=false;
				p("RESTART TRIGGERED - in ExitUserCallback");
				TrackingListener listener = programController.getCurrentState();
				if(listener!=null) {
					listener.userLost();
				}
			}
		}
	}
	
	public final String SAMPLE_XML_FILE = "SamplesConfig.xml";
	OutArg<ScriptNode> scriptNode;
	public Context context;
	public DepthGenerator depthGen;
//	private int width;
//	private int height;
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
	private boolean activeUserHasPixels=false;
	private boolean isJumping=false;
	private float oldz1=0;
	private float oldz2=0;
	private float z2;
	private float z1;
	public SkeletonJoint[] body= {
			SkeletonJoint.HEAD,
			SkeletonJoint.LEFT_SHOULDER,
			SkeletonJoint.LEFT_ELBOW,
			SkeletonJoint.LEFT_HAND,
			SkeletonJoint.RIGHT_SHOULDER,
			SkeletonJoint.RIGHT_ELBOW,
			SkeletonJoint.RIGHT_HAND,
			SkeletonJoint.NECK,
			SkeletonJoint.TORSO,
			SkeletonJoint.LEFT_HIP,
			SkeletonJoint.LEFT_KNEE,
			SkeletonJoint.LEFT_FOOT,
			SkeletonJoint.RIGHT_HIP,
			SkeletonJoint.RIGHT_KNEE,
			SkeletonJoint.RIGHT_FOOT};
	public Point3D[] skeletonpoints=new Point3D[body.length];
	private Point3D[] positions=new Point3D[body.length];
	private float jumpingspeed;
	
	private float drinkbuffery=0;
	private float drinkbufferx=0;
	private Vector3d vecLeftUpperArmAngle;
	private Vector3d vecRightUpperArmAngle;
	private Vector3d vecRightLowerArmAngle;
	private Vector3d vecLeftLowerArmAngle;
	private float winkel1;
	private Point3D rightelbow3D;
	private Point3D rightshoulder3D;
	private float bendingtemp;
	private float usertemp;
	private boolean breaker;
	private ProgramState listener;
	private Point3D headtorso;
	private Point3D headneck;
	private Point3D temppoint;

	
	
	
	//SceneMetaData sceneMD = new SceneMetaData();
	
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
            userGen.getUserExitEvent().addObserver(new ExitUserCallback());
            skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObserver());
            poseDetectionCap.getPoseDetectedEvent().addObserver(new PoseDetectedObserver());
            
            calibPose = skeletonCap.getSkeletonCalibrationPose();
            joints = new HashMap<Integer, HashMap<SkeletonJoint,SkeletonJointPosition>>();
            
            skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);
			
			context.startGeneratingAll();
			headpos=new Point2d(0,0);
			lefthandpos=new Point2d(0,0);
			righthandpos=new Point2d(0,0);
			vecLeftUpperArmAngle=new Vector3d();
			vecRightUpperArmAngle=new Vector3d();
			vecRightLowerArmAngle=new Vector3d();
			vecLeftLowerArmAngle=new Vector3d();
			winkel1=0;
			rightelbow3D=new Point3D();
			rightshoulder3D=new Point3D();
			bendingtemp=0;
			usertemp=0;
			headtorso=new Point3D();
			headneck=new Point3D();
			temppoint=new Point3D();
        } catch (GeneralException e) {
            e.printStackTrace();
            System.exit(1);
        }
	}
    
	public void setNextUser() {
		try {
			//p(1);
			users = userGen.getUsers();
			breaker=false;
			if (0<users.length && activeUser==-1){// p(2);
				for (int i=0;i<users.length;i++){ //p(3);
					//p(skeletonCap.isSkeletonTracking(users[i])+"||"+userGen.getUserPixels(users[i]).getDataSize());
					if (skeletonCap.isSkeletonTracking(users[i])){
					//	p("endlich drin");
						SceneMetaData sceneMD = userGen.getUserPixels(users[i]);
			            ShortBuffer scene = sceneMD.getData().createShortBuffer();
			            scene=sceneMD.getData().createShortBuffer();
			            scene.rewind();
			            for (int z =0;z<scene.capacity();z++){
			            	if (scene.get()==users[i]){
			            		activeUserHasPixels=true;
			            		breaker=true;
			            		break;
			            	}
			            		
			            }
						if (activeUserHasPixels){
							activeUser=users[i];
							programController.tracking.trackedUser=true;
							p("TrackedUser=true");
							p("activeuser:: "+activeUser );
						} else {
							
						}
						activeUserHasPixels=false;
						if (breaker)break;
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
            
            if (activeUser==-1) {
            	oldz1=oldz2=0;
            	programController.tracking.trackArms=false;
            	setNextUser();
            	programController.tracking.trackedUser=false;
            	}
            
            //check if activeuser is in fov
            if (activeUser!=-1){
            	SceneMetaData sceneMD = userGen.getUserPixels(activeUser);
            	ShortBuffer scene = sceneMD.getData().createShortBuffer();
            	scene=sceneMD.getData().createShortBuffer();
            	scene.rewind();
            	for (int i =0;i<scene.capacity();i++){
            		if (scene.get()==activeUser){
            			activeUserHasPixels=true;
            			break;
            		}	
            	}
            	if (!activeUserHasPixels) {
//            		listener = programController.getCurrentState();
//            		if(listener!=null) {
//            			listener.userLost();
//            		}
            		activeUser=-1;
            		programController.tracking.trackedUser=false;
				
            	}
            	activeUserHasPixels=false;        
            }
            
            
            if (activeUser!=-1 && skeletonCap.isSkeletonTracking(activeUser)){ 
            	checkTriggers();
            	nottrackedsince=0;
            	programController.tracking.trackedUser=true;
            	}
            else if (activeUser !=-1 && !skeletonCap.isSkeletonTracking(activeUser)) {
            	nottrackedsince+=deltatime;
            	}
            //p(nottrackedsince);
            if (nottrackedsince>1) {
				activeUser=-1;
				p("RESTART TRIGGERED - user not tracked over 1 sec");
				programController.tracking.trackedUser=false;
				//programController.switchState(new MainMenuState().init(programController));
				listener = programController.getCurrentState();
				if(listener!=null) {
					listener.userLost();
				}
			}
           // if (Debug.TRACKING_SYSTEM_OUT_PRINTLN)System.out.println(activeuser);
            
        } catch (GeneralException e) {
            e.printStackTrace();
        }
		
	}

	private void checkTriggers() {
		try {
			//hasDrinkingPose=false;
			//makesStep=false;
			users = userGen.getUsers();
			if (users.length>0 && skeletonCap.isSkeletonTracking(activeUser)){
				for (int i =0;i<body.length;i++){
					positions[i]=skeletonCap.getSkeletonJointPosition(activeUser, body[i]).getPosition();
					skeletonpoints[i]=depthGen.convertRealWorldToProjective(positions[i]);
					
				}
				drinkbufferx=Math.abs(positions[4].getX()-positions[0].getX());
				drinkbuffery=Math.abs(positions[0].getY()-positions[7].getY());
				
				
//				System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.HEAD).getPosition().getY()+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getY()+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_SHOULDER).getPosition().getY());
//				System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_KNEE).getPosition().getY()-skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HIP).getPosition().getY());
//				System.out.println(skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getX()
//					+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getY()
//					+"::"+skeletonCap.getSkeletonJointPosition(1, SkeletonJoint.RIGHT_HAND).getPosition().getZ());
				if (positions[0].getY()+drinkbuffery*3
					>=positions[6].getY()
				&&	positions[6].getY()
					>=positions[4].getY()
					&& positions[6].getX()
					<= positions[4].getX()+drinkbufferx
					&& positions[6].getX()
					>= positions[0].getX()
					){
					//if (Debug.TRACKING_SYSTEM_OUT_PRINTLN)System.out.println("TRINKbewegung erkannt");
					hasDrinkingPose=true;
					programController.tracking.drinking+=deltatime;
					//p(programController.tracking.drinking);
				}
			
			if (positions[0].getY()
					<positions[6].getY()
				||	positions[6].getY()
					<positions[4].getY()){
				//if (Debug.TRACKING_SYSTEM_OUT_PRINTLN)System.out.println("ANTI-TRINKbewegung erkannt");
				hasDrinkingPose=false;
				drinkAlreadyCalled=false;
				programController.tracking.drinking=0;
			}
			
			if (positions[13].getY()
					-positions[12].getY()>-330){
				//if (Debug.TRACKING_SYSTEM_OUT_PRINTLN)System.out.println("STEPbewegung erkannt");
				makesStep=true;
			}
			temppoint=skeletonpoints[0];
			//headpos=new Point2d(temp.getX(), temp.getY());
			headpos.set(temppoint.getX(), temppoint.getY());
			temppoint=skeletonpoints[3];
			lefthandpos.set(temppoint.getX(), temppoint.getY());
			temppoint=skeletonpoints[6];
			righthandpos.set(temppoint.getX(), temppoint.getY());
			
			
			// Jump Detection
			z1=skeletonpoints[9].getY()*skeletonpoints[9].getZ()/2250;
			z2=skeletonpoints[12].getY()*skeletonpoints[12].getZ()/2250;
			if (z1-oldz1<-3 && z2-oldz2<-3 && z1-oldz1>-8 && z2-oldz2>-8 && oldz1!=0 && oldz2!=0){
				p("JUMP detected");
				isJumping =true;
				jumpingspeed=Math.abs(z1-oldz1+z2-oldz2)/2;
			}
			oldz1=z1;
			oldz2=z2;
			//p(oldz1+"::"+z1+"||"+oldz2+"::"+z2);
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
			if (hasDrinkingPose && !drinkAlreadyCalled && programController.tracking.drinking>DRINKINGTIME){
				listener.onDrink();
				drinkAlreadyCalled=true;
				
				} else if (isJumping){
					listener.onJump(jumpingspeed);
					isJumping=false;
				}
			listener.onBend(bendingangle);
		}
	}

	private void calculateArmsAngle() {
		
		Point3D lefthand = (positions[3]);
		Point3D leftelbow = (positions[2]);
		Point3D leftshoulder = (positions[1]);
		Point3D righthand = (positions[6]);
		Point3D rightelbow = (positions[5]);
		Point3D rightshoulder = (positions[4]);
		
		vecLeftUpperArmAngle.set(leftelbow.getX()-leftshoulder.getX(),leftelbow.getY()-leftshoulder.getY(),leftelbow.getZ()-leftshoulder.getZ());
		
		vecRightUpperArmAngle.set(rightelbow.getX()-rightshoulder.getX(),rightelbow.getY()-rightshoulder.getY(),rightelbow.getZ()-rightshoulder.getZ());
		
		vecLeftLowerArmAngle.set(lefthand.getX()-leftelbow.getX(),lefthand.getY()-leftelbow.getY(),lefthand.getZ()-leftelbow.getZ());
		
		vecRightLowerArmAngle.set(righthand.getX()-rightelbow.getX(),righthand.getY()-rightelbow.getY(),righthand.getZ()-rightelbow.getZ());
		programController.tracking.leftUpperArmAngle=(float) (Math.PI-Math.atan2(vecLeftUpperArmAngle.x,vecLeftUpperArmAngle.y));//-bendingangle;
		programController.tracking.rightUpperArmAngle=(float) (-Math.PI-Math.atan2(vecRightUpperArmAngle.x,vecRightUpperArmAngle.y));//-bendingangle;
		programController.tracking.leftLowerArmAngle=(float) (Math.PI-Math.atan2(vecLeftLowerArmAngle.x,vecLeftLowerArmAngle.y));//-programController.tracking.rightUpperArmAngle;
		programController.tracking.rightLowerArmAngle=(float) (-Math.PI-Math.atan2(vecRightLowerArmAngle.x,vecRightLowerArmAngle.y));//-programController.tracking.leftUpperArmAngle;
		programController.tracking.trackArms=true;
//	p("leftupper: "+(programController.tracking.leftUpperArmAngle*180/Math.PI)+"||leftlower: "+(programController.tracking.leftLowerArmAngle*180/Math.PI)
//			+"||rightupper: "+(programController.tracking.rightUpperArmAngle*180/Math.PI)+"||rightlower: "+(programController.tracking.rightLowerArmAngle*180/Math.PI)+"||"+(bendingangle*180/Math.PI));
		
	}

	private void calculateShoulderAngle(){
		rightelbow3D = positions[5];
		rightshoulder3D = positions[4];
		//Point3D head3D = skeletonCap.getSkeletonJointPosition(1,SkeletonJoint.HEAD).getPosition();
		//Point3D torso3D = skeletonCap.getSkeletonJointPosition(1,SkeletonJoint.TORSO).getPosition();
		Vector3d v1= new Vector3d(rightelbow3D.getX()-rightshoulder3D.getX(),rightelbow3D.getY()-rightshoulder3D.getY(),rightelbow3D.getZ()-rightshoulder3D.getZ());
		//Vector3d v2=new Vector3d(torso3D.getX()-head3D.getX(),torso3D.getY()-head3D.getY(),torso3D.getZ()-head3D.getZ());
		winkel1=(float) Math.atan2(v1.x,v1.y);
		//float winkel2=(float) Math.atan2(v2.x,v2.y);
		//schulterwinkel=winkel2-winkel1;
		schulterwinkel=bendingangle-winkel1;
		
		
	}

	private void calculateBendingAngle() {
		Point3D head3d = positions[0];
		Point3D torso3d = positions[8];
		Point3D neck3d=positions[7];
		headtorso.setPoint(neck3d.getX()-torso3d.getX(),neck3d.getY()-torso3d.getY(),neck3d.getZ()-torso3d.getZ());
		headneck.setPoint(head3d.getX()-neck3d.getX(),head3d.getY()-neck3d.getY(),head3d.getZ()-neck3d.getZ());
		//System.out.println(temp.getX()+"::"+temp.getY());
		bendingtemp=(float) Math.atan2(headtorso.getX(),headtorso.getY());
		headangle =(float) Math.atan2(headneck.getX(),headneck.getY());
		programController.tracking.headangle=headangle;
//			Point2d head2d=new Point2d(head3d.getX(),head3d.getY());
//			Point2d torso2d=new Point2d(head3d.getX(),head3d.getY());
//			Point2d temp2d=new Point2d();
		bendingangle=bendingtemp/BENDINGANGLEFACTOR;
		//System.out.println(test*180/Math.PI+"::"+bendingangle);
		//boolean wrongz =false;
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
					usertemp=Math.abs(positions[8].getX());
					//p(temp+"::user"+i);
					if (usertemp<nearestx){
						mostmiddleuser=i;
						//p("activeuser set to:"+i+" :: "+temp);
						nearestx=usertemp;
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
