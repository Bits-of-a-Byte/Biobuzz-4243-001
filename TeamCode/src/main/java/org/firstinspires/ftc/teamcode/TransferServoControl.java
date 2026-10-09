package org.firstinspires.ftc.teamcode;

/** Nonblocking transfer speed toggles and independent front/back reverse pulses. */
final class TransferServoControl {
    enum Mode { OFF, FULL, SLOW }

    private final double fullPower;
    private final double slowPower;
    private final double reversePower;
    private final double burstSeconds;
    private Mode mode = Mode.OFF;
    private boolean previousUp;
    private boolean previousDown;
    private boolean previousLeft;
    private boolean previousRight;
    private double frontReverseUntil;
    private double backReverseUntil;
    private double frontPower;
    private double backPower;

    TransferServoControl(double fullPower, double slowPower,
                         double reversePower, double burstSeconds) {
        this.fullPower = fullPower;
        this.slowPower = slowPower;
        this.reversePower = reversePower;
        this.burstSeconds = burstSeconds;
    }

    // Ignore buttons held through INIT until they have been released and pressed again.
    void syncButtons(boolean up, boolean down, boolean left, boolean right) {
        previousUp = up;
        previousDown = down;
        previousLeft = left;
        previousRight = right;
    }

    void update(double nowSeconds, boolean up, boolean down, boolean left, boolean right) {
        // Ignore conflicting speed selections; neither has priority.
        if (up && !down && !previousUp) {
            mode = mode == Mode.FULL ? Mode.OFF : Mode.FULL;
        } else if (down && !up && !previousDown) {
            mode = mode == Mode.SLOW ? Mode.OFF : Mode.SLOW;
        }
        if (left && !previousLeft) {
            backReverseUntil = nowSeconds + burstSeconds;
        }
        if (right && !previousRight) {
            frontReverseUntil = nowSeconds + burstSeconds;
        }
        syncButtons(up, down, left, right);

        double forwardPower = mode == Mode.FULL ? fullPower : (mode == Mode.SLOW ? slowPower : 0);
        frontPower = nowSeconds < frontReverseUntil ? -reversePower : forwardPower;
        backPower = nowSeconds < backReverseUntil ? -reversePower : forwardPower;
    }

    Mode getMode() {
        return mode;
    }

    double getFrontPower() {
        return frontPower;
    }

    double getBackPower() {
        return backPower;
    }
}
