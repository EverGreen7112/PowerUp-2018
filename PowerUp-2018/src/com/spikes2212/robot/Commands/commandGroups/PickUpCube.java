package com.spikes2212.robot.Commands.commandGroups;

import com.spikes2212.genericsubsystems.commands.MoveBasicSubsystem;
import com.spikes2212.robot.Robot;
import com.spikes2212.robot.SubsystemConstants;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * 
 * This command group picks up the cube that is infront of it.
 * @author Matan
 */
public class PickUpCube extends CommandGroup {

	public PickUpCube() {
		//vacuums the cube
		addSequential(new MoveBasicSubsystem(Robot.roller, SubsystemConstants.Roller.ROLL_IN_SPEED));
		//closing the claw on the cube so it wont fall when we lift it
		addSequential(new MoveBasicSubsystem(Robot.claw, SubsystemConstants.Claw.CLOSE_SPEED));
		//moving the folder up so we can lift the cube safely with out the robot falling
		addSequential(new MoveBasicSubsystem(Robot.folder, SubsystemConstants.Folder.UP_SPEED));
	}
}
 