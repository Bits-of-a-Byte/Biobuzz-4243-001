package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * Field-oriented mecanum TeleOp using the Pinpoint's built-in IMU.
 * Mount the Pinpoint flat, label up; connect two perpendicular 4-bar pods.
 * Face the robot toward your chosen field-forward direction before START.
 * Gamepad 1: left stick translates, right stick turns, LB slows, Y redefines field-forward.
 * Gamepad 2: A toggles both intakes, X/B reverse-pulse the front/back intake.
 * D-pad up/down toggles full/slow transfer; left/right reverse-pulses back/front.
 * Axon MAX MK2 servos must be programmed with CR-mode firmware before use.
 */
@TeleOp(name = "BioBuzz 4243 Field Oriented", group = "TeleOp")
public class BioBuzz_4243_2nd extends LinearOpMode {
    private static final double STICK_DEADZONE = 0.05;
    private static final double SLOW_SPEED = 0.35;
    private static final double INTAKE_POWER = 1.0;
    private static final double REVERSE_POWER = 0.5;
    private static final double REVERSE_BURST_SECONDS = 0.20;
    private static final double TRANSFER_FULL_POWER = 1.0;
    private static final double TRANSFER_SLOW_POWER = 0.25;
    private static final double TRANSFER_REVERSE_POWER = 0.5;
    private static final double TRANSFER_REVERSE_BURST_SECONDS = 0.20;
    private static final double CALIBRATION_WAIT_SECONDS = 0.30;

    // Drivetrain and pod settings are shared with Pedro in BioBuzzDriveConfig.

    @Override
    public void runOpMode() {
        DcMotor bR = hardwareMap.get(DcMotor.class, BioBuzzDriveConfig.BACK_RIGHT_NAME);
        DcMotor bL = hardwareMap.get(DcMotor.class, BioBuzzDriveConfig.BACK_LEFT_NAME);
        DcMotor fR = hardwareMap.get(DcMotor.class, BioBuzzDriveConfig.FRONT_RIGHT_NAME);
        DcMotor fL = hardwareMap.get(DcMotor.class, BioBuzzDriveConfig.FRONT_LEFT_NAME);
        DcMotor iF = hardwareMap.get(DcMotor.class, "iF");
        DcMotor iB = hardwareMap.get(DcMotor.class, "iB");
        DcMotor[] allMotors = {fL, fR, bL, bR, iF, iB};

        // Verify these directions against your motor mounting and bevel gears.
        fL.setDirection(BioBuzzDriveConfig.FRONT_LEFT_DIRECTION);
        bL.setDirection(BioBuzzDriveConfig.BACK_LEFT_DIRECTION);
        fR.setDirection(BioBuzzDriveConfig.FRONT_RIGHT_DIRECTION);
        bR.setDirection(BioBuzzDriveConfig.BACK_RIGHT_DIRECTION);
        // Positive power must collect pollen; reverse either intake direction if needed.
        iF.setDirection(DcMotorSimple.Direction.FORWARD);
        iB.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotor motor : allMotors) {
            motor.setPower(0);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        CRServoImplEx mk2bL = hardwareMap.get(CRServoImplEx.class, "mk2bL");
        CRServoImplEx mk2bR = hardwareMap.get(CRServoImplEx.class, "mk2bR");
        CRServoImplEx mk2fL = hardwareMap.get(CRServoImplEx.class, "mk2fL");
        CRServoImplEx mk2fR = hardwareMap.get(CRServoImplEx.class, "mk2fR");
        CRServoImplEx[] allTransferServos = {mk2bL, mk2bR, mk2fL, mk2fR};

        try {
            // Change individual directions so positive power transfers toward the launcher.
            configureTransferServo(mk2bL, DcMotorSimple.Direction.FORWARD);
            configureTransferServo(mk2bR, DcMotorSimple.Direction.FORWARD);
            configureTransferServo(mk2fL, DcMotorSimple.Direction.FORWARD);
            configureTransferServo(mk2fR, DcMotorSimple.Direction.FORWARD);

            GoBildaPinpointDriver pinpoint =
                    hardwareMap.get(GoBildaPinpointDriver.class, BioBuzzDriveConfig.PINPOINT_NAME);
            pinpoint.setErrorDetectionType(GoBildaPinpointDriver.ErrorDetectionType.LOCAL_TEST);
            pinpoint.setOffsets(BioBuzzDriveConfig.X_POD_OFFSET_MM,
                    BioBuzzDriveConfig.Y_POD_OFFSET_MM, DistanceUnit.MM);
            pinpoint.setEncoderResolution(
                    BioBuzzDriveConfig.POD_TYPE);
            pinpoint.setEncoderDirections(BioBuzzDriveConfig.X_POD_DIRECTION,
                    BioBuzzDriveConfig.Y_POD_DIRECTION);

            // Keep the robot completely still during INIT calibration.
            pinpoint.resetPosAndIMU();
            ElapsedTime calibrationClock = new ElapsedTime();
            while (opModeInInit()) {
                pinpoint.update();
                boolean ready = pinpoint.getDeviceStatus() == GoBildaPinpointDriver.DeviceStatus.READY
                        && calibrationClock.seconds() >= CALIBRATION_WAIT_SECONDS;
                telemetry.addData("Pinpoint", pinpoint.getDeviceStatus());
                telemetry.addData("Status", ready ? "Ready to START" : "Waiting for Pinpoint");
                telemetry.addLine("Keep still during calibration; face the chosen field-forward direction.");
                telemetry.addData("Heading (deg)", "%.1f", pinpoint.getHeading(AngleUnit.DEGREES));
                telemetry.addData("Pod check (mm)", "X %.1f | Y %.1f",
                        pinpoint.getPosX(DistanceUnit.MM), pinpoint.getPosY(DistanceUnit.MM));
                telemetry.addLine("Gamepad 1: left stick drive, right stick turn, LB slow, Y field-forward");
                telemetry.addLine("Gamepad 2: A intakes, X front reverse, B back reverse");
                telemetry.addLine("D-pad up/down: full/slow transfer; left/right: back/front reverse");
                telemetry.update();
                idle();
            }
            waitForStart();
            if (isStopRequested()) {
                return;
            }

            boolean intakesEnabled = false;
            boolean previousA = gamepad2.a;
            boolean previousX = gamepad2.x;
            boolean previousB = gamepad2.b;
            boolean previousY = gamepad1.y;
            double frontReverseUntil = 0;
            double backReverseUntil = 0;
            ElapsedTime intakeClock = new ElapsedTime();
            boolean fieldReferenceSet = false;
            double fieldForwardHeading = 0;
            TransferServoControl transfer = new TransferServoControl(
                    TRANSFER_FULL_POWER, TRANSFER_SLOW_POWER,
                    TRANSFER_REVERSE_POWER, TRANSFER_REVERSE_BURST_SECONDS);
            transfer.syncButtons(gamepad2.dpad_up, gamepad2.dpad_down,
                    gamepad2.dpad_left, gamepad2.dpad_right);

            while (opModeIsActive()) {
                // Read full data so the heading AND fault status are refreshed every loop.
                pinpoint.update();
                double rawHeading = pinpoint.getHeading(AngleUnit.RADIANS);
                boolean headingReady =
                        pinpoint.getDeviceStatus() == GoBildaPinpointDriver.DeviceStatus.READY
                        && calibrationClock.seconds() >= CALIBRATION_WAIT_SECONDS
                        && Double.isFinite(rawHeading);

                boolean yPressed = gamepad1.y;
                if (headingReady && (!fieldReferenceSet || (yPressed && !previousY))) {
                    // Change only the driver reference. Do not recalibrate a moving IMU.
                    fieldForwardHeading = rawHeading;
                    fieldReferenceSet = true;
                }
                previousY = yPressed;

                double fieldForward = applyDeadzone(-gamepad1.left_stick_y);
                double fieldRight = applyDeadzone(gamepad1.left_stick_x);
                double turn = applyDeadzone(gamepad1.right_stick_x);
                double speed = gamepad1.left_bumper ? SLOW_SPEED : 1.0;
                double heading = headingReady ? rawHeading - fieldForwardHeading : 0;

                // An unavailable heading stops the drivetrain instead of changing control frames.
                double[] powers = headingReady
                        ? FieldOrientedDrive.calculate(fieldForward, fieldRight, turn, heading, speed)
                        : new double[] {0, 0, 0, 0};
                fL.setPower(powers[0]);
                fR.setPower(powers[1]);
                bL.setPower(powers[2]);
                bR.setPower(powers[3]);

                boolean aPressed = gamepad2.a;
                boolean xPressed = gamepad2.x;
                boolean bPressed = gamepad2.b;
                double now = intakeClock.seconds();
                if (aPressed && !previousA) {
                    intakesEnabled = !intakesEnabled;
                }
                if (xPressed && !previousX) {
                    frontReverseUntil = now + REVERSE_BURST_SECONDS;
                }
                if (bPressed && !previousB) {
                    backReverseUntil = now + REVERSE_BURST_SECONDS;
                }
                previousA = aPressed;
                previousX = xPressed;
                previousB = bPressed;

                // Reverse pulses override either on/off state without pausing driving.
                boolean frontReversing = now < frontReverseUntil;
                boolean backReversing = now < backReverseUntil;
                double intakePower = intakesEnabled ? INTAKE_POWER : 0;
                iF.setPower(frontReversing ? -REVERSE_POWER : intakePower);
                iB.setPower(backReversing ? -REVERSE_POWER : intakePower);

                transfer.update(now, gamepad2.dpad_up, gamepad2.dpad_down,
                        gamepad2.dpad_left, gamepad2.dpad_right);
                mk2fL.setPower(transfer.getFrontPower());
                mk2fR.setPower(transfer.getFrontPower());
                mk2bL.setPower(transfer.getBackPower());
                mk2bR.setPower(transfer.getBackPower());

                telemetry.addData("Transfer mode", transfer.getMode());
                telemetry.addData("Transfer power", "Front %.2f | Back %.2f",
                        transfer.getFrontPower(), transfer.getBackPower());

                telemetry.addData("Pinpoint", pinpoint.getDeviceStatus());
                telemetry.addData("Drive", headingReady ? "Field oriented" : "Stopped: heading unavailable");
                telemetry.addData("Field heading (deg)", "%.1f",
                        AngleUnit.normalizeDegrees(Math.toDegrees(heading)));
                telemetry.addData("Speed", gamepad1.left_bumper ? "Slow (35%)" : "Full");
                telemetry.addData("Front drive", "fL %.2f | fR %.2f", powers[0], powers[1]);
                telemetry.addData("Back drive", "bL %.2f | bR %.2f", powers[2], powers[3]);
                telemetry.addData("Intakes toggled", intakesEnabled ? "On" : "Off");
                telemetry.addData("Front intake", frontReversing ? "Reverse burst"
                        : (intakesEnabled ? "Collecting" : "Stopped"));
                telemetry.addData("Back intake", backReversing ? "Reverse burst"
                        : (intakesEnabled ? "Collecting" : "Stopped"));
                telemetry.update();
                idle();
            }
        } finally {
            for (DcMotor motor : allMotors) {
                motor.setPower(0);
            }
            for (CRServoImplEx servo : allTransferServos) {
                servo.setPower(0);
            }
        }
    }

    private static void configureTransferServo(CRServoImplEx servo,
                                               DcMotorSimple.Direction direction) {
        servo.setPower(0);
        servo.setDirection(direction);
        // Axon MAX MK2 CR range: neutral 1500 us, full reverse/forward 1050/1950 us.
        servo.setPwmRange(new PwmControl.PwmRange(1050, 1950));
    }

    private static double applyDeadzone(double value) {
        if (Math.abs(value) <= STICK_DEADZONE) {
            return 0;
        }
        return Math.copySign(
                (Math.abs(value) - STICK_DEADZONE) / (1.0 - STICK_DEADZONE), value);
    }
}
