package com.spikes2212.robot.Commands.commandGroups.autonomousCommands.temp;

import java.util.function.Supplier;

import org.opencv.core.RotatedRect;

import com.spikes2212.dashboard.ConstantHandler;
import com.spikes2212.genericsubsystems.commands.MoveBasicSubsystem;
import com.spikes2212.genericsubsystems.drivetrains.commands.DriveArcade;
import com.spikes2212.robot.Robot;
import com.spikes2212.robot.SubsystemConstants;
import com.spikes2212.robot.Commands.TurnWithIMU;
import com.spikes2212.robot.Commands.commandGroups.MoveLift;
import com.spikes2212.robot.Commands.commandGroups.PlaceCube;
import com.spikes2212.robot.Commands.commandGroups.autonomousCommands.MoveToSetpointWithEncoders;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CloseScaleAuto extends CommandGroup {

	public static final Supplier<Double> FORWARD_SPEED = ConstantHandler.addConstantDouble("close scale forward speed",
			0.3);
	public static final Supplier<Double> FORWARD_TIME = ConstantHandler.addConstantDouble("close scale forward time",
			2);

	public static final Supplier<Double> ANGLE = ConstantHandler.addConstantDouble("close scale rotate angle", 25);

	public CloseScaleAuto(char startSide) {
		// drive to scale setPoint
		addSequential(new MoveToSetpointWithEncoders(MoveToSetpointWithEncoders.BETWEEN_SWITCH_AND_SCALE));

		// turn 30 degrees
		addSequential(new TurnWithIMU(Robot.drivetrain,
				() -> startSide == 'L' ? -TurnWithIMU.ROTATE_SPEED.get() : TurnWithIMU.ROTATE_SPEED.get(), ANGLE.get(),
				TurnWithIMU.ROTATE_TOLERANCE.get()));

		// open lift
		addSequential(new MoveLift(SubsystemConstants.Lift.UP_SPEED));
		addSequential(new MoveBasicSubsystem(Robot.liftLocker, SubsystemConstants.LiftLocker.LOCK_SPEED));

		// drive closer to scale
		addSequential(new DriveArcade(Robot.drivetrain, FORWARD_SPEED, () -> 0.0), FORWARD_TIME.get());

		// score cube
		addSequential(new PlaceCube(SubsystemConstants.Roller.SLOW_ROLL_OUT_SPEED),
				PlaceCube.PLACE_CUBE_WAIT_TIME.get());

		// drive backwards
		addSequential(new DriveArcade(Robot.drivetrain, () -> -FORWARD_SPEED.get(), () -> 0.0), FORWARD_TIME.get());

		// move lift down
		addParallel(new MoveBasicSubsystem(Robot.folder, SubsystemConstants.Folder.DOWN_SPEED_SUPPLIER), 3);
		addSequential(new MoveLift(SubsystemConstants.Lift.DOWN_SPEED_SUPPLIER));
	}
}
