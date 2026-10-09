package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.TunerRegistrar;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.PinpointTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;

/** Registers AutoTune tools; open http://192.168.43.1:10158 on the robot Wi-Fi. */
public final class Tuning {
    private Tuning() {
    }

    @OpModeRegistrar
    public static void register(OpModeManager manager) {
        // AutoTune 1.0.2's @Tuner scanner dereferences Map.putIfAbsent's null
        // return on its first factory. Use the supported public registrar instead.
        registerIfAbsent("BioBuzz Mecanum", mecanumTuner());
        registerIfAbsent("BioBuzz Pinpoint", pinpointTuner());
        registerIfAbsent("BioBuzz Foresight", foresightTuner());
        registerIfAbsent("BioBuzz Tests", tests());
    }

    private static void registerIfAbsent(String name, Procedure procedure) {
        for (TunerRegistrar.RegisteredProcedure registered : TunerRegistrar.getProcedures()) {
            if (registered.name.equals(name)) {
                return;
            }
        }
        TunerRegistrar.register(name, procedure);
    }

    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    public static Procedure pinpointTuner() {
        return new PinpointTuner();
    }

    public static Procedure foresightTuner() {
        return new ForesightTuner(Constants::createLocalizer, Constants::createDrivetrain);
    }

    public static Procedure tests() {
        // Localization tests work before Foresight has been tuned.
        return new Tests(Constants::createDrivetrain, Constants::createLocalizer,
                Constants.FOLLOWER_TUNED ? Constants::createAlgorithm : null);
    }
}
