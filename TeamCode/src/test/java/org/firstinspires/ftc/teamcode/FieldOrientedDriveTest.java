package org.firstinspires.ftc.teamcode;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldOrientedDriveTest {
    private static final double EPSILON = 1e-9;

    @Test
    public void fieldForwardAtZeroHeadingDrivesAllWheelsForward() {
        assertArrayEquals(new double[] {1, 1, 1, 1},
                FieldOrientedDrive.calculate(1, 0, 0, 0, 1), EPSILON);
    }

    @Test
    public void fieldForwardWhenFacingLeftStrafesRobotRight() {
        assertArrayEquals(new double[] {1, -1, -1, 1},
                FieldOrientedDrive.calculate(1, 0, 0, Math.PI / 2, 1), EPSILON);
    }

    @Test
    public void fieldForwardWhenFacingRightStrafesRobotLeft() {
        assertArrayEquals(new double[] {-1, 1, 1, -1},
                FieldOrientedDrive.calculate(1, 0, 0, -Math.PI / 2, 1), EPSILON);
    }

    @Test
    public void fieldForwardWhenFacingBackwardDrivesRobotBackward() {
        assertArrayEquals(new double[] {-1, -1, -1, -1},
                FieldOrientedDrive.calculate(1, 0, 0, Math.PI, 1), EPSILON);
    }

    @Test
    public void wheelCommandsPreserveRequestedFieldDirectionAtArbitraryHeadings() {
        double fieldForward = 0.25;
        double fieldRight = -0.30;
        double[] headings = {0.43, 1.21, -2.40};

        for (double heading : headings) {
            double[] powers = FieldOrientedDrive.calculate(
                    fieldForward, fieldRight, 0, heading, 1);
            // Recover the robot's translation from the four wheel commands.
            double robotForward = (powers[0] + powers[1] + powers[2] + powers[3]) / 4;
            double robotRight = (powers[0] - powers[1] - powers[2] + powers[3]) / 4;
            double recoveredForward = robotForward * Math.cos(heading)
                    + robotRight * Math.sin(heading);
            double recoveredRight = -robotForward * Math.sin(heading)
                    + robotRight * Math.cos(heading);

            assertEquals(fieldForward, recoveredForward, EPSILON);
            assertEquals(fieldRight, recoveredRight, EPSILON);
        }
    }

    @Test
    public void clockwiseTurnKeepsItsDirectionRegardlessOfHeading() {
        for (double heading : new double[] {0, Math.PI / 2, -Math.PI / 2, Math.PI}) {
            assertArrayEquals(new double[] {1, -1, 1, -1},
                    FieldOrientedDrive.calculate(0, 0, 1, heading, 1), EPSILON);
        }
    }

    @Test
    public void saturatedTranslationAndTurningRespectTheSpeedLimit() {
        double speed = 0.35;
        for (double heading : new double[] {0, 0.73, Math.PI / 2, -2.20}) {
            for (double turn : new double[] {-1, 1}) {
                double[] powers = FieldOrientedDrive.calculate(1, 1, turn, heading, speed);
                double largestPower = 0;
                for (double power : powers) {
                    assertTrue(Double.isFinite(power));
                    assertTrue(Math.abs(power) <= speed + EPSILON);
                    largestPower = Math.max(largestPower, Math.abs(power));
                }
                assertEquals(speed, largestPower, EPSILON);
            }
        }
    }

    @Test
    public void slowModeScalesEveryWheelEqually() {
        double[] full = FieldOrientedDrive.calculate(0.8, -0.6, 0.4, 0.7, 1);
        double[] slow = FieldOrientedDrive.calculate(0.8, -0.6, 0.4, 0.7, 0.35);
        for (int wheel = 0; wheel < full.length; wheel++) {
            assertEquals(full[wheel] * 0.35, slow[wheel], EPSILON);
        }
    }

    @Test
    public void headingWrapDoesNotCauseAJumpInWheelPower() {
        double delta = 1e-10;
        assertArrayEquals(
                FieldOrientedDrive.calculate(0.3, -0.2, 0.1, Math.PI - delta, 1),
                FieldOrientedDrive.calculate(0.3, -0.2, 0.1, -Math.PI + delta, 1),
                EPSILON);
        assertArrayEquals(
                FieldOrientedDrive.calculate(0.3, -0.2, 0.1, 0.47, 1),
                FieldOrientedDrive.calculate(0.3, -0.2, 0.1, 0.47 + 2 * Math.PI, 1),
                EPSILON);
    }
}
