package oog.mini;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import robocode.*;
import robocode.util.*;
//MagestyOfColors: Why Just have one?
public class MagestyOfColors extends AdvancedRobot {
	final static double BULLET_POWER=2;
	final static double BULLET_SPEED=20-3*BULLET_POWER;
	final static double DIR_CHANCE=0.375;
	final static double BEST_DIST=200;
	final static double APPROACH=800;
	static Rectangle2D.Double fieldRect;
	static double enemyEnergy;
	static int dir;
	static ArrayList<MagestyOfColors.wave> waves;
	static ArrayList<MagestyOfColors.wave> firingAngles=new ArrayList<MagestyOfColors.wave>();
	static double accel;
	static int eDir;
	static double eDirTime;
	static double bestDist=BEST_DIST;
	static double dirChange;
	public void run(){
		fieldRect=new Rectangle2D.Double(18,18,getBattleFieldWidth()-36,getBattleFieldHeight()-36);
		dir=1;
		eDirTime=0;
		waves=new ArrayList<MagestyOfColors.wave>();
		setAllColors(new Color((int)(250*Math.random()),(int)(250*Math.random()),(int)(250*Math.random())));
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		while(true){
			if(getRadarTurnRemainingRadians()==0){
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			}
			execute();
		}
	}
	public void onScannedRobot(ScannedRobotEvent e){
		wave w=new wave();
		double absBearing;
		double velSeg;
		double dist=e.getDistance();
		double adSeg;
		double wallDistance=0;
		Point2D.Double myPos=new Point2D.Double(getX(),getY());
		setTurnRightRadians(Math.cos(absBearing=e.getBearingRadians())+(dir*(bestDist-e.getDistance()))/APPROACH);
		Point2D.Double ePos=project(myPos,e.getDistance(),absBearing+=getHeadingRadians());
		int i;
		double acSeg=accel-(accel=e.getVelocity());
		if(e.getVelocity()>0&&eDir!=1){
			eDir=1;
			eDirTime=getTime();
		}
		if(e.getVelocity()<0&&eDir!=-1){
			eDir=-1;
			eDirTime=getTime();
		}
		double eDirSeg=getTime()-eDirTime;
		if((adSeg=enemyEnergy-(enemyEnergy=e.getEnergy()))>=0.1&&adSeg<=3){
			if(Math.random()>dirChange){
				dir=-dir;
			}
			setAhead(1000*dir);
			setMaxVelocity(8*Math.random()+2);
		}
		if(!fieldRect.contains(project(myPos,100*dir,getHeadingRadians()))){
			setAhead(1000*(dir=-dir));
			setMaxVelocity(8);
		}
		adSeg=e.getVelocity()*Math.cos(e.getHeadingRadians()-absBearing);
		velSeg=e.getVelocity()*Math.sin(e.getHeadingRadians()-absBearing);
		setTurnRadarRightRadians(Utils.normalRelativeAngle(absBearing-getRadarHeadingRadians())*2);
		while(fieldRect.contains(project(ePos,(e.getVelocity()>=0?1:-1)*(wallDistance+=1),e.getHeadingRadians())));
		if(getGunHeat()==0&&getEnergy()>0){
			w.startTime=getTime();
			w.velSeg=velSeg;
			w.startPos=new Point2D.Double(getX(),getY());
			w.startBearing=absBearing;
			w.distSeg=dist;
			w.adSeg=adSeg;
			w.acSeg=acSeg;
			w.wallDist=wallDistance;
			w.dirChangeSeg=eDirSeg;
			waves.add(w);
		}
		for(i=0;i<waves.size();i++){
			w=waves.get(i);
			if((getTime()-w.startTime)*BULLET_SPEED>=Point2D.distance(ePos.x,ePos.y,w.startPos.x,w.startPos.y)){
				w.angle=Utils.normalRelativeAngle(Utils.normalAbsoluteAngle(Math.atan2(ePos.x-w.startPos.x,ePos.y-w.startPos.y)-w.startBearing));
				firingAngles.add(w);
				waves.remove(w);
			}
		}
		double aim=0;
		double maxMatch=Double.POSITIVE_INFINITY;

		for(i=0;i<firingAngles.size();i++){
			w=firingAngles.get(i);
			dist=2*Math.pow(velSeg-w.velSeg,2)+Math.pow(adSeg-w.adSeg,2)+Math.pow((w.distSeg-e.getDistance())/200,2)+2*Math.pow(acSeg-w.acSeg, 2)+
				Math.pow((wallDistance-w.wallDist)/200,2)*2+Math.pow((w.dirChangeSeg-eDirSeg)/50,2);
			if(dist<maxMatch){
				maxMatch=dist;
				aim=w.angle;
			}
		}
		
		setFire(e.getDistance()<100?3:BULLET_POWER);
		setTurnGunRightRadians(Utils.normalRelativeAngle(absBearing-getGunHeadingRadians())+aim);
	}
	public void onDeath(DeathEvent e){
		bestDist=400*Math.random()+100;
	}
	public void onHitByBullet(HitByBulletEvent e){
		dirChange=Math.random();
	}
	public static Point2D.Double project(Point2D.Double origin, double dist,double angle){
		return new Point2D.Double(origin.x+dist*Math.sin(angle),origin.y+dist*Math.cos(angle));
	}
	public static class wave{
		Point2D.Double startPos;
		double startBearing;
		double velSeg;
		double angle;
		double distSeg;
		double startTime;
		double adSeg;
		double acSeg;
		double wallDist;
		double dirChangeSeg;
	}
}
