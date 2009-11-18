package zyx.nano;

import java.awt.Color;

import robocode.AdvancedRobot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class Ant extends AdvancedRobot {
  public static String enemy_;
  public static double target_index_;
  public static int lock_;
  public static int direction_;
  public void run() {
    setAdjustGunForRobotTurn(true);
    setAdjustRadarForGunTurn(true);
    setAllColors(Color.BLACK);
    onRobotDeath(null);
    while ( true ) {
      if ( ++lock_ > 16 ) {//|| getRadarTurnRemainingRadians() == 0 ) {
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        lock_ = -8;
      }
      fire(2.3);
    }
  }
  public void onScannedRobot(ScannedRobotEvent event) {
    double bearing = getHeadingRadians() + event.getBearingRadians();
    double target_index;// = event.getEnergy() / 120 + event.getDistance() / 400;
    if ( (target_index = event.getEnergy() * 0.006 + event.getDistance() * 0.002) < target_index_ ) {
      lock_ = 0;
      target_index_ = target_index;
      enemy_ = event.getName();
    }
    if ( getDistanceRemaining() == 0 ) {
      setAhead(110 * (direction_ = (Math.random() < 0.5 ? -1 : 1)));
    }
    if ( enemy_.equals(event.getName()) ) {
      if ( lock_ >= 0 ) setTurnRadarRightRadians(Utils.normalRelativeAngle(bearing - getRadarHeadingRadians()) * 2.1);
      setTurnGunRightRadians(Utils.normalRelativeAngle(bearing - getGunHeadingRadians()) * 0.9);
      setTurnRightRadians(Utils.normalRelativeAngle(bearing +
          1.5707963267948966192313216916398 - 0.56548667764616278292327580899031 * direction_
          - getHeadingRadians()));
    }
  }
  public void onRobotDeath(RobotDeathEvent event) {
    lock_ = -8;
    setTurnRadarRightRadians(target_index_ = Double.POSITIVE_INFINITY);
  }
}
