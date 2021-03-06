package com.spikes2212.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.spikes2212.utils.DoubleSpeedcontroller;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.interfaces.Gyro;

public class SubsystemComponents {

	public static class Folder {
		public static final DoubleSpeedcontroller MOTORS = new DoubleSpeedcontroller(
				new VictorSP(RobotMap.PWM.FOLDER_1), new VictorSP(RobotMap.PWM.FOLDER_2));
		public static final DigitalInput MAX_LIMIT = new DigitalInput(RobotMap.DIO.FOLDER_MAX_LIMIT);
		public static final DigitalInput MIN_LIMIT = new DigitalInput(RobotMap.DIO.FOLDER_MIN_LIMIT);
	}

	public static class Drivetrain {
		public static final DoubleSpeedcontroller RIGHT_MOTOR = new DoubleSpeedcontroller(
				new WPI_TalonSRX(RobotMap.CAN.DRIVE_RIGHT1), new WPI_TalonSRX(RobotMap.CAN.DRIVE_RIGHT2));
		public static final DoubleSpeedcontroller LEFT_MOTOR = new DoubleSpeedcontroller(
				new WPI_TalonSRX(RobotMap.CAN.DRIVE_LEFT1), new WPI_TalonSRX(RobotMap.CAN.DRIVE_LEFT2));

		public static final Encoder RIGHT_ENCODER = new Encoder(RobotMap.DIO.DRIVE_RIGHT_ENCODER_A,
				RobotMap.DIO.DRIVE_RIGHT_ENCODER_B);
		public static final Encoder LEFT_ENCODER = new Encoder(RobotMap.DIO.DRIVE_LEFT_ENCODER_A,
				RobotMap.DIO.DRIVE_LEFT_ENCODER_B);
		public static final Gyro GYRO = new ADXRS450_Gyro();
	}

	public static class Roller {
		public static boolean hasCube() {
			return SubsystemConstants.Roller.LASER_SENSOR_CONSTANT.get()
					/ LASER_SENSOR.getVoltage() <= SubsystemConstants.Roller.CUBE_DISTANCE.get();
		}

		public static final VictorSP MOTOR_RIGHT = new VictorSP(RobotMap.PWM.ROLLER_RIGHT);
		public static final VictorSP MOTOR_LEFT = new VictorSP(RobotMap.PWM.ROLLER_LEFT);
		public static final AnalogInput LASER_SENSOR = new AnalogInput(RobotMap.ANALOG_IN.ROLLER_LASER_SENSOR);
	}

	public static class LiftLocker {
		public static final VictorSP MOTOR = new VictorSP(RobotMap.PWM.LIFT_LOCKER);
		public static final DigitalInput LIMIT_UNLOCKED = new DigitalInput(RobotMap.DIO.LIFT_LOCKER_UNLOCKED_LIMIT);
		public static final DigitalInput LIMIT_LOCKED = new DigitalInput(RobotMap.DIO.LIFT_LOCKER_LOCKED_LIMIT);
	}

	public static class Lift {

		public enum HallEffects {
			SWITCH(1, new DigitalInput(RobotMap.DIO.LIFT_HALL_EFFECTS_SWITCH)), LOW_SCALE(2,
					new DigitalInput(RobotMap.DIO.LIFT_HALL_EFFECTS_LOW_SCALE)), MID_SCALE(3,
							new DigitalInput(RobotMap.DIO.LIFT_HALL_EFFECTS_MID_SCALE));

			private final int index;
			private DigitalInput hallEffect;

			private HallEffects(int index, DigitalInput hallEffect) {
				this.index = index;
				this.hallEffect = hallEffect;
			}

			public int getIndex() {
				return index;
			}

			public DigitalInput getHallEffect() {
				return hallEffect;
			}
		}

		public static final DoubleSpeedcontroller MOTORS = new DoubleSpeedcontroller(
				new WPI_TalonSRX(RobotMap.CAN.LIFT_MOTOR_A), new WPI_TalonSRX(RobotMap.CAN.LIFT_MOTOR_B));
		public static final DigitalInput LIMIT_UP = new DigitalInput(RobotMap.DIO.LIFT_LIMIT_UP);
		public static final DigitalInput LIMIT_DOWN = new DigitalInput(RobotMap.DIO.LIFT_LIMIT_DOWN);
		// stores the position of the lift to display on shuffleBoard
		private static double position = 0;

		public static void updateLiftPosition() {
			if (LIMIT_UP.get())
				position = (MOTORS.get() >= SubsystemConstants.Lift.STAYING_SPEED.get()) ? 4 : 3.5;
			// The hall effects are wired to say false when there is magnet near
			// them so we need to invert them in code
			else if (!HallEffects.MID_SCALE.getHallEffect().get())
				position = (MOTORS.get() >= SubsystemConstants.Lift.STAYING_SPEED.get()) ? 3.5 : 2.5;
			else if (!HallEffects.LOW_SCALE.getHallEffect().get())
				position = (MOTORS.get() >= SubsystemConstants.Lift.STAYING_SPEED.get()) ? 2.5 : 1.5;
			else if (!HallEffects.SWITCH.getHallEffect().get())
				position = (MOTORS.get() >= SubsystemConstants.Lift.STAYING_SPEED.get()) ? 1.5 : 0.5;
			else if (LIMIT_DOWN.get())
				position = (MOTORS.get() >= SubsystemConstants.Lift.STAYING_SPEED.get()) ? 0.5 : 0;

		}

		public static double getPosition() {
			return position;
		}
	}
}