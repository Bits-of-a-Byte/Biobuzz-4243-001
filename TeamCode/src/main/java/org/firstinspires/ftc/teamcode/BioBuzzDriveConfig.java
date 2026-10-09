package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/** Shared drivetrain and odometry settings for the second TeleOp and Pedro autos. */
public final class BioBuzzDriveConfig {
    private BioBuzzDriveConfig() {
    }

    public static final String FRONT_LEFT_NAME = "fL";
    public static final String FRONT_RIGHT_NAME = "fR";
    public static final String BACK_LEFT_NAME = "bL";
    public static final String BACK_RIGHT_NAME = "bR";
    public static final String PINPOINT_NAME = "pinpoint";

    // Verify against your mounting and bevel gears: positive drive power moves forward.
    public static final DcMotorSimple.Direction FRONT_LEFT_DIRECTION = DcMotorSimple.Direction.REVERSE;
    public static final DcMotorSimple.Direction BACK_LEFT_DIRECTION = DcMotorSimple.Direction.REVERSE;
    public static final DcMotorSimple.Direction FRONT_RIGHT_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static final DcMotorSimple.Direction BACK_RIGHT_DIRECTION = DcMotorSimple.Direction.FORWARD;

    public static final GoBildaPinpointDriver.GoBildaOdometryPods POD_TYPE =
            GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD;

    // TODO: Replace zero placeholders with measured values or Pinpoint AutoTune results.
    // X forward-tracking pod: sideways from robot center, positive LEFT / negative RIGHT.
    // Y sideways-tracking pod: fore/aft from robot center, positive AHEAD / negative BEHIND.
    // These values are MILLIMETERS, even though Pedro path positions use inches.
    public static final double X_POD_OFFSET_MM = 0.0;
    public static final double Y_POD_OFFSET_MM = 0.0;

    // Estimated X must increase forward; estimated Y must increase when moving left.
    public static final GoBildaPinpointDriver.EncoderDirection X_POD_DIRECTION =
            GoBildaPinpointDriver.EncoderDirection.FORWARD;
    public static final GoBildaPinpointDriver.EncoderDirection Y_POD_DIRECTION =
            GoBildaPinpointDriver.EncoderDirection.FORWARD;
}
