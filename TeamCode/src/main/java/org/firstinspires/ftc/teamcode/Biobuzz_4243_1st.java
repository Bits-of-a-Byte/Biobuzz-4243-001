package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Robot-centric mecanum drive (standard X-pattern wheel arrangement).
 * Gamepad 1: left stick drives/strafes, right stick turns,
 * and holding the left bumper reduces speed for precision driving.
 * Gamepad 2: A toggles both intakes; X/B pulse the front/back intake in reverse.
 */
@TeleOp(name = "Biobuzz 4243 Mecanum", group = "TeleOp")
public class Biobuzz_4243_1st extends LinearOpMode {
    private static final double STICK_DEADZONE = 0.05;
    private static final double SLOW_SPEED = 0.35;
    private static final double INTAKE_POWER = 1.0;
    private static final double REVERSE_POWER = 0.5;
    private static final double REVERSE_BURST_SECONDS = 0.20;

    private DcMotor bR;
    private DcMotor bL;
    private DcMotor fR;
    private DcMotor fL;
    private DcMotor iF;
    private DcMotor iB;

    @Override
    public void runOpMode() throws InterruptedException {
        bR = hardwareMap.get(DcMotor.class, "bR");
        bL = hardwareMap.get(DcMotor.class, "bL");
        fR = hardwareMap.get(DcMotor.class, "fR");
        fL = hardwareMap.get(DcMotor.class, "fL");
        iF = hardwareMap.get(DcMotor.class, "iF");
        iB = hardwareMap.get(DcMotor.class, "iB");

        // Positive power must pull pollen in. Reverse either direction if needed.
        iF.setDirection(DcMotorSimple.Direction.FORWARD);
        iB.setDirection(DcMotorSimple.Direction.FORWARD);

        // Adjust individual directions if your motor mounting differs.
        fL.setDirection(DcMotorSimple.Direction.REVERSE);
        bL.setDirection(DcMotorSimple.Direction.REVERSE);
        fR.setDirection(DcMotorSimple.Direction.FORWARD);
        bR.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotor motor : new DcMotor[] {fL, fR, bL, bR, iF, iB}) {
            motor.setPower(0);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        telemetry.addData("Status", "Ready");
        telemetry.addData("Drive", "Left stick: forward/back and strafe");
        telemetry.addData("Turn", "Right stick: left/right");
        telemetry.addData("Slow mode", "Hold left bumper (35% power)");
        telemetry.addData("Gamepad 2", "A: toggle intakes | X: reverse front | B: reverse back");
        telemetry.update();

        waitForStart();

        boolean intakesEnabled = false;
        // A button held during INIT must be released before it can trigger an action.
        boolean previousA = gamepad2.a;
        boolean previousX = gamepad2.x;
        boolean previousB = gamepad2.b;
        ElapsedTime intakeClock = new ElapsedTime();
        double frontReverseUntil = 0;
        double backReverseUntil = 0;

        try {
            while (opModeIsActive()) {
                // Negate Y because pushing the stick forward gives a negative value.
                double forward = applyDeadzone(-gamepad1.left_stick_y);
                double strafe = applyDeadzone(gamepad1.left_stick_x);
                double turn = applyDeadzone(gamepad1.right_stick_x);
                double speed = gamepad1.left_bumper ? SLOW_SPEED : 1.0;

                // Normalize all wheels together to preserve the requested direction.
                double denominator = Math.max(
                        Math.abs(forward) + Math.abs(strafe) + Math.abs(turn), 1.0);
                double frontLeftPower = (forward + strafe + turn) / denominator * speed;
                double frontRightPower = (forward - strafe - turn) / denominator * speed;
                double backLeftPower = (forward - strafe + turn) / denominator * speed;
                double backRightPower = (forward + strafe - turn) / denominator * speed;

                fL.setPower(frontLeftPower);
                fR.setPower(frontRightPower);
                bL.setPower(backLeftPower);
                bR.setPower(backRightPower);

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

                // Timed overrides keep driving responsive and work even when intakes are off.
                boolean frontReversing = now < frontReverseUntil;
                boolean backReversing = now < backReverseUntil;
                double normalIntakePower = intakesEnabled ? INTAKE_POWER : 0;
                iF.setPower(frontReversing ? -REVERSE_POWER : normalIntakePower);
                iB.setPower(backReversing ? -REVERSE_POWER : normalIntakePower);

                telemetry.addData("Intakes", intakesEnabled ? "On" : "Off");
                telemetry.addData("Front intake", frontReversing ? "Reverse burst"
                        : (intakesEnabled ? "Collecting" : "Stopped"));
                telemetry.addData("Back intake", backReversing ? "Reverse burst"
                        : (intakesEnabled ? "Collecting" : "Stopped"));

                telemetry.addData("Mode", gamepad1.left_bumper ? "Slow" : "Full speed");
                telemetry.addData("Drive", "Forward %.2f | Strafe %.2f | Turn %.2f",
                        forward, strafe, turn);
                telemetry.addData("Front", "fL %.2f | fR %.2f",
                        frontLeftPower, frontRightPower);
                telemetry.addData("Back", "bL %.2f | bR %.2f",
                        backLeftPower, backRightPower);
                telemetry.update();
                idle();
            }
        } finally {
            fL.setPower(0);
            fR.setPower(0);
            bL.setPower(0);
            bR.setPower(0);
            iF.setPower(0);
            iB.setPower(0);
        }
    }

    private static double applyDeadzone(double value) {
        if (Math.abs(value) <= STICK_DEADZONE) {
            return 0;
        }
        return Math.copySign(
                (Math.abs(value) - STICK_DEADZONE) / (1.0 - STICK_DEADZONE), value);
    }
}
