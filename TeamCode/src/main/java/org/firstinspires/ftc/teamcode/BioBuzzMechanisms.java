package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;

/** Direct mechanism commands for autonomous steps; no sleeps or gamepad dependency. */
public final class BioBuzzMechanisms {
    private final DcMotor iF;
    private final DcMotor iB;
    private final CRServoImplEx mk2fL;
    private final CRServoImplEx mk2fR;
    private final CRServoImplEx mk2bL;
    private final CRServoImplEx mk2bR;

    public BioBuzzMechanisms(HardwareMap hardwareMap) {
        iF = hardwareMap.get(DcMotor.class, "iF");
        iB = hardwareMap.get(DcMotor.class, "iB");
        mk2fL = hardwareMap.get(CRServoImplEx.class, "mk2fL");
        mk2fR = hardwareMap.get(CRServoImplEx.class, "mk2fR");
        mk2bL = hardwareMap.get(CRServoImplEx.class, "mk2bL");
        mk2bR = hardwareMap.get(CRServoImplEx.class, "mk2bR");

        // Keep these directions consistent with the second TeleOp when commissioning.
        for (DcMotor motor : new DcMotor[] {iF, iB}) {
            motor.setPower(0);
            motor.setDirection(DcMotorSimple.Direction.FORWARD);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        for (CRServoImplEx servo : new CRServoImplEx[] {mk2fL, mk2fR, mk2bL, mk2bR}) {
            servo.setPower(0);
            servo.setDirection(DcMotorSimple.Direction.FORWARD);
            servo.setPwmRange(new PwmControl.PwmRange(1050, 1950));
        }
    }

    /** Signed commands in [-1, 1]; positive power collects pollen. */
    public void setIntakePowers(double front, double back) {
        iF.setPower(front);
        iB.setPower(back);
    }

    /** Signed commands in [-1, 1]; positive power transfers toward the launcher. */
    public void setTransferPowers(double front, double back) {
        mk2fL.setPower(front);
        mk2fR.setPower(front);
        mk2bL.setPower(back);
        mk2bR.setPower(back);
    }

    public void stop() {
        setIntakePowers(0, 0);
        setTransferPowers(0, 0);
    }
}
