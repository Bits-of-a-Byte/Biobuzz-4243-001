package org.firstinspires.ftc.teamcode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransferServoControlTest {
    private static final double EPSILON = 1e-9;

    private TransferServoControl createControl() {
        return new TransferServoControl(1.0, 0.25, 0.5, 0.20);
    }

    private void assertPowers(TransferServoControl control, double front, double back) {
        assertEquals(front, control.getFrontPower(), EPSILON);
        assertEquals(back, control.getBackPower(), EPSILON);
    }

    @Test
    public void startsStoppedAndIgnoresButtonsHeldThroughInit() {
        TransferServoControl control = createControl();
        control.syncButtons(true, false, true, true);
        control.update(0, true, false, true, true);
        assertPowers(control, 0, 0);
        control.update(0.1, false, false, false, false);
        control.update(0.2, true, false, false, false);
        assertPowers(control, 1, 1);
    }

    @Test
    public void fullSpeedTogglesOncePerPress() {
        TransferServoControl control = createControl();
        control.update(0, true, false, false, false);
        control.update(0.1, true, false, false, false);
        assertPowers(control, 1, 1);
        control.update(0.2, false, false, false, false);
        control.update(0.3, true, false, false, false);
        assertPowers(control, 0, 0);
    }

    @Test
    public void slowSpeedTogglesOncePerPress() {
        TransferServoControl control = createControl();
        control.update(0, false, true, false, false);
        control.update(0.1, false, true, false, false);
        assertPowers(control, 0.25, 0.25);
        control.update(0.2, false, false, false, false);
        control.update(0.3, false, true, false, false);
        assertPowers(control, 0, 0);
    }

    @Test
    public void selectingAnotherSpeedSwitchesWithoutAnOffStep() {
        TransferServoControl control = createControl();
        control.update(0, true, false, false, false);
        control.update(0.1, false, true, false, false);
        assertPowers(control, 0.25, 0.25);
        control.update(0.2, true, false, false, false);
        assertPowers(control, 1, 1);
    }

    @Test
    public void conflictingSpeedButtonsLeaveTheSelectedModeUnchanged() {
        TransferServoControl control = createControl();
        control.update(0, false, true, false, false);
        control.update(0.1, false, false, false, false);
        control.update(0.2, true, true, false, false);
        assertEquals(TransferServoControl.Mode.SLOW, control.getMode());
        assertPowers(control, 0.25, 0.25);
    }

    @Test
    public void backReverseWorksWhileOffAndExpiresEvenIfHeld() {
        TransferServoControl control = createControl();
        control.update(1, false, false, true, false);
        assertPowers(control, 0, -0.5);
        control.update(1.19, false, false, true, false);
        assertPowers(control, 0, -0.5);
        control.update(1.20, false, false, true, false);
        assertPowers(control, 0, 0);
    }

    @Test
    public void frontReverseRestoresSlowModeAndLeavesBackRunning() {
        TransferServoControl control = createControl();
        control.update(0, false, true, false, false);
        control.update(1, false, false, false, true);
        assertPowers(control, -0.5, 0.25);
        control.update(1.20, false, false, false, true);
        assertPowers(control, 0.25, 0.25);
    }

    @Test
    public void bothPairsCanReverseTogetherThenResumeFullSpeed() {
        TransferServoControl control = createControl();
        control.update(0, true, false, false, false);
        control.update(1, false, false, true, true);
        assertPowers(control, -0.5, -0.5);
        control.update(1.21, false, false, false, false);
        assertPowers(control, 1, 1);
    }

    @Test
    public void pulseResumesTheNewModeWhenSpeedChangesDuringReverse() {
        TransferServoControl control = createControl();
        control.update(0, true, false, false, false);
        control.update(1, false, false, false, true);
        control.update(1.05, false, true, false, false);
        assertPowers(control, -0.5, 0.25);
        control.update(1.21, false, false, false, false);
        assertPowers(control, 0.25, 0.25);
    }

    @Test
    public void releaseAndRepressStartsANewBurstWithoutAffectingTheOtherPair() {
        TransferServoControl control = createControl();
        control.update(1, false, false, true, false);
        control.update(1.05, false, false, false, false);
        control.update(1.10, false, false, true, false);
        control.update(1.25, false, false, false, false);
        assertPowers(control, 0, -0.5);
        control.update(1.31, false, false, false, false);
        assertPowers(control, 0, 0);
    }
}
