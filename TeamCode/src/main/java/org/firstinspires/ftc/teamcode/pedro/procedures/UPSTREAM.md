# Upstream AutoTune procedures

These four Java files are imported without modification from the official [Pedro Pathing Quickstart](https://github.com/Pedro-Pathing/Quickstart/tree/2df96463753dba59271df3f71c1619fb5f9da65a/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures).

Pinned revision: `2df96463753dba59271df3f71c1619fb5f9da65a` (2026-09-30).

- `MecanumTuner.java`
- `PinpointTuner.java`
- `ForesightTuner.java`
- `Tests.java`

Their package remains `org.firstinspires.ftc.teamcode.pedro.procedures`. The published `com.pedropathing:tuning:1.0.2` dependency supplies the AutoTune framework; the Quickstart supplies these robot-specific procedures.

The project registers these procedures explicitly through the FTC SDK's `@OpModeRegistrar` and `TunerRegistrar.register()`, avoiding AutoTune 1.0.2's `TunerScanner` first-insertion `putIfAbsent()` null-return bug. The imported procedures remain unchanged; the registration workaround is in `pedro/Tuning.java`.

The upstream repository license is reproduced unchanged in [UPSTREAM_LICENSE.txt](UPSTREAM_LICENSE.txt), from [the pinned LICENSE](https://github.com/Pedro-Pathing/Quickstart/blob/2df96463753dba59271df3f71c1619fb5f9da65a/LICENSE). Preserve the source and license notices when redistributing.
