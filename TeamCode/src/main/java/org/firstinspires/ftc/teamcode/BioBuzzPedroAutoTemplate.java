package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import static com.pedropathing.api.Paths.line;

/**
 * Starter for future autos: a 24-inch straight test path, not a BIOBUZZ scoring route.
 * Complete Pedro tuning, choose actual field poses, then remove @Disabled.
 * Positions use inches and headings use radians; (0,0,0) is just a test reference.
 */
@Disabled
@Autonomous(name = "BioBuzz Pedro Auto Template", group = "Pedro")
public class BioBuzzPedroAutoTemplate extends LinearOpMode {
    private static final Pose START_POSE = new Pose(0, 0, 0);
    private static final Pose END_POSE = new Pose(24, 0, 0);
    private static final double PATH_TIMEOUT_SECONDS = 10;

    @Override
    public void runOpMode() {
        if (!Constants.FOLLOWER_TUNED) {
            telemetry.addLine("Complete AutoTune and save Constants.foresightConfig first.");
            telemetry.addLine("See TeamCode/PEDRO_SETUP.md.");
            telemetry.update();
            return;
        }

        BioBuzzMechanisms mechanisms = new BioBuzzMechanisms(hardwareMap);
        Follower follower = null;
        try {
            // Constructor recalibrates the Pinpoint; keep the robot still during INIT.
            follower = Constants.create(hardwareMap);
            GoBildaPinpointDriver pinpoint = hardwareMap.get(
                    GoBildaPinpointDriver.class, BioBuzzDriveConfig.PINPOINT_NAME);
            Path route = line(START_POSE, END_POSE).constant(START_POSE);

            while (opModeInInit()) {
                pinpoint.update();
                telemetry.addData("Pinpoint", pinpoint.getDeviceStatus());
                telemetry.addLine("Place robot at START_POSE; keep still during calibration.");
                telemetry.addLine("Test route: 24 inches forward. Intakes and transfer remain stopped.");
                telemetry.update();
                idle();
            }
            waitForStart();
            if (isStopRequested()) {
                return;
            }
            pinpoint.update();
            if (pinpoint.getDeviceStatus() != GoBildaPinpointDriver.DeviceStatus.READY) {
                telemetry.addLine("Cannot start path: Pinpoint is not READY.");
                telemetry.update();
                return;
            }

            // Establish the actual field pose only after calibration is complete.
            follower.setPose(START_POSE);
            follower.follow(route);
            ElapsedTime pathClock = new ElapsedTime();

            while (opModeIsActive()) {
                // Check status before letting the follower command motion.
                pinpoint.update();
                if (pinpoint.getDeviceStatus() != GoBildaPinpointDriver.DeviceStatus.READY) {
                    telemetry.addData("Stopped: Pinpoint fault", pinpoint.getDeviceStatus());
                    telemetry.update();
                    break;
                }
                if (pathClock.seconds() >= PATH_TIMEOUT_SECONDS) {
                    telemetry.addLine("Stopped: path timeout.");
                    telemetry.update();
                    break;
                }

                follower.update();
                Pose pose = follower.pose();
                telemetry.addData("Pose (in, deg)", "X %.1f | Y %.1f | Heading %.1f",
                        pose.x(), pose.y(), Math.toDegrees(pose.heading()));
                telemetry.addData("Following", follower.isBusy());
                telemetry.update();

                if (!follower.isBusy()) {
                    // Add your next nonblocking path/action state here.
                    // Mechanism commands available:
                    // mechanisms.setIntakePowers(frontPower, backPower);
                    // mechanisms.setTransferPowers(frontPower, backPower);
                    break;
                }
                idle();
            }
        } finally {
            // stop() changes Pedro's mode; drivetrain.stop() applies zero power immediately.
            if (follower != null) {
                follower.stop();
                follower.drivetrain.stop();
            }
            mechanisms.stop();
        }
    }
}
