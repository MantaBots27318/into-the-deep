package org.firstinspires.ftc.teamcode.intake;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

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
import org.firstinspires.ftc.teamcode.utils.SmartTimer;

import java.util.LinkedHashMap;
import java.util.Map;

public class IntakeSlides {

    public enum Position {
        MIN,
        TRANSFER,
        RETRACT,
        INIT,
        MAX,
        UNKNOWN
    }

    private static final Map<String, Position> sConfToPosition = Map.of(
            "min",  Position.MIN,
            "transfer", Position.TRANSFER,
            "retracted", Position.RETRACT,
            "init", Position.INIT,
            "max", Position.MAX
    );

    private static final int sTimeOut = 2000; // Timeout in ms

    Telemetry               mLogger;      // Local logger

    boolean                 mReady;       // True if component is able to fulfil its mission
    SmartTimer              mTimer;       // Timer for timeout management

    Position                mPosition;    // Current slide position (unknown if moving freely)

    MotorComponent          mMotor;       // Motors (coupled if specified by the configuration) driving the slides

    Map<Position, Integer>  mPositions;    // Link between positions enumerated and encoder positions


    // Check if the component is currently moving on command
    public boolean isMoving() {
        return (mMotor.isBusy() && mTimer.isArmed());
    }

    // Initialize component from configuration
    public void setHW(Configuration config, HardwareMap hwm, Telemetry logger) {

        mLogger = logger;
        mReady = true;

        mPositions = new LinkedHashMap<>();
        mTimer = new SmartTimer(mLogger);

        String status = "";

        ConfMotor slides = config.getMotor("intake-slides");
        if(slides == null)  { mReady = false; status += " CONF";}
        else {

            // Build motor based on configuration
            if (slides.shallMock()) { mMotor = new MotorMock("intake-slides"); }
            else if (slides.getHw().size() == 1) { mMotor = new MotorSingle(slides, hwm, "intake-slides", logger); }
            else if (slides.getHw().size() == 2) { mMotor = new MotorCoupled(slides, hwm, "intake-slides", logger); }

            if (!mMotor.isReady()) { mReady = false; status += " HW";}
            else {
                // Initialize motor
                mMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                mMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

                // Store encoder positions
                mPositions.clear();
                Map<String, Integer> confPosition = slides.getPositions();
                for (Map.Entry<String, Integer> pos : confPosition.entrySet()) {
                    if(sConfToPosition.containsKey(pos.getKey())) {
                        mPositions.put(sConfToPosition.get(pos.getKey()), pos.getValue());
                    }
                }

            }
        }

        // Final check to assess the component readiness
        if (!mPositions.containsKey(Position.MIN)) { mReady = false; }
        if (!mPositions.containsKey(Position.MAX)) { mReady = false; }

        // Log status
        if (mReady) { logger.addLine("==>  IN SLD : OK"); }
        else        { logger.addLine("==>  IN SLD : KO : " + status); }

    }

    // Extends the slides with a given power
    public void extend(double Power)   {

        if(mReady && !this.isMoving())
        {
            mMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mPosition = Position.UNKNOWN;

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
            else {
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
            // mMotor.setPower(position==IntakeSlides.Position.TRANSFER? 0.9 : 0.6);
            mMotor.setPower(1.0);
            mTimer.arm(sTimeOut);

            mPosition = position;

        }
    }


    public boolean isRetracted() {
        boolean result = true;
        if(mReady && mPositions.containsKey(Position.RETRACT)) {
            mLogger.addLine("IN SLD : Current position " + mMotor.getCurrentPosition() + ", Retract : " + mPositions.get(Position.RETRACT));
            result = (mMotor.getCurrentPosition() < mPositions.get(Position.RETRACT));
        }
        return result;
    }

    // Logging function
    public String logPositions()
    {
        String result = "";
        if(mReady) {
            result = "POS IN SLD : " + mMotor.logPositions();
        }
        return result;
    }

}


