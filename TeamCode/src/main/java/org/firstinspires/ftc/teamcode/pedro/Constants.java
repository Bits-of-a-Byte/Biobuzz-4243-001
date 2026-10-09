package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.follower.Follower;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.BioBuzzDriveConfig;

/** Pedro 3 configuration. Complete AutoTune before enabling path following. */
public final class Constants {
    private Constants() {
    }

    public static boolean FOLLOWER_TUNED = false;

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set(BioBuzzDriveConfig.FRONT_LEFT_NAME);
        c.backLeftName.set(BioBuzzDriveConfig.BACK_LEFT_NAME);
        c.frontRightName.set(BioBuzzDriveConfig.FRONT_RIGHT_NAME);
        c.backRightName.set(BioBuzzDriveConfig.BACK_RIGHT_NAME);
        c.frontLeftDirection.set(BioBuzzDriveConfig.FRONT_LEFT_DIRECTION);
        c.backLeftDirection.set(BioBuzzDriveConfig.BACK_LEFT_DIRECTION);
        c.frontRightDirection.set(BioBuzzDriveConfig.FRONT_RIGHT_DIRECTION);
        c.backRightDirection.set(BioBuzzDriveConfig.BACK_RIGHT_DIRECTION);
        c.manualBrakeMode.set(true);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set(BioBuzzDriveConfig.PINPOINT_NAME);
        c.podType.set(BioBuzzDriveConfig.POD_TYPE);
        c.offsetUnits.set(DistanceUnit.MM);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.xPodOffset.set(BioBuzzDriveConfig.X_POD_OFFSET_MM);
        c.yPodOffset.set(BioBuzzDriveConfig.Y_POD_OFFSET_MM);
        c.xPodDirection.set(BioBuzzDriveConfig.X_POD_DIRECTION);
        c.yPodDirection.set(BioBuzzDriveConfig.Y_POD_DIRECTION);
        c.resetMode.set(PinpointLocalizer.ResetMode.RECALIBRATE_IMU);
    });

    // TODO: Replace this block with YOUR Foresight AutoTune Java output, and import
    // Controller, Matrix, Vector2D as required by that output. There are no sample gains.
    // After saving all measured settings and testing localization, set FOLLOWER_TUNED=true.
    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
    });

    public static Mecanum createDrivetrain(HardwareMap hardwareMap) {
        return new Mecanum(hardwareMap, drivetrainConfig);
    }

    public static PinpointLocalizer createLocalizer(HardwareMap hardwareMap) {
        return new PinpointLocalizer(hardwareMap, localizerConfig);
    }

    public static Foresight createAlgorithm() {
        if (!FOLLOWER_TUNED) {
            throw new IllegalStateException(
                    "Complete Pedro AutoTune and save its ForesightConfig before enabling autonomous. "
                            + "See TeamCode/PEDRO_SETUP.md.");
        }
        return new Foresight(foresightConfig);
    }

    public static Follower create(HardwareMap hardwareMap) {
        // Check tuning before accessing hardware or commanding the drivetrain.
        Foresight algorithm = createAlgorithm();
        return new Follower(createLocalizer(hardwareMap), createDrivetrain(hardwareMap), algorithm);
    }
}
