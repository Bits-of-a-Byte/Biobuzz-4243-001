package org.firstinspires.ftc.teamcode;

/** Converts field-relative inputs into mecanum wheel powers, independent of hardware. */
final class FieldOrientedDrive {
    private FieldOrientedDrive() {
    }

    /**
     * Heading is counterclockwise-positive, as reported by the Pinpoint.
     * Forward/right inputs are relative to the driver's field reference.
     * Returns front-left, front-right, back-left, back-right powers.
     */
    static double[] calculate(double fieldForward, double fieldRight, double clockwiseTurn,
                              double headingRadians, double speed) {
        double cos = Math.cos(headingRadians);
        double sin = Math.sin(headingRadians);
        double robotForward = fieldForward * cos - fieldRight * sin;
        double robotRight = fieldForward * sin + fieldRight * cos;

        // Normalize after rotating into the robot's frame.
        double denominator = Math.max(
                Math.abs(robotForward) + Math.abs(robotRight) + Math.abs(clockwiseTurn), 1.0);
        return new double[] {
                (robotForward + robotRight + clockwiseTurn) / denominator * speed,
                (robotForward - robotRight - clockwiseTurn) / denominator * speed,
                (robotForward - robotRight + clockwiseTurn) / denominator * speed,
                (robotForward + robotRight - clockwiseTurn) / denominator * speed
        };
    }
}
