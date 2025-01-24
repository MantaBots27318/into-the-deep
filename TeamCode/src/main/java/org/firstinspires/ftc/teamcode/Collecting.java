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

    public enum RetractMode {
        NONE,
        WAITING,
        IS_REACHING_TRANSFER,
        IS_MOVING_OUTTAKE_ARM,
        IS_MOVING_OUTTAKE_SLIDES,
        IS_MOVING_INTAKE_ARM,
        IS_MOVING_INTAKE_SLIDES
    }

    Telemetry       logger;

    TransitionMode  transitionMode;
    RetractMode     retractMode;
    ClawMode        clawMode;
    IntakeSlides    intakeSlides;
    IntakeArm       intakeArm;
    IntakeElbow     intakeElbow;
    IntakeWrist     intakeWrist;
    IntakeClaw      intakeClaw;
    OuttakeSlides   outtakeSlides;
    OuttakeElbow    outtakeElbow;
    OuttakeWrist    outtakeWrist;
    OuttakeClaw     outtakeClaw;

    Gamepad         gamepad;
    boolean         wasXPressed;
    boolean         wasAPressed;
    boolean         wasYPressed;
    boolean         wasBPressed;
    boolean         wasDPadUpPressed;
    boolean         wasDPadDownPressed;
    boolean         wasDPadLeftPressed;
    boolean         wasDPadRightPressed;
    boolean         wasLeftStickXPositivePressed;
    boolean         wasLeftStickXNegativePressed;
    boolean         wasRightStickXPositivePressed;
    boolean         wasRightStickXNegativePressed;
    boolean         wasRightBumperPressed;
    boolean         wasLeftBumperPressed;
    boolean         wasRightStickButtonPressed;
    boolean         wasLeftStickButtonPressed;


    public Collecting() {

        intakeSlides = new IntakeSlides();
        intakeArm    = new IntakeArm();
        intakeElbow  = new IntakeElbow();
        intakeWrist  = new IntakeWrist();
        intakeClaw   = new IntakeClaw();

        outtakeSlides = new OuttakeSlides();
        outtakeElbow = new OuttakeElbow();
        outtakeWrist = new OuttakeWrist();
        outtakeClaw = new OuttakeClaw();

        wasXPressed = false;
        wasAPressed = false;
        wasYPressed = false;
        wasBPressed = false;

        wasDPadDownPressed = false;
        wasDPadUpPressed = false;
        wasDPadLeftPressed = false;
        wasDPadRightPressed = false;

        wasLeftStickXPositivePressed = false;
        wasLeftStickXNegativePressed = false;
        wasRightStickXPositivePressed = false;
        wasRightStickXNegativePressed = false;

        wasRightBumperPressed = false;
        wasLeftBumperPressed = false;
        wasRightStickButtonPressed = false;
        wasLeftStickButtonPressed = false;

        transitionMode = TransitionMode.NONE;
        retractMode = RetractMode.NONE;
        clawMode = ClawMode.NONE;
    }

    public void setHW(Configuration config, HardwareMap hwm, Telemetry tm, Gamepad gp) {

        logger = tm;
        logger.addLine("=== COLLECTING ===");

        intakeSlides.setHW(config, hwm, tm);
        intakeArm.setHW(config, hwm, tm);
        intakeElbow.setHW(config, hwm, tm);
        intakeWrist.setHW(config, hwm, tm);
        intakeClaw.setHW(config, hwm, tm);

        outtakeSlides.setHW(config, hwm, tm);
        outtakeElbow.setHW(config, hwm, tm);
        outtakeWrist.setHW(config, hwm, tm);
        outtakeClaw.setHW(config, hwm, tm);

        gamepad = gp;
    }

    public void move() {

        logger.addLine("-------- FUNCTION --------");

        if (gamepad.left_bumper && (intakeSlides.isRetracted()))
        {
            logger.addLine("==> EXT OUT SLD");
            outtakeSlides.extend(0.9);
        }
        else if (gamepad.right_bumper) {
            logger.addLine("==> RLB OUT SLD");
            outtakeSlides.rollback(0.7);
        }
        else                            {
            outtakeSlides.stop();
        }

        if(gamepad.right_stick_button) {
            logger.addLine("==> OUT SLD TO TRANSFER");
            if(!wasRightStickButtonPressed) { outtakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5 ); }
            wasRightStickButtonPressed = true;
        }
        else { wasRightStickButtonPressed = false; }

        if((gamepad.left_trigger > 0 ) && (outtakeSlides.isRetracted())) {
            logger.addLine("==> EXT IN SLD");
            intakeSlides.extend(gamepad.left_trigger * 0.9);
        }
        else if (gamepad.right_trigger > 0)          {
            logger.addLine("==> RLB IN SLD");
            intakeSlides.rollback(gamepad.right_trigger * 0.7);
        }
        else                                         {
            intakeSlides.stop();
        }

        if(gamepad.left_stick_button) {
            logger.addLine("==> IN SLD TO TRANSFER");
            if(!wasLeftStickButtonPressed) { intakeSlides.setPosition(IntakeSlides.Position.TRANSFER, 2 ); }
            wasLeftStickButtonPressed = true;
        }
        else { wasLeftStickButtonPressed = false; }

        if(gamepad.x)                 {
            logger.addLine(String.format("==> SWT OUT CLW : " + outtakeClaw.getPosition()));
            if(!wasXPressed){ outtakeClaw.switchPosition(); }
            wasXPressed = true;
        }
        else { wasXPressed = false; }

        if(gamepad.y)     {
            logger.addLine(String.format("==> MDW OUT ARM : " + outtakeElbow.getPosition()));
            if(!wasYPressed){ outtakeElbow.moveDown(); }
            wasYPressed = true;
        }
        else { wasYPressed = false; }

        if(gamepad.a) {
            logger.addLine(String.format("==> MUP OUT ARM : " + outtakeElbow.getPosition()));
            if(!wasAPressed){ outtakeElbow.moveUp();}
            wasAPressed = true;
        }
        else { wasAPressed = false; }

        if(gamepad.b) {
            logger.addLine("==> TRANSITION");
            if(!wasBPressed){ this.transition() ;}
            wasBPressed = true;
        }
        else { wasBPressed = false; }

        if(gamepad.dpad_left) {
            logger.addLine(String.format("==> SWT IN CLW : " + intakeClaw.getPosition()));
            if(!wasDPadLeftPressed){
                if (intakeClaw.getPosition() == IntakeClaw.Position.OPEN){
                    this.closing();
                }
                if(intakeClaw.getPosition() == IntakeClaw.Position.CLOSED){
                    intakeClaw.setPosition(IntakeClaw.Position.OPEN);
                }
            }
            wasDPadLeftPressed = true;
        }
        else { wasDPadLeftPressed = false; }

        if(gamepad.dpad_up)     {
            logger.addLine(String.format("==> MDW IN ARM : " + intakeArm.getPosition()));
            if(!wasDPadUpPressed){ intakeElbow.moveDown(); intakeArm.moveDown(); }
            wasDPadUpPressed = true;
        }
        else { wasDPadUpPressed = false; }

        if(gamepad.dpad_down) {
            logger.addLine(String.format("==> MUP IN ARM : " + intakeArm.getPosition()));
            if(!wasDPadDownPressed){ intakeArm.moveUp(); intakeElbow.moveUp();}
            wasDPadDownPressed = true;
        }
        else { wasDPadDownPressed = false; }

//        if(gamepad.dpad_right) {
//            logger.addLine(String.format("==> RETRACT"));
//            if(!wasDPadRightPressed){ this.retract() ;}
//            wasDPadRightPressed = true;
//        }
//        else { wasDPadRightPressed = false; }

        if(gamepad.left_stick_x < 0) {
            logger.addLine(String.format("==> RDW IN WRS : " + intakeWrist.getPosition()));
            if(!wasLeftStickXNegativePressed){ intakeWrist.rotateDown(); }
            wasLeftStickXNegativePressed = true;
        }
        else { wasLeftStickXNegativePressed = false; }

        if(gamepad.left_stick_x > 0) {
            logger.addLine(String.format("==> RUP IN WRS : " + intakeWrist.getPosition()));
            if(!wasLeftStickXPositivePressed){ intakeWrist.rotateUp(); }
            wasLeftStickXPositivePressed = true;
        }
        else { wasLeftStickXPositivePressed = false; }

        if(gamepad.right_stick_x < 0) {
            logger.addLine(String.format("==> RDW OUT WRS : " + outtakeWrist.getPosition()));
            if(!wasRightStickXNegativePressed){ outtakeWrist.rotateDown(); }
            wasRightStickXNegativePressed = true;
        }
        else { wasRightStickXNegativePressed = false; }

        if(gamepad.right_stick_x > 0) {
            logger.addLine(String.format("==> RUP OUT WRS : " + outtakeWrist.getPosition()));
            if(!wasRightStickXPositivePressed){ outtakeWrist.rotateUp(); }
            wasRightStickXPositivePressed = true;
        }
        else { wasRightStickXPositivePressed = false; }

        if(transitionMode != TransitionMode.NONE) { this.transition(); }
        if(clawMode != ClawMode.NONE)             { this.closing();    }

        logger.addLine("\n-------- POSITION --------");
        logger.addLine(intakeSlides.logPositions());
        logger.addLine(outtakeSlides.logPositions());

        logger.addLine("\n--------- MOVING ---------");
        logger.addLine(this.logMovements());
    }

    public void transition (){

        logger.addLine("TRANSITIONING : " + transitionMode);

        if (transitionMode == TransitionMode.NONE) {
            // Just transition to make sure that even though the first robot part is not yet ready
            // to move, we won't forget we have to keep on transiting
            transitionMode = TransitionMode.WAITING;
        }
        if (transitionMode == TransitionMode.WAITING) {
            intakeSlides.setPosition(IntakeSlides.Position.EXTEND, 20);

            if(intakeSlides.getPosition() == IntakeSlides.Position.EXTEND) {
                transitionMode = TransitionMode.IS_EXTENDING_INTAKE_SLIDES;
            }
        }
        if (transitionMode == TransitionMode.IS_EXTENDING_INTAKE_SLIDES && !intakeSlides.isMoving()) {
            outtakeClaw.setPosition(OuttakeClaw.Position.OPEN);
            outtakeWrist.setPosition(OuttakeWrist.Position.NULL);
            outtakeElbow.setPosition(OuttakeElbow.Position.TRANSFER);

            if(     (outtakeClaw.getPosition() == OuttakeClaw.Position.OPEN) &&
                    (outtakeWrist.getPosition() == OuttakeWrist.Position.NULL) &&
                    (outtakeElbow.getPosition() == OuttakeElbow.Position.TRANSFER))
            {
                transitionMode = TransitionMode.IS_MOVING_OUTTAKE_ARM;
            }
        }
        else if(transitionMode == TransitionMode.IS_MOVING_OUTTAKE_ARM && !outtakeClaw.isMoving() && !outtakeWrist.isMoving() && !outtakeElbow.isMoving())
        {
            outtakeSlides.setPosition(OuttakeSlides.Position.TRANSFER, 5);

            if(outtakeSlides.getPosition() == OuttakeSlides.Position.TRANSFER) {
                transitionMode = TransitionMode.IS_MOVING_OUTTAKE_SLIDES;
            }
        }
        else if(transitionMode == TransitionMode.IS_MOVING_OUTTAKE_SLIDES && !outtakeSlides.isMoving())
        {
            intakeWrist.setPosition(IntakeWrist.Position.NULL );
            intakeElbow.setPosition(IntakeElbow.Position.TRANSFER );
            intakeArm.setPosition(IntakeArm.Position.TRANSFER );

            if(     (intakeWrist.getPosition() == IntakeWrist.Position.NULL) &&
                    (intakeElbow.getPosition() == IntakeElbow.Position.TRANSFER) &&
                    (intakeArm.getPosition() == IntakeArm.Position.TRANSFER))
            {
                transitionMode = TransitionMode.IS_MOVING_INTAKE_ARM;
            }
        }
        else if(transitionMode == TransitionMode.IS_MOVING_INTAKE_ARM && !intakeArm.isMoving() && !intakeWrist.isMoving() && !intakeElbow.isMoving())
        {
            intakeSlides.setPosition(IntakeSlides.Position.TRANSFER, 2);

            if(intakeSlides.getPosition() == IntakeSlides.Position.TRANSFER) {
                transitionMode = TransitionMode.IS_MOVING_INTAKE_SLIDES;
            }
        }
        else if(transitionMode == TransitionMode.IS_MOVING_INTAKE_SLIDES && !intakeSlides.isMoving()) {
            intakeClaw.setPosition(IntakeClaw.Position.MICRORELEASED);

            if(intakeClaw.getPosition() == IntakeClaw.Position.MICRORELEASED) {
                transitionMode = TransitionMode.IS_MICRO_RELEASING;
            }
        }
        else if(transitionMode == TransitionMode.IS_MICRO_RELEASING && !intakeClaw.isMoving()) {
            outtakeClaw.setPosition(OuttakeClaw.Position.CLOSED);

            if (outtakeClaw.getPosition() == OuttakeClaw.Position.CLOSED) {
                transitionMode = TransitionMode.IS_GRABBING;
            }
        }
        else if(transitionMode == TransitionMode.IS_GRABBING && !outtakeClaw.isMoving()) {
            intakeClaw.setPosition(IntakeClaw.Position.OPEN);

            if(intakeClaw.getPosition() == IntakeClaw.Position.OPEN) {
                transitionMode = TransitionMode.IS_RELEASING;
            }
        }
        else if(transitionMode == TransitionMode.IS_RELEASING && !intakeClaw.isMoving()) {
            transitionMode = TransitionMode.NONE;
        }
    }

    public void closing(){
        logger.addLine("CLOSING CLAW : " + clawMode);
        if (clawMode == ClawMode.NONE) {
            clawMode = ClawMode.WAITING;
        }
        if(clawMode == ClawMode.WAITING) {
            intakeClaw.setPosition(IntakeClaw.Position.CLOSED);

            if(intakeClaw.getPosition() == IntakeClaw.Position.CLOSED) {
                clawMode = ClawMode.IS_CLOSING;
            }
        }
        if(clawMode == ClawMode.IS_CLOSING && !intakeClaw.isMoving()){

            intakeArm.setPosition(IntakeArm.Position.OVER_SUBMERSIBLE);

            if(intakeArm.getPosition() == IntakeArm.Position.OVER_SUBMERSIBLE) {
                clawMode = ClawMode.IS_LIFTING_ARM;
            }
        }
        else if(clawMode == ClawMode.IS_LIFTING_ARM && !intakeArm.isMoving())
        {
            clawMode = ClawMode.NONE;
        }
    }

    public String logMovements() {
        String result = "";
        if(intakeClaw.isMoving())    { result += "IN CLW\n"; }
        if(intakeWrist.isMoving())   { result += "IN WRS\n"; }
        if(intakeArm.isMoving())     { result += "IN ARM\n"; }
        if(intakeElbow.isMoving())   { result += "IN ELB\n"; }
        if(intakeSlides.isMoving())  { result += "IN SLD\n"; }
        if(outtakeClaw.isMoving())   { result += "OUT CLW\n"; }
        if(outtakeWrist.isMoving())  { result += "OUT WRS\n"; }
        if(outtakeElbow.isMoving())  { result += "OUT ELB\n"; }
        if(outtakeSlides.isMoving()) { result += "OUT SLD\n"; }
        return result;
    }
}

