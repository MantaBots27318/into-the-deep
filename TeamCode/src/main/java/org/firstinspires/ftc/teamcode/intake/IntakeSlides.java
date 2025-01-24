package org.firstinspires.ftc.teamcode.intake;

/* System includes */
import java.util.LinkedHashMap;
import java.util.Map;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

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


public class IntakeSlides {

    public enum Position {
        MIN,
        TRANSFER,
        RETRACT,
        EXTEND,
        MAX,
        UNKNOWN
    }

    private static final Map<String, Position> sConfToPosition = Map.of(
            "min",  Position.MIN,
            "transfer", Position.TRANSFER,
            "retracted", Position.RETRACT,
            "init", Position.EXTEND,
            "max", Position.MAX
    );

    private static final int sTimeOut = 2000; // Timeout in ms

    Telemetry               mLogger;      // Local logger
    String                  mPersistentLog;

    boolean                 mReady;       // True if component is able to fulfil its mission
    SmartTimer              mTimer;       // Timer for timeout management
    boolean                 mIsMoving;

    Position                mPosition;    // Current slide position (unknown if moving freely)

    MotorComponent          mMotor;       // Motors (coupled if specified by the configuration) driving the slides
    PIDFCoefficients        mPID;
    int                     mTolerance;

    Map<Position, Integer>  mPositions;    // Link between positions enumerated and encoder positions


    // Check if the component is currently moving on command
    public boolean isMoving() {
        // If motor stopped turning once, even if it is starting again to hold position, we decide
        // it's no longer moving. By that time, its power would have been changed to 0, so he won't
        // be able to reach its target position and we would be stuck waiting for the timer to unarm
        if(mIsMoving) {
            double error = Math.abs(mMotor.getCurrentPosition() - mMotor.getTargetPosition());
            mPersistentLog = "" + error + " " + mTolerance + " " + mMotor.isBusy() + " " + mMotor.getVelocity();
            if(error <= mTolerance && !mMotor.isBusy() && (Math.abs(mMotor.getVelocity()) < 5)) {
                mIsMoving = false;
            }
        }
        return (mIsMoving && mTimer.isArmed());
    }

    // Initialize component from configuration
    public void setHW(Configuration config, HardwareMap hwm, Telemetry logger) {

        mLogger = logger;
        mReady = true;
        mIsMoving = false;

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


        //Setting the intake slides PID for more precision
        if(mReady){
            mPID = new PIDFCoefficients(12.0,0.05,0,0, MotorControlAlgorithm.LegacyPID);
        }

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
    public void setPosition(Position position, int tolerance)
    {
        if(mReady && !this.isMoving() && mPositions.containsKey(position)) {
            
            mMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, mPID);
            mMotor.setTargetPositionTolerance(tolerance);
            mTolerance = tolerance;
            
            mMotor.setTargetPosition(mPositions.get(position));
            mMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            mIsMoving = true;
            mMotor.setPower(1.0);
            
            mTimer.arm(sTimeOut);

            mPosition = position;

        }
    }


    public boolean isRetracted() {
        boolean result = true;
        if(mReady && mPositions.containsKey(Position.RETRACT)) {
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
            if(mPositions.containsKey(Position.RETRACT)) {
                result += "\nRETRACT IN SLD : " + mPositions.get(Position.RETRACT);
            }
            result += "\nPERSISTENT IN SLD : " + mPersistentLog;
        }
        return result;
    }
    
    public Position getPosition() {
        Position result = Position.UNKNOWN;
        if(mReady) {
            result = mPosition;
        }
        return result;
    }

}


