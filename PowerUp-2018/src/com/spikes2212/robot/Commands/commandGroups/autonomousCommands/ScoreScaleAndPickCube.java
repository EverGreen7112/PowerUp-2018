package com.spikes2212.robot.Commands.commandGroups.autonomousCommands;

import java.util.function.Supplier;

import com.spikes2212.dashboard.ConstantHandler;
import com.spikes2212.genericsubsystems.drivetrains.commands.DriveArcade;
import com.spikes2212.genericsubsystems.drivetrains.commands.DriveArcadeWithPID;
import com.spikes2212.robot.ImageProcessingConstants;
import com.spikes2212.robot.Robot;
import com.spikes2212.robot.SubsystemComponents;
import com.spikes2212.robot.SubsystemConstants;
import com.spikes2212.robot.Commands.commandGroups.PickUpCube;
import com.spikes2212.utils.PIDSettings;
import com.spikes2212.utils.RunnableCommand;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ScoreScaleAndPickCube extends CommandGroup {

	// the powerCube pipeline name
	public static final String POWER_CUBE_PIPELINE_NAME = "power-cube";

	// defining PID set point of the point between switch and scale on the y
	// axis
	public static final Supplier<Double> BETWEEN_SWITCH_AND_SCALE = ConstantHandler
			.addConstantDouble("between objectives set point", 160);

	// defining PID set point of the point before the scale on the X axis
	public static final Supplier<Double> CLOSE_SCALE_X_SET_POINT = ConstantHandler
			.addConstantDouble("close scale x setPoint", 30);

	// defining turning constants
	public static final Supplier<Double> TURNING_SPEED = ConstantHandler
			.addConstantDouble("scale and cube - turning speed", 0.5);
	public static final Supplier<Double> TURNING_TIME_OUT = ConstantHandler
			.addConstantDouble("scale and cube - turning timeout", 1.2);

	// defining forward constants
	public static final Supplier<Double> FORWARD_SPEED = ConstantHandler
			.addConstantDouble("scale and cube  - close forward speed", 0.4);
	public static final Supplier<Double> FORWARD_TIME_OUT = ConstantHandler
			.addConstantDouble("scale and cube - close forward timeout", 2);

	// orientation constants
	public static final Supplier<Double> ORIENTATION_FORWARD_SPEED = ConstantHandler
			.addConstantDouble("auto pick cube - orientation forward speed", 0.45);
	public static final Supplier<Double> TOLERANCE = ConstantHandler
			.addConstantDouble("auto pick cube - orienting tolerance", 0);
	public static final Supplier<Double> PID_WAIT_TIME = ConstantHandler
			.addConstantDouble("auto pick cube - PID waitTime", 1);

	public ScoreScaleAndPickCube(String gameData, char startSide) {
		// switch to the power cube pipeline
		addSequential(new RunnableCommand(
				() -> ImageProcessingConstants.NETWORK_TABLE.getEntry("pipeline").setString(POWER_CUBE_PIPELINE_NAME)));

		// score scale
		addSequential(new ScoreScaleAuto(gameData, startSide));

		// rotate 180 degrees
		addSequential(
				new DriveArcade(Robot.drivetrain, () -> 0.0,
						() -> startSide == 'L' ? TURNING_SPEED.get() : -TURNING_SPEED.get()),
				TURNING_TIME_OUT.get() * 2);

		// initiate picking cube sequence
		addParallel(new PickUpCube());

		// chase the cube
		addSequential(new DriveArcadeWithPID(Robot.drivetrain, ImageProcessingConstants.CENTER, () -> 0.0,
				ORIENTATION_FORWARD_SPEED, () -> SubsystemComponents.Roller.hasCube(),
				new PIDSettings(SubsystemConstants.Drivetrain.ORIENTATION_KP.get(),
						SubsystemConstants.Drivetrain.ORIENTATION_KI.get(),
						SubsystemConstants.Drivetrain.ORIENTATION_KD.get(), TOLERANCE.get(), PID_WAIT_TIME.get()),
				ImageProcessingConstants.RANGE));

	}
}
