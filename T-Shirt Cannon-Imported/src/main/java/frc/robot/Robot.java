/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.livewindow.*;
import edu.wpi.first.wpilibj.Solenoid;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.command.PrintCommand;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  private final XboxController Controller = new XboxController(0); //check driver station for port
  private final Timer m_timer = new Timer();
  private final VictorSPX Right1 = new VictorSPX(3);
  private final VictorSPX Right2 = new VictorSPX(0);
  private final VictorSPX Left1 = new VictorSPX(1);
  private final VictorSPX Left2 = new VictorSPX(2); //ur gonna need to check device numbers pal
  private final Compressor Comp = new Compressor(0);
  private final PowerDistributionPanel PDP = new PowerDistributionPanel();
  private final Solenoid LeftTube = new Solenoid(4);
  private final Solenoid RightTube = new Solenoid(0);
  private final Solenoid BackTube = new Solenoid(3);
  

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    PDP.clearStickyFaults();
    LiveWindow.disableTelemetry(PDP);
    Left1.clearStickyFaults();
    Left2.clearStickyFaults();
    Right1.clearStickyFaults();
    Right2.clearStickyFaults();
    Comp.clearAllPCMStickyFaults();
  }

  /**
   * This function is run once each time the robot enters autonomous mode.
   */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
    Comp.setClosedLoopControl(true);
    Comp.stop();
    LeftTube.clearAllPCMStickyFaults();
  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {
    //driving (left motors need a deadzone)
    if(Controller.getY(Hand.kLeft) < -0.1 | Controller.getY(Hand.kLeft) > 0.1){
      Left1.set(ControlMode.PercentOutput, Controller.getY(Hand.kLeft)*-0.25);
      Left2.follow(Left1);
    }
    Right1.set(ControlMode.PercentOutput, Controller.getY(Hand.kRight)*.25);
    Right2.follow(Right1);

    //compressor start/stop
    if(Controller.getBumperPressed(Hand.kRight)){
      Comp.stop();
    }
    if(Controller.getBumperPressed(Hand.kLeft)){
      Comp.start();
    }

    //solenoid testing
    if(Controller.getTriggerAxis(Hand.kLeft) > 0.9){
      LeftTube.set(true);
    }
    else{
      LeftTube.set(false);
    }
    if(Controller.getTriggerAxis(Hand.kRight) > 0.9){
      RightTube.set(true);
    }
    else{
      RightTube.set(false);
    }
    if(Controller.getAButtonPressed()){
      BackTube.set(true);
    }
    else{
      BackTube.set(false);
    }

    if(LeftTube.get() | RightTube.get() | BackTube.get()){
      Controller.setRumble(RumbleType.kLeftRumble, 1);
      Controller.setRumble(RumbleType.kRightRumble, 1);
    }
    else{
      Controller.setRumble(RumbleType.kLeftRumble, 0);
      Controller.setRumble(RumbleType.kRightRumble, 0);  
    }
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
