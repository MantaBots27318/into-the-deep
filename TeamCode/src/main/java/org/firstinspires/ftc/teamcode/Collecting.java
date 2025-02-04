package org.firstinspires.ftc.teamcode;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

/* FTC Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.configurations.Configuration;

/* Intake includes */
import org.firstinspires.ftc.teamcode.intake.IntakeSlides;
import org.firstinspires.ftc.teamcode.intake.IntakeArm;
import org.firstinspires.ftc.teamcode.intake.IntakeElbow;
import org.firstinspires.ftc.teamcode.intake.IntakeWrist;
import org.firstinspires.ftc.teamcode.intake.IntakeClaw;

/* Outtake includes */
import org.firstinspires.ftc.teamcode.outtake.OuttakeSlides;
import org.firstinspires.ftc.teamcode.outtake.OuttakeElbow;
import org.firstinspires.ftc.teamcode.outtake.OuttakeWrist;
import org.firstinspires.ftc.teamcode.outtake.OuttakeClaw;

import java.util.Map;


public class Collecting {
    public enum OuttakeClawMode {
        NONE,
        WAITING,
        IS_RELEASING,
        IS_MOVING_ARM
    }

    public enum IntakeClawMode {
        NONE,
        WAITING,
        IS_CLOSING,
        IS_LIFTING_ARM
    }

    public enum TransitionMode {
        NONE,
        WAITING,
        IS_EXTENDING_INTAKE_SLIDES,
        IS_MOVING_ARMS,
        IS_MOVING_OUTTAKE_SLIDES_AND_MICRORELEASING_CLAW,
        IS_REGRABBING,
        IS_MOVING_INTAKE_SLIDES,
        IS_GRABBING,
        IS_RELEASING,
        IS_MOVING_OUTTAKE_ARM
    }

    Telemetry mLogger;

    TransitionMode mTransitionMode;
    IntakeClawMode mIntakeClawMode;
    OuttakeClawMode mOuttakeClawMode;
    IntakeSlides mIntakeSlides;
    IntakeArm mIntakeArm;
    IntakeElbow mIntakeElbow;
    IntakeWrist mIntakeWrist;
    IntakeClaw mIntakeClaw;
    OuttakeSlides mOuttakeSlides;
    OuttakeElbow mOuttakeElbow;
    OuttakeWrist mOuttakeWrist;
    OuttakeClaw mOuttakeClaw;

    Gamepad mGamepad;
    boolean mWasXPressed;
    boolean mWasAPressed;
    boolean mWasYPressed;
    boolean mWasBPressed;
    boolean mWasDPadUpPressed;
    boolean mWasDPadDownPressed;
    boolean mWasDPadLeftPressed;
    boolean mWasDPadRightPressed;
    boolean mWasLeftStickXPositivePressed;
    boolean mWasLeftStickXNegativePressed;
    boolean mWasRightStickXPositivePressed;
    boolean mWasRightStickXNegativePressed;
    boolean mWasRightBumperPressed;
    boolean mWasLeftBumperPressed;
    boolean mWasRightStickButtonPressed;
    boolean mWasLeftStickButtonPressed;


    public Collecting() {

        mIntakeSlides = new IntakeSlides();
        mIntakeArm = new IntakeArm();
        mIntakeElbow = new IntakeElbow();
        mIntakeWrist = new IntakeWrist();
        mIntakeClaw = new IntakeClaw();

        mOuttakeSlides = new OuttakeSlides();
        mOuttakeElbow = new OuttakeElbow();
        mOuttakeWrist = new OuttakeWrist();
        mOuttakeClaw = new OuttakeClaw();

        mWasXPressed = false;
        mWasAPressed = false;
        mWasYPressed = false;
        mWasBPressed = false;

        mWasDPadDownPressed = false;
        mWasDPadUpPressed = false;
        mWasDPadLeftPressed = false;
        mWasDPadRightPressed = false;

        mWasLeftStickXPositivePressed = false;
        mWasLeftStickXNegativePressed = false;
        mWasRightStickXPositivePressed = false;
        mWasRightStickXNegativePressed = false;

        mWasRightBumperPressed = false;
        mWasLeftBumperPressed = false;
        mWasRightStickButtonPressed = false;
        mWasLeftStickButtonPressed = false;

        mTransitionMode = TransitionMode.NONE;
        mIntakeClawMode = IntakeClawMode.NONE;
        mOuttakeClawMode = OuttakeClawMode.NONE;
    }

    public void setHW(Configuration config, HardwareMap hwm, Telemetry logger, Gamepad gamepad) {

        mLogger = logger;
        mLogger.addLine("======= COLLECTING =======");

        mIntakeSlides.setHW(config, hwm, mLogger);
        mIntakeArm.setHW(config, hwm, mLogger);
        mIntakeElbow.setHW(config, hwm, mLogger);
        mIntakeWrist.setHW(config, hwm, mLogger);
        mIntakeClaw.setHW(config, hwm, mLogger);

        mOuttakeSlides.setHW(config, hwm, mLogger);
        mOuttakeElbow.setHW(config, hwm, mLogger);
        mOuttakeWrist.setHW(config, hwm, mLogger);
        mOuttakeClaw.setHW(config, hwm, mLogger);

        mGamepad = gamepad;
    }

    public void control() {

        mLogger.addLine("======= COLLECTING =======");
        mLogger.addLine("-------- FUNCTION --------");

        if (mGamepad.left_bumper && (mIntakeSlides.isRetracted())) {
            mLogger.addLine("==> EXT OUT SLD");
            mOuttakeSlides.extend(0.9);
        } else if (mGamepad.right_bumper) {
            mLogger.addLine("==> RLB OUT SLD");
            mOuttakeSlides.rollback(0.7);
        } else {
            mOuttakeSlides.stop();
        }

        if (mGamepad.right_stick_button) {
            mLogger.addLine("==> OUT SLD TO TRANSFER");
            if (!mWasRightStickButtonPressed) {
                mOuttakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5);
            }
            mWasRightStickButtonPressed = true;
        } else {
            mWasRightStickButtonPressed = false;
        }

        if ((mGamepad.left_trigger > 0) && (mOuttakeSlides.isRetracted())) {
            mLogger.addLine("==> EXT IN SLD");
            mIntakeSlides.extend(mGamepad.left_trigger * 0.9);
        } else if (mGamepad.right_trigger > 0) {
            mLogger.addLine("==> RLB IN SLD");
            mIntakeSlides.rollback(mGamepad.right_trigger * 0.7);
        } else {
            mIntakeSlides.stop();
        }

        if (mGamepad.x) {
            mLogger.addLine(String.format("==> SWT OUT CLW : " + mOuttakeClaw.getPosition()));
            if (!mWasXPressed) {
                if (mOuttakeClaw.getPosition() == OuttakeClaw.Position.OPEN) {
                    mOuttakeClaw.setPosition(OuttakeClaw.Position.CLOSED);
                }
                if (mOuttakeClaw.getPosition() == OuttakeClaw.Position.CLOSED) {
                    this.release();
                }
            }
            mWasXPressed = true;
        } else {
            mWasXPressed = false;
        }

        if (mGamepad.y) {
            mLogger.addLine(String.format("==> MDW OUT ARM : " + mOuttakeElbow.getPosition()));
            if (!mWasYPressed) {
                mOuttakeElbow.moveDown();
            }
            mWasYPressed = true;
        } else {
            mWasYPressed = false;
        }

        if (mGamepad.a) {
            mLogger.addLine(String.format("==> MUP OUT ARM : " + mOuttakeElbow.getPosition()));
            if (!mWasAPressed) {
                mOuttakeElbow.moveUp();
            }
            mWasAPressed = true;
        } else {
            mWasAPressed = false;
        }

        if (mGamepad.b) {
            mLogger.addLine("==> TRANSITION");
            if (!mWasBPressed) {
                this.transition();
            }
            mWasBPressed = true;
        } else {
            mWasBPressed = false;
        }

        if (mGamepad.dpad_left) {
            mLogger.addLine(String.format("==> SWT IN CLW : " + mIntakeClaw.getPosition()));
            if (!mWasDPadLeftPressed) {
                if (mIntakeClaw.getPosition() == IntakeClaw.Position.OPEN) {
                    this.grab();
                }
                if (mIntakeClaw.getPosition() == IntakeClaw.Position.CLOSED) {
                    mIntakeClaw.setPosition(IntakeClaw.Position.OPEN);
                }
            }
            mWasDPadLeftPressed = true;
        } else {
            mWasDPadLeftPressed = false;
        }

        if (mGamepad.dpad_up) {
            mLogger.addLine(String.format("==> MDW IN ARM : " + mIntakeArm.getPosition()));
            if (!mWasDPadUpPressed) {
                mIntakeElbow.moveDown();
                mIntakeArm.moveDown();
            }
            mWasDPadUpPressed = true;
        } else {
            mWasDPadUpPressed = false;
        }

        if (mGamepad.dpad_down) {
            mLogger.addLine(String.format("==> MUP IN ARM : " + mIntakeArm.getPosition()));
            if (!mWasDPadDownPressed) {
                mIntakeArm.moveUp();
                mIntakeElbow.moveUp();
            }
            mWasDPadDownPressed = true;
        } else {
            mWasDPadDownPressed = false;
        }

        if (mGamepad.left_stick_x < 0) {
            mLogger.addLine(String.format("==> RDW IN WRS : " + mIntakeWrist.getPosition()));
            if (!mWasLeftStickXNegativePressed) {
                mIntakeWrist.rotateDown();
            }
            mWasLeftStickXNegativePressed = true;
        } else {
            mWasLeftStickXNegativePressed = false;
        }

        if (mGamepad.left_stick_x > 0) {
            mLogger.addLine(String.format("==> RUP IN WRS : " + mIntakeWrist.getPosition()));
            if (!mWasLeftStickXPositivePressed) {
                mIntakeWrist.rotateUp();
            }
            mWasLeftStickXPositivePressed = true;
        } else {
            mWasLeftStickXPositivePressed = false;
        }


        mLogger.addLine("\n-------- POSITION --------");
        mLogger.addLine(mIntakeSlides.logPositions());
        mLogger.addLine(mOuttakeSlides.logPositions());

        mLogger.addLine("\n--------- MOVING ---------");
        mLogger.addLine(this.logMovements());
    }

    public void loop() {
        if (mTransitionMode != TransitionMode.NONE) {
            this.transition();
        }
        if (mIntakeClawMode != IntakeClawMode.NONE) {
            this.grab();
        }
        if (mOuttakeClawMode != OuttakeClawMode.NONE) {
            this.release();
        }
    }


    public void transition() {

        mLogger.addLine("TRANSITIONING : " + mTransitionMode);

        if (mTransitionMode == TransitionMode.NONE) {
            // Just transition to make sure that even though the first robot part is not yet ready
            // to move, we won't forget we have to keep on transiting
            mTransitionMode = TransitionMode.WAITING;
        }
        if (mTransitionMode == TransitionMode.WAITING) {
            mIntakeSlides.setPosition(IntakeSlides.Position.EXTEND, 20);

            if (mIntakeSlides.getPosition() == IntakeSlides.Position.EXTEND) {
                mTransitionMode = TransitionMode.IS_EXTENDING_INTAKE_SLIDES;
            }
        }
        if (mTransitionMode == TransitionMode.IS_EXTENDING_INTAKE_SLIDES && !mIntakeSlides.isMoving()) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.OPEN);
            mOuttakeWrist.setPosition(OuttakeWrist.Position.NULL);
            mOuttakeElbow.setPosition(OuttakeElbow.Position.TRANSFER);
            mIntakeWrist.setPosition(IntakeWrist.Position.NULL);
            mIntakeElbow.setPosition(IntakeElbow.Position.TRANSFER);
            mIntakeArm.setPosition(IntakeArm.Position.TRANSFER);

            if ((mOuttakeClaw.getPosition() == OuttakeClaw.Position.OPEN) &&
                    (mOuttakeWrist.getPosition() == OuttakeWrist.Position.NULL) &&
                    (mOuttakeElbow.getPosition() == OuttakeElbow.Position.TRANSFER) &&
                    (mIntakeWrist.getPosition() == IntakeWrist.Position.NULL) &&
                    (mIntakeElbow.getPosition() == IntakeElbow.Position.TRANSFER) &&
                    (mIntakeArm.getPosition() == IntakeArm.Position.TRANSFER)) {
                mTransitionMode = TransitionMode.IS_MOVING_ARMS;
            }
        } else if (mTransitionMode == TransitionMode.IS_MOVING_ARMS && !mIntakeArm.isMoving() && !mIntakeWrist.isMoving() && !mIntakeElbow.isMoving() && !mOuttakeClaw.isMoving() && !mOuttakeWrist.isMoving() && !mOuttakeElbow.isMoving()) {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5);
            mIntakeClaw.setPosition(IntakeClaw.Position.MICRORELEASED, 200);
            if ((mOuttakeSlides.getPosition() == OuttakeSlides.Position.TRANSFER) &&
                    (mIntakeClaw.getPosition() == IntakeClaw.Position.MICRORELEASED)) {
                mTransitionMode = TransitionMode.IS_MOVING_OUTTAKE_SLIDES_AND_MICRORELEASING_CLAW;
            }

        } else if (mTransitionMode == TransitionMode.IS_MOVING_OUTTAKE_SLIDES_AND_MICRORELEASING_CLAW && !mIntakeClaw.isMoving() && !mOuttakeSlides.isMoving()) {
            mIntakeClaw.setPosition(IntakeClaw.Position.CLOSED, 200);

            if (mIntakeClaw.getPosition() == IntakeClaw.Position.CLOSED) {
                mTransitionMode = TransitionMode.IS_REGRABBING;
            }
        } else if (mTransitionMode == TransitionMode.IS_REGRABBING && !mIntakeClaw.isMoving()) {
            mIntakeSlides.setPosition(IntakeSlides.Position.TRANSFER, 5);

            if (mIntakeSlides.getPosition() == IntakeSlides.Position.TRANSFER) {
                mTransitionMode = TransitionMode.IS_MOVING_INTAKE_SLIDES;
            }
        } else if (mTransitionMode == TransitionMode.IS_MOVING_INTAKE_SLIDES && !mIntakeSlides.isMoving()) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.CLOSED, 500);

            if (mOuttakeClaw.getPosition() == OuttakeClaw.Position.CLOSED) {
                mTransitionMode = TransitionMode.IS_GRABBING;
            }
        } else if (mTransitionMode == TransitionMode.IS_GRABBING && !mOuttakeClaw.isMoving()) {
            mIntakeClaw.setPosition(IntakeClaw.Position.OPEN);

            if (mIntakeClaw.getPosition() == IntakeClaw.Position.OPEN) {
                mTransitionMode = TransitionMode.IS_RELEASING;
            }
        } else if (mTransitionMode == TransitionMode.IS_RELEASING && !mIntakeClaw.isMoving()) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.DROP);

            if (mOuttakeElbow.getPosition() == OuttakeElbow.Position.DROP) {
                mTransitionMode = TransitionMode.IS_MOVING_OUTTAKE_ARM;
            }
        } else if (mTransitionMode == TransitionMode.IS_MOVING_OUTTAKE_ARM && !mOuttakeElbow.isMoving()) {
            mTransitionMode = TransitionMode.NONE;
            mIntakeSlides.extend(0.05);
            mOuttakeSlides.extend(0.05);
        }
    }


    public void grab() {
        mLogger.addLine("CLOSING CLAW : " + mIntakeClawMode);
        if (mIntakeClawMode == IntakeClawMode.NONE) {
            mIntakeClawMode = IntakeClawMode.WAITING;
        }
        if (mIntakeClawMode == IntakeClawMode.WAITING) {
            mIntakeClaw.setPosition(IntakeClaw.Position.CLOSED, 100);

            if (mIntakeClaw.getPosition() == IntakeClaw.Position.CLOSED) {
                mIntakeClawMode = IntakeClawMode.IS_CLOSING;
            }
        }
        if (mIntakeClawMode == IntakeClawMode.IS_CLOSING && !mIntakeClaw.isMoving()) {

            mIntakeArm.setPosition(IntakeArm.Position.OVER_SUBMERSIBLE, 200);
            mIntakeElbow.setPosition(IntakeElbow.Position.OVER_SUBMERSIBLE);

            if ((mIntakeArm.getPosition() == IntakeArm.Position.OVER_SUBMERSIBLE) &&
                    (mIntakeElbow.getPosition() == IntakeElbow.Position.OVER_SUBMERSIBLE)) {
                mIntakeClawMode = IntakeClawMode.IS_LIFTING_ARM;
            }
        } else if (mIntakeClawMode == IntakeClawMode.IS_LIFTING_ARM && !mIntakeArm.isMoving() && !mIntakeElbow.isMoving()) {
            mIntakeClawMode = IntakeClawMode.NONE;
        }
    }

    public void release() {
        mLogger.addLine("RELEASING CLAW : " + mOuttakeClawMode);
        if (mOuttakeClawMode == OuttakeClawMode.NONE) {
            mOuttakeClawMode = OuttakeClawMode.WAITING;
        }
        if (mOuttakeClawMode == OuttakeClawMode.WAITING) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.OPEN, 100);

            if (mOuttakeClaw.getPosition() == OuttakeClaw.Position.OPEN) {
                mOuttakeClawMode = OuttakeClawMode.IS_RELEASING;
            }
        }
        if (mOuttakeClawMode == OuttakeClawMode.IS_RELEASING && !mOuttakeClaw.isMoving()) {

            mOuttakeElbow.setPosition(OuttakeElbow.Position.OFF, 200);

            if (mOuttakeElbow.getPosition() == OuttakeElbow.Position.OFF) {
                mOuttakeClawMode = OuttakeClawMode.IS_MOVING_ARM;
            }
        } else if (mOuttakeClawMode == OuttakeClawMode.IS_MOVING_ARM && !mOuttakeElbow.isMoving()) {
            mOuttakeClawMode = OuttakeClawMode.NONE;

        }
    }

    public String logMovements() {
        String result = "";
        if (mIntakeClaw.isMoving()) {
            result += "IN CLW\n";
        }
        if (mIntakeWrist.isMoving()) {
            result += "IN WRS\n";
        }
        if (mIntakeArm.isMoving()) {
            result += "IN ARM\n";
        }
        if (mIntakeElbow.isMoving()) {
            result += "IN ELB\n";
        }
        if (mIntakeSlides.isMoving()) {
            result += "IN SLD\n";
        }
        if (mOuttakeClaw.isMoving()) {
            result += "OUT CLW\n";
        }
        if (mOuttakeWrist.isMoving()) {
            result += "OUT WRS\n";
        }
        if (mOuttakeElbow.isMoving()) {
            result += "OUT ELB\n";
        }
        if (mOuttakeSlides.isMoving()) {
            result += "OUT SLD\n";
        }
        return result;
    }

    public void dropHighBasket() {

        while(mOuttakeSlides.getPosition() != OuttakeSlides.Position.MAX) {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.MAX, 25);
        }
        while (mOuttakeSlides.isMoving()) {
            mLogger.addLine("HGB : OUT SLD MAX");
            mLogger.addLine(mOuttakeSlides.logPositions());
            mLogger.update();
        }

        while(mOuttakeElbow.getPosition() != OuttakeElbow.Position.DROP) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.DROP);
        }
        while (mOuttakeElbow.isMoving()) {
            mLogger.addLine("HGB : OUT ELB DROP");
            mLogger.update();
        }

        while(mOuttakeClaw.getPosition() != OuttakeClaw.Position.OPEN) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.OPEN);
        }
        while (mOuttakeClaw.isMoving()) {
            mLogger.addLine("HGB : OUT CLW OPEN");
            mLogger.update();
        }

        while(mOuttakeElbow.getPosition() != OuttakeElbow.Position.OFF) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.OFF);
        }
        while (mOuttakeElbow.isMoving()) {
            mLogger.addLine("HGB : OUT ELB OFF");
            mLogger.update();
        }

        while(mOuttakeSlides.getPosition() != OuttakeSlides.Position.MIN) {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.MIN, 25);
        }
        while (mOuttakeSlides.isMoving()) {
            mLogger.addLine("HGB : OUT SLD MIN");
            mLogger.addLine(mOuttakeSlides.logPositions());
            mLogger.update();
        }
    }
    public void dropHighBasketWithoutRetracting() {

        mOuttakeSlides.setPosition(OuttakeSlides.Position.MAX, 25);
        while (mOuttakeSlides.isMoving()) {
            mLogger.addLine("HGB : OUT SLD MAX");
            mLogger.addLine(mOuttakeSlides.logPositions());
            mLogger.update();
        }

        mOuttakeElbow.setPosition(OuttakeElbow.Position.DROP);
        while (mOuttakeElbow.isMoving()) {
            mLogger.addLine("HGB : OUT ELB DROP");
            mLogger.update();
        }

        mOuttakeClaw.setPosition(OuttakeClaw.Position.OPEN);
        while (mOuttakeClaw.isMoving()) {
            mLogger.addLine("HGB : OUT CLW OPEN");
            mLogger.update();
        }

        mOuttakeSlides.setPosition(OuttakeSlides.Position.ASCEND, 25);
        while (mOuttakeSlides.isMoving()) {
            mLogger.addLine("HGB : OUT SLD ASCEND");
            mLogger.addLine(mOuttakeSlides.logPositions());
            mLogger.update();
        }
    }


    public void catchFromGround() {

        while (mIntakeSlides.getPosition() != IntakeSlides.Position.AUTONOMOUS) {
            mIntakeSlides.setPosition(IntakeSlides.Position.AUTONOMOUS, 10);
        }
        while (mIntakeSlides.isMoving()) {
            mLogger.addLine("CFG : INT SLD AUTONOMOUS");
            mLogger.addLine(mIntakeSlides.logPositions());
            mLogger.update();
        }

        while (mIntakeArm.getPosition() != IntakeArm.Position.GRABBING) {
            mIntakeArm.setPosition(IntakeArm.Position.GRABBING, 1200);
        }
        while (mIntakeElbow.getPosition() != IntakeElbow.Position.GRABBING) {
            mIntakeElbow.setPosition(IntakeElbow.Position.GRABBING, 1200);
        }
        while (mIntakeArm.isMoving() || mIntakeElbow.isMoving()) {
            mLogger.addLine("CFG : IN ARM GRABBING");
            mLogger.update();
        }

        this.grab();
        while (mIntakeClawMode != IntakeClawMode.NONE){
           this.grab();
            mLogger.update();
        }

        this.transition();
        while (mTransitionMode != TransitionMode.NONE) {
            this.transition();
            mLogger.update();
        }

        while(mIntakeSlides.getPosition() != IntakeSlides.Position.TRANSFER) {
            mIntakeSlides.setPosition(IntakeSlides.Position.TRANSFER, 10);
        }
        while (mIntakeArm.getPosition() != IntakeArm.Position.TRANSFER) {
            mIntakeArm.setPosition(IntakeArm.Position.TRANSFER);
        }
        while (mIntakeElbow.getPosition() != IntakeElbow.Position.GRABBING) {
            mIntakeElbow.setPosition(IntakeElbow.Position.GRABBING);
        }

    }

    public void grabSpecimen()
    {
        while(mOuttakeClaw.getPosition() != OuttakeClaw.Position.CLOSED) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.CLOSED);
        }
        while (mOuttakeClaw.isMoving()) {
            mLogger.addLine("GSP : OUT CLW CLOSED");
            mLogger.update();
        }

        while(mOuttakeElbow.getPosition() != OuttakeElbow.Position.DROP) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.DROP);
        }
        while (mOuttakeElbow.isMoving()) {
            mLogger.addLine("HGB : OUT ELB SPECIMEN");
            mLogger.update();
        }
    }

    public void letSpecimen() {

        while(mOuttakeSlides.getPosition() != OuttakeSlides.Position.HIGH_SUBMERSIBLE_OVER) {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.HIGH_SUBMERSIBLE_OVER, 25);
        }
        while (mOuttakeSlides.isMoving()) {
            mLogger.addLine("LSP : OUT SLD HIGH SUB");
            mLogger.addLine(mOuttakeSlides.logPositions());
            mLogger.update();
        }

        while(mOuttakeElbow.getPosition() != OuttakeElbow.Position.SPECIMEN) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.SPECIMEN);
        }
        while (mOuttakeElbow.isMoving()) {
            mLogger.addLine("LSP : OUT ELB SPECIMEN");
            mLogger.update();
        }

        while(mOuttakeClaw.getPosition() != OuttakeClaw.Position.OPEN) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.OPEN);
        }
        while (mOuttakeClaw.isMoving()) {
            mLogger.addLine("LSP : OUT CLW CLOSED");
            mLogger.update();
        }

        while(mOuttakeElbow.getPosition() != OuttakeElbow.Position.DROP) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.DROP);
        }
        while(mOuttakeSlides.getPosition() != OuttakeSlides.Position.TRANSFER) {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5);
        }

    }

    public void clipSpecimen() {

        while(mOuttakeElbow.getPosition() != OuttakeElbow.Position.SPECIMEN) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.SPECIMEN);
        }
        while (mOuttakeElbow.isMoving()) {
            mLogger.addLine("HGB : OUT ELB SPECIMEN");
            mLogger.update();
        }

        while(mOuttakeSlides.getPosition() != OuttakeSlides.Position.HIGH_SUBMERSIBLE_UNDER) {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.HIGH_SUBMERSIBLE_UNDER, 25);
        }
        while (mOuttakeSlides.isMoving()) {
            mLogger.addLine("HGB : OUT SLD HIGH SUB");
            mLogger.addLine(mOuttakeSlides.logPositions());
            mLogger.update();
        }

        while(mOuttakeElbow.getPosition() != OuttakeElbow.Position.VERTICAL) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.VERTICAL);
        }
        while (mOuttakeElbow.isMoving()) {
            mLogger.addLine("HGB : OUT ELB VERTICAL");
        }


    }
    public void openClaw() {

        while(mOuttakeClaw.getPosition() != OuttakeClaw.Position.OPEN) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.OPEN);
        }
        while (mOuttakeClaw.isMoving()) {
            mLogger.addLine("HGB : OUT CLW OPEN");
            mLogger.update();
        }

        while(mOuttakeSlides.getPosition() != OuttakeSlides.Position.TRANSFER) {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5);
        }
        while(mOuttakeElbow.getPosition() != OuttakeElbow.Position.SPECIMEN) {
            mOuttakeElbow.setPosition(OuttakeElbow.Position.SPECIMEN);
        }
    }

    public void persist(Configuration config) {
        mIntakeSlides.persist(config);
        mOuttakeSlides.persist(config);
    }

}

