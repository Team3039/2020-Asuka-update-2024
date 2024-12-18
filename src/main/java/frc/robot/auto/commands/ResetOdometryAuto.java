package frc.robot.auto.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Drive;

public class ResetOdometryAuto extends Command {

    private static final Drive mDrive = Drive.getInstance();

    @Override
    public void initialize() {
        System.out.println("Reset Odometry Started");
        mDrive.resetOdometry(new Pose2d());
    }

    @Override
    public void execute() {

    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Reset Odometry Finished");
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
