/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.sequences;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.SetShooterSpeed;
import frc.robot.commands.SetTopWheel;
import frc.robot.commands.SetTurretDriverMode;
import frc.robot.commands.SetTurretJoystickMode;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class ResetShooter extends SequentialCommandGroup {
  /**
   * Creates a new ResetShooter.
   */
  public ResetShooter() {
    // Add your commands in the super() call, e.g.
    // super(new FooCommand(), new BarCommand());
    super(
          new SetTurretDriverMode(),
          new SetShooterSpeed(0.15),
          new SetTopWheel(0.15),
          new WaitCommand(1),
          new SetTurretJoystickMode()
          );
  }
}
