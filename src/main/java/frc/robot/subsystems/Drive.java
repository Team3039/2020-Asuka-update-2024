package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.RobotMap;

public class Drive extends SubsystemBase {

  public enum DriveControlMode {
    JOYSTICK, PATH_FOLLOWING
  }

  private DriveControlMode driveControlMode = DriveControlMode.JOYSTICK;

  // Left Drive
  private WPI_TalonFX leftMaster;
  private TalonFX leftSlave;

  // Right Drive
  private WPI_TalonFX rightMaster;
  private TalonFX rightSlave;

  private TalonSRX pigeonTalon;

  // Gyro
  private PigeonIMU gyroPigeon;
  private double[] yprPigeon = new double[3];
  private boolean isCalibrating = false;
  private double gyroYawOffsetAngleDeg = 0;

  // Differential Drive
  private DifferentialDrive drive;

  //Path Following
  private final DifferentialDriveOdometry odometry;


  // Subsystem Instance
  private final static Drive INSTANCE = new Drive();

  @SuppressWarnings("removal")
  public Drive() {
    leftMaster = new WPI_TalonFX(RobotMap.leftFrontDrive);
    leftSlave = new TalonFX(RobotMap.leftRearDrive);

    rightMaster = new WPI_TalonFX(RobotMap.rightFrontDrive);
    rightSlave = new TalonFX(RobotMap.rightRearDrive);

    pigeonTalon = new TalonSRX(9);

    TalonFXConfiguration configs = new TalonFXConfiguration();
    configs.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor;

    leftMaster.configAllSettings(configs);
    leftSlave.configAllSettings(configs);

    rightMaster.configAllSettings(configs);
    rightSlave.configAllSettings(configs);

    leftMaster.setInverted(TalonFXInvertType.Clockwise);
    leftSlave.setInverted(TalonFXInvertType.FollowMaster);

    leftMaster.setNeutralMode(NeutralMode.Brake);
    leftSlave.setNeutralMode(NeutralMode.Brake);

    leftSlave.follow(leftMaster);

    rightMaster.setInverted(TalonFXInvertType.CounterClockwise);
    rightSlave.setInverted(TalonFXInvertType.FollowMaster);

    rightMaster.setNeutralMode(NeutralMode.Brake);
    rightSlave.setNeutralMode(NeutralMode.Brake);

    leftMaster.setStatusFramePeriod(StatusFrame.Status_1_General, 100);
    rightMaster.setStatusFramePeriod(StatusFrame.Status_1_General, 100);
    leftSlave.setStatusFramePeriod(StatusFrame.Status_1_General, 100);
    rightSlave.setStatusFramePeriod(StatusFrame.Status_1_General, 100);

    rightSlave.follow(rightMaster);

    drive = new DifferentialDrive(leftMaster, rightMaster);
    drive.setSafetyEnabled(false);

    gyroPigeon = new PigeonIMU(pigeonTalon);
    gyroPigeon.configFactoryDefault();
    leftMaster.configFactoryDefault();
    rightMaster.configFactoryDefault();
    leftSlave.configFactoryDefault();
    rightSlave.configFactoryDefault();
    
    drive.setDeadband(0.05);

    odometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(getGyroFusedHeadingAngleDeg()), 0, 0);
  }

  public static Drive getInstance() {
    return INSTANCE;
  }

  //Encoder Setup
  public synchronized DriveControlMode getControlMode() {
    return driveControlMode;
  }

  public synchronized void setControlMode(DriveControlMode controlMode) {
    this.driveControlMode = controlMode;
  }

  // Encoder Setup

  //Returns left sensors velocity in ticks per 100ms
  public double getLeftVelocityNativeUnits() {
    return leftMaster.getSelectedSensorVelocity(0);
  }

  //Returns right sensors velocity in ticks per 100ms
  public double getRightVelocityNativeUnits() {
    return leftMaster.getSelectedSensorVelocity(0);
  }

  //Returns left sensors position in ticks
  public double getLeftSensorPosition(){
    return leftMaster.getSelectedSensorPosition(0);
  }

  //Returns right sensors position in ticks
  public double getRightSensorPosition(){
    return leftMaster.getSelectedSensorPosition(0);
  }

  //Takes that times the wheel has rotated * by the circumference of the wheel to get its distance traveled in inches
  public static double rotationsToInches(double rotations) {
    return rotations * (Constants.kWheelDiameterInches * Math.PI);
  }

  //Takes inches and converts it to meters using units class
  public static double inchesToMeters(double inches){
    return Units.inchesToMeters(inches);
  }

  //Takes the sensor velocity of an encoder * by 10 to get ticks per second / the encoder PPR to get encoder rotations
  //per second and then uses the rotations to inches functions to get inches per second
  private static double ticksPer100msToInchesPerSec(double ticks_100ms) {
    return rotationsToInches(ticks_100ms * 10.0 / 2048);
  }

  //Returns left inches per second using the sensor velocity and the ticksToInches conversion method
  public double getLeftInchesPerSecond(){
    return ticksPer100msToInchesPerSec(getLeftVelocityNativeUnits()) / Constants.kEncoderRotationToWheelRotationRatio;
  }

  //Returns right inches per second using the sensor velocity and the ticksToInches conversion method
  public double getRightInchesPerSecond(){
    return ticksPer100msToInchesPerSec(getRightVelocityNativeUnits()) / Constants.kEncoderRotationToWheelRotationRatio;
  }

  //Returns left meters per second using inchesPerSecond calculation and inchesToMeters method
  public double getLeftMetersPerSecond(){
    return inchesToMeters(getLeftInchesPerSecond());
  }

  //Returns right meters per second using inchesPerSecond calculation and inchesToMeters method
  public double getRightMetersPerSecond(){
    return inchesToMeters(getRightInchesPerSecond());
  }

  //Sensors positions in ticks / Pulses per Revolution of the Encoder = Encoder Rotations (If ratio is 1:1)
  public double getLeftEncoderRotations() {
    return getLeftSensorPosition() / Constants.kDriveEncoderPPR;
  }

  public double getRightEncoderRotations() {
    return getRightSensorPosition() / Constants.kDriveEncoderPPR;
  }

  //Wheel Rotations = Encoder Rotations (If ratio is 1:1)
  public double getLeftWheelRotations() {
    return getLeftEncoderRotations() / Constants.kEncoderRotationToWheelRotationRatio;
  }

  public double getRightWheelRotations() {
    return getRightEncoderRotations() / Constants.kEncoderRotationToWheelRotationRatio;
  }

  //Returns left distance traveled in inches by taking wheel rotations and converting it to inches
  public double getLeftWheelDistanceInches() {
    return rotationsToInches(getLeftWheelRotations());
  }

  //Returns right distance traveled in inches by taking wheel rotations and converting it to inches
  public double getRightWheelDistanceInches() {
    return rotationsToInches(getRightWheelRotations());
  }

  //Returns left distance traveled in meters using calculated inches distances and inchesToMeters conversion
  public double getLeftWheelDistanceMeters() {
    return inchesToMeters(getLeftWheelDistanceInches());
  }

  //Returns right distance traveled in meters using calculated inches distances and inchesToMeters conversion
  public double getRightWheelDistanceMeters(){
    return inchesToMeters(getRightWheelDistanceInches());
  }

  public synchronized void resetEncoders() {
    leftMaster.setSelectedSensorPosition(0);
    rightMaster.setSelectedSensorPosition(0);
  }

  // Gyro Set Up
  public void calibrateGyro() {
    gyroPigeon.enterCalibrationMode(PigeonIMU.CalibrationMode.Temperature);
  }

  public void endGyroCalibration() {
    if (isCalibrating == true) {
      isCalibrating = false;
    }
  }

  public void setGyroYawOffset(double offsetDeg) {
    gyroYawOffsetAngleDeg = offsetDeg;
  }

  public synchronized double getGyroYawAngleDeg() {
    gyroPigeon.getYawPitchRoll(yprPigeon);
    return yprPigeon[0] + gyroYawOffsetAngleDeg;
  }

  public synchronized double getGyroFusedHeadingAngleDeg() {
    return (gyroPigeon.getFusedHeading() + gyroYawOffsetAngleDeg) % 360;
  }

  public synchronized double getGyroPitchAngle() {
    gyroPigeon.getYawPitchRoll(yprPigeon);
    return yprPigeon[2];
  }

  public synchronized void resetGyroYawAngle() {
    gyroPigeon.setYaw(0);
    gyroPigeon.setFusedHeading(0);
  }

  public synchronized void resetGyroYawAngle(double homeAngle) {
    resetGyroYawAngle();
    setGyroYawOffset(homeAngle);
  }

  public synchronized void driveWithJoystick() {
    double y = -1 * RobotContainer.getDriver().getLeftY() * Constants.DRIVER_Y;
    double rot = RobotContainer.getDriver().getRightX() * Constants.DRIVER_ROT;

    // Calculated Outputs (Limits Output to 12V)
    double leftOutput = rot + y ;
    double rightOutput = y - rot;

    // Assigns Each Motor's Power
    leftMaster.set(ControlMode.PercentOutput, leftOutput);
    rightMaster.set(ControlMode.PercentOutput, rightOutput);
  }

  //Path Following
  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  public Pose2d getPose() {
    return odometry.getPoseMeters();
  }

  /**
   * Returns the current wheel speeds of the robot.
   *
   * @return The current wheel speeds.
   */
  public DifferentialDriveWheelSpeeds getWheelSpeeds() {
    return new DifferentialDriveWheelSpeeds(getLeftMetersPerSecond(), getRightMetersPerSecond());
  }

  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose2d pose) {
    resetEncoders();
    resetGyroYawAngle(0);
    odometry.resetPosition(Rotation2d.fromDegrees(getGyroFusedHeadingAngleDeg()), leftMaster.getSelectedSensorPosition(), rightMaster.getSelectedSensorPosition(), pose);
  }

  /**
   * Controls the left and right sides of the drive directly with voltages.
   *
   * @param leftVolts  the commanded left output
   * @param rightVolts the commanded right output
   */
  public void tankDriveVolts(double leftVolts, double rightVolts) {
    leftMaster.setVoltage(leftVolts);
    rightMaster.setVoltage(rightVolts);
    drive.feed();
  }

  public void periodic() {
    synchronized (Drive.this){
      DriveControlMode currentControlMode = getControlMode();
      switch (currentControlMode){
        case JOYSTICK:
          driveWithJoystick();
          break;
        case PATH_FOLLOWING:
          odometry.update(Rotation2d.fromDegrees(getGyroFusedHeadingAngleDeg()),
                  getLeftWheelDistanceMeters(),getRightWheelDistanceMeters());
          break;
        default:
          System.out.println("Unknown drive control mode: " + currentControlMode);
      }
    }

    // SmartDashboard.putNumber("Left Distance Inches: ", getLeftWheelDistanceInches());
    // SmartDashboard.putNumber("Right Distance Inches: ", getRightWheelDistanceInches());

    // SmartDashboard.putNumber("Left Distance Meters: ", getLeftWheelDistanceMeters());
    // SmartDashboard.putNumber("Right Distance Meters: ", getRightWheelDistanceMeters());

    // SmartDashboard.putNumber("Heading: ", getGyroFusedHeadingAngleDeg());

  }
}
