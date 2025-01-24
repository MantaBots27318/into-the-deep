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


public class Collecting {
    public enum ClawMode{
        NONE,
        WAITING,
        IS_CLOSING,
        IS_LIFTING_ARM,
    }
    public enum TransitionMode {
        NONE,
        WAITING,
        IS_EXTENDING_INTAKE_SLIDES,
        IS_MOVING_OUTTAKE_ARM,
        IS_MOVING_OUTTAKE_SLIDES,
        IS_MOVING_INTAKE_ARM,
        IS_MOVING_INTAKE_SLIDES,
        IS_MICRO_RELEASING,
        IS_GRABBING,
        IS_RELEASING
    }

    Telemetry       mLogger;

    TransitionMode  mTransitionMode;
    ClawMode        mClawMode;
    IntakeSlides    mIntakeSlides;
    IntakeArm       mIntakeArm;
    IntakeElbow     mIntakeElbow;
    IntakeWrist     intakeWrist;
    IntakeClaw      intakeClaw;
    OuttakeSlides   mOuttakeSlides;
    OuttakeElbow    mOuttakeElbow;
    OuttakeWrist    mOuttakeWrist;
    OuttakeClaw     mOuttakeClaw;

    Gamepad         mGamepad;
    boolean         mWasXPressed;
    boolean         mWasAPressed;
    boolean         mWasYPressed;
    boolean         mWasBPressed;
    boolean         mWasDPadUpPressed;
    boolean         mWasDPadDownPressed;
    boolean         mWasDPadLeftPressed;
    boolean         mWasDPadRightPressed;
    boolean         mWasLeftStickXPositivePressed;
    boolean         mWasLeftStickXNegativePressed;
    boolean         mWasRightStickXPositivePressed;
    boolean         mWasRightStickXNegativePressed;
    boolean         mWasRightBumperPressed;
    boolean         mWasLeftBumperPressed;
    boolean         mWasRightStickButtonPressed;
    boolean         mWasLeftStickButtonPressed;


    public Collecting() {

        mIntakeSlides = new IntakeSlides();
        mIntakeArm    = new IntakeArm();
        mIntakeElbow  = new IntakeElbow();
        intakeWrist  = new IntakeWrist();
        intakeClaw   = new IntakeClaw();

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
        mClawMode = ClawMode.NONE;
    }

    public void setHW(Configuration config, HardwareMap hwm, Telemetry logger, Gamepad gamepad) {

        mLogger = logger;
        mLogger.addLine("======= COLLECTING =======");

        mIntakeSlides.setHW(config, hwm, mLogger);
        mIntakeArm.setHW(config, hwm, mLogger);
        mIntakeElbow.setHW(config, hwm, mLogger);
        intakeWrist.setHW(config, hwm, mLogger);
        intakeClaw.setHW(config, hwm, mLogger);

        mOuttakeSlides.setHW(config, hwm, mLogger);
        mOuttakeElbow.setHW(config, hwm, mLogger);
        mOuttakeWrist.setHW(config, hwm, mLogger);
        mOuttakeClaw.setHW(config, hwm, mLogger);

        mGamepad = gamepad;
    }

    public void move() {

        mLogger.addLine("======= COLLECTING =======");
        mLogger.addLine("-------- FUNCTION --------");

        if (mGamepad.left_bumper && (mIntakeSlides.isRetracted()))
        {
            mLogger.addLine("==> EXT OUT SLD");
            mOuttakeSlides.extend(0.9);
        }
        else if (mGamepad.right_bumper) {
            mLogger.addLine("==> RLB OUT SLD");
            mOuttakeSlides.rollback(0.7);
        }
        else                            {
            mOuttakeSlides.stop();
        }

        if(mGamepad.right_stick_button) {
            mLogger.addLine("==> OUT SLD TO TRANSFER");
            if(!mWasRightStickButtonPressed) { mOuttakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5 ); }
            mWasRightStickButtonPressed = true;
        }
        else { mWasRightStickButtonPressed = false; }

        if((mGamepad.left_trigger > 0 ) && (mOuttakeSlides.isRetracted())) {
            mLogger.addLine("==> EXT IN SLD");
            mIntakeSlides.extend(mGamepad.left_trigger * 0.9);
        }
        else if (mGamepad.right_trigger > 0)          {
            mLogger.addLine("==> RLB IN SLD");
            mIntakeSlides.rollback(mGamepad.right_trigger * 0.7);
        }
        else                                         {
            mIntakeSlides.stop();
        }

        if(mGamepad.left_stick_button) {
            mLogger.addLine("==> IN SLD TO TRANSFER");
            if(!mWasLeftStickButtonPressed) { mIntakeSlides.setPosition(IntakeSlides.Position.TRANSFER, 2 ); }
            mWasLeftStickButtonPressed = true;
        }
        else { mWasLeftStickButtonPressed = false; }

        if(mGamepad.x)                 {
            mLogger.addLine(String.format("==> SWT OUT CLW : " + mOuttakeClaw.getPosition()));
            if(!mWasXPressed){ mOuttakeClaw.switchPosition(); }
            mWasXPressed = true;
        }
        else { mWasXPressed = false; }

        if(mGamepad.y)     {
            mLogger.addLine(String.format("==> MDW OUT ARM : " + mOuttakeElbow.getPosition()));
            if(!mWasYPressed){ mOuttakeElbow.moveDown(); }
            mWasYPressed = true;
        }
        else { mWasYPressed = false; }

        if(mGamepad.a) {
            mLogger.addLine(String.format("==> MUP OUT ARM : " + mOuttakeElbow.getPosition()));
            if(!mWasAPressed){ mOuttakeElbow.moveUp();}
            mWasAPressed = true;
        }
        else { mWasAPressed = false; }

        if(mGamepad.b) {
            mLogger.addLine("==> TRANSITION");
            if(!mWasBPressed){ this.transition_optimized() ;}
            mWasBPressed = true;
        }
        else { mWasBPressed = false; }

        if(mGamepad.dpad_left) {
            mLogger.addLine(String.format("==> SWT IN CLW : " + intakeClaw.getPosition()));
            if(!mWasDPadLeftPressed){
                if (intakeClaw.getPosition() == IntakeClaw.Position.OPEN){
                    this.closing();
                }
                if(intakeClaw.getPosition() == IntakeClaw.Position.CLOSED){
                    intakeClaw.setPosition(IntakeClaw.Position.OPEN);
                }
            }
            mWasDPadLeftPressed = true;
        }
        else { mWasDPadLeftPressed = false; }

        if(mGamepad.dpad_up)     {
            mLogger.addLine(String.format("==> MDW IN ARM : " + mIntakeArm.getPosition()));
            if(!mWasDPadUpPressed){ mIntakeElbow.moveDown(); mIntakeArm.moveDown(); }
            mWasDPadUpPressed = true;
        }
        else { mWasDPadUpPressed = false; }

        if(mGamepad.dpad_down) {
            mLogger.addLine(String.format("==> MUP IN ARM : " + mIntakeArm.getPosition()));
            if(!mWasDPadDownPressed){ mIntakeArm.moveUp(); mIntakeElbow.moveUp();}
            mWasDPadDownPressed = true;
        }
        else { mWasDPadDownPressed = false; }

//        if(mGamepad.dpad_right) {
//            mLogger.addLine(String.format("==> RETRACT"));
//            if(!mWasDPadRightPressed){ this.retract() ;}
//            mWasDPadRightPressed = true;
//        }
//        else { mWasDPadRightPressed = false; }

        if(mGamepad.left_stick_x < 0) {
            mLogger.addLine(String.format("==> RDW IN WRS : " + intakeWrist.getPosition()));
            if(!mWasLeftStickXNegativePressed){ intakeWrist.rotateDown(); }
            mWasLeftStickXNegativePressed = true;
        }
        else { mWasLeftStickXNegativePressed = false; }

        if(mGamepad.left_stick_x > 0) {
            mLogger.addLine(String.format("==> RUP IN WRS : " + intakeWrist.getPosition()));
            if(!mWasLeftStickXPositivePressed){ intakeWrist.rotateUp(); }
            mWasLeftStickXPositivePressed = true;
        }
        else { mWasLeftStickXPositivePressed = false; }

        if(mGamepad.right_stick_x < 0) {
            mLogger.addLine(String.format("==> RDW OUT WRS : " + mOuttakeWrist.getPosition()));
            if(!mWasRightStickXNegativePressed){ mOuttakeWrist.rotateDown(); }
            mWasRightStickXNegativePressed = true;
        }
        else { mWasRightStickXNegativePressed = false; }

        if(mGamepad.right_stick_x > 0) {
            mLogger.addLine(String.format("==> RUP OUT WRS : " + mOuttakeWrist.getPosition()));
            if(!mWasRightStickXPositivePressed){ mOuttakeWrist.rotateUp(); }
            mWasRightStickXPositivePressed = true;
        }
        else { mWasRightStickXPositivePressed = false; }

        if(mTransitionMode != TransitionMode.NONE) { this.transition_optimized(); }
        if(mClawMode != ClawMode.NONE)             { this.closing();    }

        mLogger.addLine("\n-------- POSITION --------");
        mLogger.addLine(mIntakeSlides.logPositions());
        mLogger.addLine(mOuttakeSlides.logPositions());

        mLogger.addLine("\n--------- MOVING ---------");
        mLogger.addLine(this.logMovements());
    }

    public void transition (){

        mLogger.addLine("TRANSITIONING : " + mTransitionMode);

        if (mTransitionMode == TransitionMode.NONE) {
            // Just transition to make sure that even though the first robot part is not yet ready
            // to move, we won't forget we have to keep on transiting
            mTransitionMode = TransitionMode.WAITING;
        }
        if (mTransitionMode == TransitionMode.WAITING) {
            mIntakeSlides.setPosition(IntakeSlides.Position.EXTEND, 20);

            if(mIntakeSlides.getPosition() == IntakeSlides.Position.EXTEND) {
                mTransitionMode = TransitionMode.IS_EXTENDING_INTAKE_SLIDES;
            }
        }
        if (mTransitionMode == TransitionMode.IS_EXTENDING_INTAKE_SLIDES && !mIntakeSlides.isMoving()) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.OPEN);
            mOuttakeWrist.setPosition(OuttakeWrist.Position.NULL);
            mOuttakeElbow.setPosition(OuttakeElbow.Position.TRANSFER);

            if(     (mOuttakeClaw.getPosition() == OuttakeClaw.Position.OPEN) &&
                    (mOuttakeWrist.getPosition() == OuttakeWrist.Position.NULL) &&
                    (mOuttakeElbow.getPosition() == OuttakeElbow.Position.TRANSFER))
            {
                mTransitionMode = TransitionMode.IS_MOVING_OUTTAKE_ARM;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_MOVING_OUTTAKE_ARM && !mOuttakeClaw.isMoving() && !mOuttakeWrist.isMoving() && !mOuttakeElbow.isMoving())
        {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5);

            if(mOuttakeSlides.getPosition() == OuttakeSlides.Position.TRANSFER) {
                mTransitionMode = TransitionMode.IS_MOVING_OUTTAKE_SLIDES;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_MOVING_OUTTAKE_SLIDES && !mOuttakeSlides.isMoving())
        {
            intakeWrist.setPosition(IntakeWrist.Position.NULL );
            mIntakeElbow.setPosition(IntakeElbow.Position.TRANSFER );
            mIntakeArm.setPosition(IntakeArm.Position.TRANSFER );

            if(     (intakeWrist.getPosition() == IntakeWrist.Position.NULL) &&
                    (mIntakeElbow.getPosition() == IntakeElbow.Position.TRANSFER) &&
                    (mIntakeArm.getPosition() == IntakeArm.Position.TRANSFER))
            {
                mTransitionMode = TransitionMode.IS_MOVING_INTAKE_ARM;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_MOVING_INTAKE_ARM && !mIntakeArm.isMoving() && !intakeWrist.isMoving() && !mIntakeElbow.isMoving())
        {
            mIntakeSlides.setPosition(IntakeSlides.Position.TRANSFER, 2);

            if(mIntakeSlides.getPosition() == IntakeSlides.Position.TRANSFER) {
                mTransitionMode = TransitionMode.IS_MOVING_INTAKE_SLIDES;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_MOVING_INTAKE_SLIDES && !mIntakeSlides.isMoving()) {
            intakeClaw.setPosition(IntakeClaw.Position.MICRORELEASED);

            if(intakeClaw.getPosition() == IntakeClaw.Position.MICRORELEASED) {
                mTransitionMode = TransitionMode.IS_MICRO_RELEASING;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_MICRO_RELEASING && !intakeClaw.isMoving()) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.CLOSED);

            if (mOuttakeClaw.getPosition() == OuttakeClaw.Position.CLOSED) {
                mTransitionMode = TransitionMode.IS_GRABBING;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_GRABBING && !mOuttakeClaw.isMoving()) {
            intakeClaw.setPosition(IntakeClaw.Position.OPEN);

            if(intakeClaw.getPosition() == IntakeClaw.Position.OPEN) {
                mTransitionMode = TransitionMode.IS_RELEASING;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_RELEASING && !intakeClaw.isMoving()) {
            mTransitionMode = TransitionMode.NONE;
        }
    }

    public void transition_optimized (){

        mLogger.addLine("TRANSITIONING : " + mTransitionMode);

        if (mTransitionMode == TransitionMode.NONE) {
            // Just transition to make sure that even though the first robot part is not yet ready
            // to move, we won't forget we have to keep on transiting
            mTransitionMode = TransitionMode.WAITING;
        }
        if (mTransitionMode == TransitionMode.WAITING) {
            mIntakeSlides.setPosition(IntakeSlides.Position.EXTEND, 20);

            if(mIntakeSlides.getPosition() == IntakeSlides.Position.EXTEND) {
                mTransitionMode = TransitionMode.IS_EXTENDING_INTAKE_SLIDES;
            }
        }
        if (mTransitionMode == TransitionMode.IS_EXTENDING_INTAKE_SLIDES && !mIntakeSlides.isMoving()) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.OPEN);
            mOuttakeWrist.setPosition(OuttakeWrist.Position.NULL);
            mOuttakeElbow.setPosition(OuttakeElbow.Position.TRANSFER);
            intakeWrist.setPosition(IntakeWrist.Position.NULL );
            mIntakeElbow.setPosition(IntakeElbow.Position.TRANSFER );
            mIntakeArm.setPosition(IntakeArm.Position.TRANSFER );

            if(     (mOuttakeClaw.getPosition() == OuttakeClaw.Position.OPEN) &&
                    (mOuttakeWrist.getPosition() == OuttakeWrist.Position.NULL) &&
                    (mOuttakeElbow.getPosition() == OuttakeElbow.Position.TRANSFER) &&
                    (intakeWrist.getPosition() == IntakeWrist.Position.NULL) &&
                    (mIntakeElbow.getPosition() == IntakeElbow.Position.TRANSFER) &&
                    (mIntakeArm.getPosition() == IntakeArm.Position.TRANSFER))
            {
                mTransitionMode = TransitionMode.IS_MOVING_OUTTAKE_ARM;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_MOVING_OUTTAKE_ARM && !mIntakeArm.isMoving() && !intakeWrist.isMoving() && !mIntakeElbow.isMoving() && !mOuttakeClaw.isMoving() && !mOuttakeWrist.isMoving() && !mOuttakeElbow.isMoving())
        {
            mOuttakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5);

            if(mOuttakeSlides.getPosition() == OuttakeSlides.Position.TRANSFER) {
                mTransitionMode = TransitionMode.IS_MOVING_OUTTAKE_SLIDES;
            }
        }

        else if(mTransitionMode == TransitionMode.IS_MOVING_OUTTAKE_SLIDES && !mIntakeArm.isMoving() && !intakeWrist.isMoving() && !mIntakeElbow.isMoving())
        {
            mIntakeSlides.setPosition(IntakeSlides.Position.TRANSFER, 2);

            if(mIntakeSlides.getPosition() == IntakeSlides.Position.TRANSFER) {
                mTransitionMode = TransitionMode.IS_MOVING_INTAKE_SLIDES;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_MOVING_INTAKE_SLIDES && !mIntakeSlides.isMoving()) {
            intakeClaw.setPosition(IntakeClaw.Position.MICRORELEASED,300);

            if(intakeClaw.getPosition() == IntakeClaw.Position.MICRORELEASED) {
                mTransitionMode = TransitionMode.IS_MICRO_RELEASING;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_MICRO_RELEASING && !intakeClaw.isMoving()) {
            mOuttakeClaw.setPosition(OuttakeClaw.Position.CLOSED);

            if (mOuttakeClaw.getPosition() == OuttakeClaw.Position.CLOSED) {
                mTransitionMode = TransitionMode.IS_GRABBING;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_GRABBING && !mOuttakeClaw.isMoving()) {
            intakeClaw.setPosition(IntakeClaw.Position.OPEN);

            if(intakeClaw.getPosition() == IntakeClaw.Position.OPEN) {
                mTransitionMode = TransitionMode.IS_RELEASING;
            }
        }
        else if(mTransitionMode == TransitionMode.IS_RELEASING && !intakeClaw.isMoving()) {
            mTransitionMode = TransitionMode.NONE;
        }
    }


    public void closing(){
        mLogger.addLine("CLOSING CLAW : " + mClawMode);
        if (mClawMode == ClawMode.NONE) {
            mClawMode = ClawMode.WAITING;
        }
        if(mClawMode == ClawMode.WAITING) {
            intakeClaw.setPosition(IntakeClaw.Position.CLOSED, 100);

            if(intakeClaw.getPosition() == IntakeClaw.Position.CLOSED) {
                mClawMode = ClawMode.IS_CLOSING;
            }
        }
        if(mClawMode == ClawMode.IS_CLOSING && !intakeClaw.isMoving()){

            mIntakeArm.setPosition(IntakeArm.Position.OVER_SUBMERSIBLE, 200);

            if(mIntakeArm.getPosition() == IntakeArm.Position.OVER_SUBMERSIBLE) {
                mClawMode = ClawMode.IS_LIFTING_ARM;
            }
        }
        else if(mClawMode == ClawMode.IS_LIFTING_ARM && !mIntakeArm.isMoving())
        {
            mClawMode = ClawMode.NONE;
        }
    }

    public String logMovements() {
        String result = "";
        if(intakeClaw.isMoving())    { result += "IN CLW\n"; }
        if(intakeWrist.isMoving())   { result += "IN WRS\n"; }
        if(mIntakeArm.isMoving())     { result += "IN ARM\n"; }
        if(mIntakeElbow.isMoving())   { result += "IN ELB\n"; }
        if(mIntakeSlides.isMoving())  { result += "IN SLD\n"; }
        if(mOuttakeClaw.isMoving())   { result += "OUT CLW\n"; }
        if(mOuttakeWrist.isMoving())  { result += "OUT WRS\n"; }
        if(mOuttakeElbow.isMoving())  { result += "OUT ELB\n"; }
        if(mOuttakeSlides.isMoving()) { result += "OUT SLD\n"; }
        return result;
    }
}

