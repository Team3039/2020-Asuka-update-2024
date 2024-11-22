package frc.robot;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.SetHopperUnjamMode;
import frc.robot.commands.SetIntake;
import frc.robot.commands.SetIntakeSpeed;
import frc.robot.commands.sequences.FeedCells;
import frc.robot.commands.sequences.ResetHopper;
import frc.robot.commands.sequences.ResetShooter;
import frc.robot.commands.sequences.ShootNearShot;
import frc.robot.controllers.InterpolatedPS4Gamepad;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Hood;
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Turret;

public class RobotContainer {

  public final static Drive drive = Drive.getInstance();
  public final static Intake intake = new Intake();
  public final static Turret turret = new Turret();
  public final static Hopper hopper = new Hopper();
  public final static Shooter shooter = new Shooter();
  public final static Hood hood = new Hood();

  public static final InterpolatedPS4Gamepad driverPad = new InterpolatedPS4Gamepad(1);
  public static final InterpolatedPS4Gamepad operatorPad = new InterpolatedPS4Gamepad(2);

  public static Timer timer = new Timer();

  /* Driver Buttons */
  private final JoystickButton driverX = new JoystickButton(driverPad, PS4Controller.Button.kCross.value);
  private final JoystickButton driverSquare = new JoystickButton(driverPad, PS4Controller.Button.kSquare.value);
  private final JoystickButton driverTriangle = new JoystickButton(driverPad, PS4Controller.Button.kTriangle.value);
  private final JoystickButton driverCircle = new JoystickButton(driverPad, PS4Controller.Button.kCircle.value);

  private final JoystickButton driverL1 = new JoystickButton(driverPad, PS4Controller.Button.kL1.value);
  private final JoystickButton driverR1 = new JoystickButton(driverPad, PS4Controller.Button.kR1.value);
  private final JoystickButton driverL2 = new JoystickButton(driverPad, PS4Controller.Button.kL2.value);
  private final JoystickButton driverR2 = new JoystickButton(driverPad, PS4Controller.Button.kR2.value);
  private final JoystickButton driverL3 = new JoystickButton(driverPad, PS4Controller.Button.kL3.value);
  private final JoystickButton driverR3 = new JoystickButton(driverPad, PS4Controller.Button.kR3.value);


  private final JoystickButton driverPadButton = new JoystickButton(driverPad, PS4Controller.Button.kTouchpad.value);
  private final JoystickButton driverStart = new JoystickButton(driverPad, PS4Controller.Button.kPS.value);

  private final JoystickButton driverShare = new JoystickButton(driverPad, PS4Controller.Button.kShare.value);
  private final JoystickButton driverOptions = new JoystickButton(driverPad, PS4Controller.Button.kOptions.value);

  /* Operator Buttons */
  private final JoystickButton operatorX = new JoystickButton(operatorPad, PS4Controller.Button.kCross.value);
  private final JoystickButton operatorSquare = new JoystickButton(operatorPad, PS4Controller.Button.kSquare.value);
  private final JoystickButton operatorTriangle = new JoystickButton(operatorPad, PS4Controller.Button.kTriangle.value);
  private final JoystickButton operatorCircle = new JoystickButton(operatorPad, PS4Controller.Button.kCircle.value);

  // private final JoystickButton operatorDPadDown = new JoystickButton(operatorPad, PS4Controller.Button.DPAD_DOWN);
  // private final JoystickButton operatorDPadLeft = new JoystickButton(operatorPad, PS4Controller.Button.DPAD_LEFT);
  // private final JoystickButton operatorDPadRight = new JoystickButton(operatorPad, PS4Controller.Button.DPAD_RIGHT);

  
  private final JoystickButton operatorL1 = new JoystickButton(operatorPad, PS4Controller.Button.kL1.value);
  private final JoystickButton operatorR1 = new JoystickButton(operatorPad, PS4Controller.Button.kR1.value);

  private final JoystickButton operatorL2 = new JoystickButton(operatorPad, PS4Controller.Button.kL2.value);
  private final JoystickButton operatorR2 = new JoystickButton(operatorPad, PS4Controller.Button.kR2.value);
  private final JoystickButton operatorR3 = new JoystickButton(operatorPad, PS4Controller.Button.kR3.value);

  private final JoystickButton operatorPadButton = new JoystickButton(operatorPad, PS4Controller.Button.kTouchpad.value);
  private final JoystickButton operatorStart = new JoystickButton(operatorPad, PS4Controller.Button.kPS.value);

  private final JoystickButton operatorShare = new JoystickButton(operatorPad, PS4Controller.Button.kShare.value);
  private final JoystickButton operatorOptions = new JoystickButton(operatorPad, PS4Controller.Button.kOptions.value);


  public RobotContainer() {
    configureButtonBindings();
  }

  
  // Put Button Bindings Here
  private void configureButtonBindings() {

    driverL1.whileTrue(new SetIntake());
    driverL1.onFalse(new ResetHopper());

  
   // driverX.onFalse(new ResetHopper());

    driverR2.whileTrue(new FeedCells());
    driverR2.onFalse(new ResetHopper());

    driverR1.whileTrue(new ShootNearShot());
    driverR1.onFalse(new ResetShooter());

    driverL2.whileTrue(new SetHopperUnjamMode());
    driverL2.onFalse(new ResetHopper());

    

    // driverPadButton.whileTrue(new SetClimbArmSpeed(0.5));
    // driverShare.whileTrue(new SetClimbArmSpeed(-0.3));
    // driverOptions.whileTrue(new SetTurretTrackMode());
    // driverOptions.onFalse(new SetTurretDriverMode());
    // driverTriangle.onTrue(new ShootNearShot());
    // driverSquare.onTrue(new ShootMidShot());
    // driverX.whileTrue(new ShootFarShot());
    // operatorX.onTrue(new ShootFarShot());
    // operatorCircle.onTrue(new ShootMidShot());
    // operatorTriangle.onTrue(new ShootNearShot());
    // operatorR2.whileTrue(new FeedCells());
    // operatorR2.onFalse(new ResetHopper());
    // operatorR2.onFalse(new ResetShooter());
    // operatorL1.whileTrue(new SetIntakeSpeed(.6));
    // operatorL1.whileTrue(new SetShooterSpeed(-.8));
    // operatorL1.whileTrue(new SetHopperU
  
   
    // operatorL1.onFalse(new SetIntakeSpeed(0));
    // operatorL1.onFalse(new SetHopperIdleMode());
    // operatorL1.onFalse(new SetShooterSpeed(0));
    // operatorL2.onTrue(new ResetHopper());
    // operatorL2.onTrue(new ResetShooter()); 
  }

  //Get Controller Objects
  public static InterpolatedPS4Gamepad getDriver() {
    return driverPad;
  }

  public static InterpolatedPS4Gamepad getOperator() {
    return operatorPad;
  }
}