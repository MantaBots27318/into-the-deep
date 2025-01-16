package org.firstinspires.ftc.teamcode.outtake;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

/* FTC Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Configurations includes */
import org.firstinspires.ftc.teamcode.configurations.Configuration;
import org.firstinspires.ftc.teamcode.configurations.ConfMotor;

/* Component includes */
import org.firstinspires.ftc.teamcode.components.MotorComponent;
import org.firstinspires.ftc.teamcode.components.MotorMock;
import org.firstinspires.ftc.teamcode.components.MotorCoupled;
import org.firstinspires.ftc.teamcode.components.MotorSingle;

/* Utils includes */
import org.firstinspires.ftc.teamcode.utils.SmartTimer;

import java.util.LinkedHashMap;
import java.util.Map;

public class OuttakeSlides {

    public enum Position {
        MIN,
        MAX,
        UNKNOWN,
        TRANSFER,
        HIGH_BASKET,
        LOW_BASKET,
        LOW_SUBMERSIBLE,
        HIGH_SUBMERSIBLE
    };

    private static final Map<String,Position> sConfToPosition = Map.of(
            "transfer", Position.TRANSFER,
            "highBasket",  Position.HIGH_BASKET ,
            "lowBasket",     Position.LOW_BASKET,
            "lowSubmersible",     Position.LOW_SUBMERSIBLE,
            "highSubmersible", Position.HIGH_SUBMERSIBLE,
            "min",  Position.MIN,
            "max", Position.MAX

    );

    private static int sTimeOut = 5000; // Timeout in ms

    Telemetry               mLogger;      // Local logger

    boolean                 mReady;       // True if component is able to fulfil its mission
    SmartTimer              mTimer;       // Timer for timeout management

    Position                mPosition;    // Current slide position (unknown if movimg freely

    MotorComponent          mMotor;       // Motors (coupled if specified by the configuration) driving the slides

    Map<Position, Integer>  mPositions;    // Link between positions enumerated and encoder positions

    //TouchSensor            mTouchSensorRight;
    //TouchSensor            mTouchSensorLeft;

    // Check if the component is currently moving on command
    public boolean isMoving() { return (mMotor.isBusy() && mTimer.isArmed());}

    // Initialize component from configuration
    public void setHW(Configuration config, HardwareMap hwm, Telemetry logger) {

        mLogger = logger;
        mReady = true;

        mPositions = new LinkedHashMap<>();
        mTimer = new SmartTimer(mLogger);

        String status = "";

        //mTouchSensorRight = hwm.touchSensor.get("outtakeSlidesRightTouch");
        //mTouchSensorLeft = hwm.touchSensor.get("outtakeSlidesLeftTouch");

        ConfMotor slides = config.getMotor("outtake-slides");
        if(slides == null)  { mReady = false; status += " CONF";}
        else {

            // Configure motor
            if (slides.shallMock()) { mMotor = new MotorMock("outtake-slides"); }
            else if (slides.getHw().size() == 1) { mMotor = new MotorSingle(slides, hwm, "outtake-slides", logger); }
            else if (slides.getHw().size() == 2) { mMotor = new MotorCoupled(slides, hwm, "outtake-slides", logger); }

            if (!mMotor.isReady()) { mReady = false; status += " HW";}
            else {
                mMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                mMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER  );


                mPositions.clear();
                Map<String, Integer> confPosition = slides.getPositions();
                for (Map.Entry<String, Integer> pos : confPosition.entrySet()) {
                    if(sConfToPosition.containsKey(pos.getKey())) {
                        mPositions.put(sConfToPosition.get(pos.getKey()), pos.getValue());
                    }
                }
            }
        }

        if (!mPositions.containsKey(Position.MIN)) { mReady = false; }
        if (!mPositions.containsKey(Position.MAX)) { mReady = false; }

        // Log status
        if (mReady) { logger.addLine("==>  OUT SLD : OK"); }
        else        { logger.addLine("==>  OUT SLD : KO : " + status); }

    }

    // Extends the slides with a given power
    public void extend(double Power)   {
        if(mReady && !this.isMoving()) {

            mMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mPosition = Position.UNKNOWN;

            boolean shall_work = (
                    (mMotor.getCurrentPosition() < mPositions.get(Position.MAX)));

            if(mMotor.getCurrentPosition() < mPositions.get(Position.MAX)){
                mMotor.setPower(Power);
            }
            else {
                mMotor.setPower(0);
            }
        }

    }

    // Stop slides
    public void stop() {
        if(mReady && !this.isMoving()) {
            mMotor.setPower(0);
        }
    }

    // Rollback slides with a given power
    public void rollback(double Power) {
        if(mReady && !this.isMoving()) {

            mMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mPosition = Position.UNKNOWN;

            if(mMotor.getCurrentPosition() > mPositions.get(Position.MIN)){
                mMotor.setPower(-Power);
            }
            else  {
                mMotor.setPower(0);
            }

        }
    }

    // Make the slides reach current position. The slides won't respond anymore until slides reached the position
    // A timer is armed fpr time out, and the slides will respond again once unarmed
    public void setPosition(Position position)
    {
        if(mReady && !this.isMoving() && mPositions.containsKey(position)) {

            mMotor.setTargetPosition(mPositions.get(position));
            mMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            mMotor.setPower(0.9);
            mTimer.arm(sTimeOut);

            mPosition = position;

        }
    }

    // Logging function
    public String logPositions()
    {
        return "POS OUT SLD : " + mMotor.logPositions();
    }



}


