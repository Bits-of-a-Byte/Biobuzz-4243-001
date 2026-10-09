# BioBuzz Pedro Pathing setup

Pedro Pathing is installed for future autonomous routines using your mecanum drivetrain and goBILDA Pinpoint. The existing second TeleOp keeps its driving, intake, and transfer controls.

## What was added

- `com.pedropathing:revhub:3.0.1` and `com.pedropathing:tuning:1.0.2` in `TeamCode/build.gradle`, using the Dairy Maven repository. Pedro matches the [official Quickstart](https://github.com/Pedro-Pathing/Quickstart/blob/master/build.dependencies.gradle); AutoTune uses its latest published framework release at setup time.
- [BioBuzzDriveConfig.java](src/main/java/org/firstinspires/ftc/teamcode/BioBuzzDriveConfig.java): shared drivetrain names, directions, Pinpoint pod type, pod directions, and offsets for the second TeleOp and Pedro.
- [pedro/Constants.java](src/main/java/org/firstinspires/ftc/teamcode/pedro/Constants.java): Pedro drivetrain, localization, and Foresight configuration; `create(hardwareMap)` builds a follower after tuning.
- [pedro/Tuning.java](src/main/java/org/firstinspires/ftc/teamcode/pedro/Tuning.java): the Mecanum, Pinpoint, Foresight, and Tests AutoTune procedures.
- [pedro/procedures](src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/UPSTREAM.md): the four official Quickstart tuning procedures, preserved with their upstream revision and license.
- [BioBuzzPedroAutoTemplate.java](src/main/java/org/firstinspires/ftc/teamcode/BioBuzzPedroAutoTemplate.java): a disabled example that drives a 24-inch line, updates without blocking, checks completion and timeout, and stops mechanisms.
- [BioBuzzMechanisms.java](src/main/java/org/firstinspires/ftc/teamcode/BioBuzzMechanisms.java): reusable `setIntakePowers(front, back)`, `setTransferPowers(front, back)`, and `stop()` methods for future autos.

Pedro 3 uses `MecanumConfig`, `PinpointConfig`, `ForesightConfig`, `PoseFactory`, and `Paths`. Tutorials using `FConstants`, `LConstants`, or `FollowerBuilder` describe earlier APIs. Start with the [current Pedro documentation](https://pedropathing.com/docs/pathing).

## Hardware configuration

| Names | Devices |
| --- | --- |
| `fL`, `fR`, `bL`, `bR` | Four drivetrain motors |
| `iF`, `iB` | Front and back intake motors |
| `mk2fL`, `mk2fR`, `mk2bL`, `mk2bR` | Four continuous-rotation transfer servos |
| `pinpoint` | goBILDA Pinpoint Odometry Computer on a hub I2C port |

The Axon servos must already be programmed for continuous rotation and configured as CR servos. The mechanism helper uses their existing 1050–1950 microsecond PWM range.

Mount the Pinpoint with its label/connector side up. Connect the forward-tracking pod to its X port and the sideways-tracking pod to Y. On the Control Hub, use I2C port 1, 2, or 3; its built-in IMU uses port 0. Both pods need reliable contact with the floor. See [Pedro's Pinpoint setup](https://pedropathing.com/docs/pathing/tuning/localization/pinpoint).

Shared pod offsets are **millimeters**, measured from the robot's center of rotation: X offset is the forward pod's lateral displacement (positive left), and Y offset is the sideways pod's fore/aft displacement (positive forward). Initial zero offsets are placeholders. Replace them with measurements or AutoTune results before relying on position tracking.

Default left drivetrain motor directions are reversed and right directions are forward. Verify them physically, especially with your bevel gears. Your 435 RPM motor rating does not determine actual wheel speed, braking, or tuning coefficients; gearing, wheels, robot load, battery, and traction also matter.

## Deploy and tune

1. Sync Gradle in Android Studio. Build and install the **TeamCode** app on the Control Hub or Robot Controller using Android Studio's Run action.
2. Connect your computer to the Robot Controller's network. Open **http://192.168.43.1:10158** in a browser. This is Pedro 3's AutoTune interface; a separate Panels or FTC Dashboard installation is not required.
3. Keep the robot still during Pinpoint IMU calibration. Start tuning with the mechanism area clear and intakes/transfers stopped.
4. Run **BioBuzz Mecanum**. Enter your motor names and check each wheel's indicated direction. Lift the drivetrain for individual wheel checks. Apply the resulting directions to `BioBuzzDriveConfig`; rebuild and install before continuing. See [Mecanum tuning](https://pedropathing.com/docs/pathing/tuning/drivetrain/mecanum).
5. Run **BioBuzz Pinpoint**. Enter `pinpoint`, select **Four Bar**, and follow the forward push, left push, and 180-degree counterclockwise rotation instructions. Save the Java output; use its directions and offsets in the shared configuration. See [Pinpoint tuning](https://pedropathing.com/docs/pathing/tuning/localization/pinpoint).
6. Keep units consistent when applying Pinpoint output: Pedro's generated offsets default to inches, while the shared offsets are millimeters. Convert inches to millimeters by multiplying by 25.4. Rebuild and install after updating the values.
7. Use **BioBuzz Tests** to verify driving and localization. A forward push should increase X and a left push should increase Y when heading is zero. Check traveled distance and turning behavior before tuning the follower.
8. Run **BioBuzz Foresight** in clear space on the floor. It drives and spins the robot to measure achievable speeds, braking, and feedback gains. Follow each procedure's distance instructions. See [Foresight tuning](https://pedropathing.com/docs/pathing/tuning/foresight).
9. Save its generated Java configuration and replace the placeholder `foresightConfig` in `pedro/Constants.java`. Only after applying actual robot results, set `FOLLOWER_TUNED = true`. Rebuild and install again.
10. Run the full **BioBuzz Tests**, including hold, line, curved, and interpolation tests, to check path following. See [Pedro's tests](https://pedropathing.com/docs/pathing/tuning/test).

AutoTune results must be saved into your source code and redeployed. Completing a browser procedure alone does not update the configurations used by the next app build. Revisit tuning after significant drivetrain, pod, gearing, or robot-weight changes.

Registration note: `Tuning.java` registers the four procedures through the FTC SDK's `@OpModeRegistrar` and `TunerRegistrar.register()`. This avoids the AutoTune 1.0.2 `TunerScanner` null return on its first `putIfAbsent()` insertion. The factory methods have no `@Tuner` annotations. Recheck this workaround when upgrading AutoTune.

`FOLLOWER_TUNED` starts false because this project cannot measure your physical robot. Follower creation is guarded until tuning is applied. While false, Tests has no Foresight algorithm: driving and localization checks remain available, but path and hold tests require the tuned configuration. Mecanum, Pinpoint, and Foresight procedures can still be used to obtain those results.

## Use the autonomous template

The template is **disabled** until you have tuned and checked the robot. It is a commissioning example, not a BIOBUZZ scoring routine, and does not control a future launcher.

After tuning:

1. Review and replace its starting and ending poses for the actual robot placement and desired route. The supplied start is `(0, 0, 0)` and the endpoint is 24 inches along positive X, with unchanged heading.
2. Choose enough clear floor space for the robot's entire footprint and stopping distance.
3. Remove `@Disabled`, build, install, and select the template's Autonomous entry on the Driver Station.
4. Place the robot at the specified starting pose, keep it still during INIT, then start the routine. Observe telemetry and use STOP to end the test when needed.

Pedro poses and path distances use **inches** here, and pose headings use **radians**. The Pinpoint's millimeter offset settings do not change that. Heading zero faces positive X; positive Y is to the left of that direction. Setting a pose declares where the robot already is; it does not move the robot there. Use [Pedro's coordinate reference](https://pedropathing.com/docs/pathing/reference/coordinates) when designing field routes.

A future auto should create its follower during INIT, set the real starting pose, begin paths after START, and call `follower.update()` on every loop. Use `isBusy()` with a timeout to sequence actions without blocking. Add mechanism commands with `BioBuzzMechanisms`, and stop the drivetrain, intakes, and transfers on completion, STOP, or a fault.

For paths, use `PoseFactory` and `Paths.line`, `Paths.curve`, or `Paths.path`; see [creating paths](https://pedropathing.com/docs/pathing/guide/path-creation) and [autonomous usage](https://pedropathing.com/docs/pathing/guide/auto-usage). The second TeleOp's field-forward reset is a driver reference; future autonomous routines must explicitly establish their own starting field pose.
