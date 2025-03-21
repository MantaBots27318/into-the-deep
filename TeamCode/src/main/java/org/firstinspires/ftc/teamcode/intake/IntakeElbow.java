package org.firstinspires.ftc.teamcode.intake;

/* System includes */
import java.util.LinkedHashMap;
import java.util.Map;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;

/* FTC Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.configurations.Configuration;
import org.firstinspires.ftc.teamcode.configurations.ConfServo;

/* Component includes */
import org.firstinspires.ftc.teamcode.components.ServoComponent;
import org.firstinspires.ftc.teamcode.components.ServoMock;
import org.firstinspires.ftc.teamcode.components.ServoCoupled;
import org.firstinspires.ftc.teamcode.components.ServoSingle;

/* Utils includes */
import org.firstinspires.ftc.teamcode.utils.SmartTimer;

public class IntakeElbow {

    public enum Position {
        TRANSFER,
        OVER_SUBMERSIBLE,
        LOOKING,
        GRABBING,
        OFF
    }

    private static final Map<String, Position> sConfToPosition = Map.of(
        "transfer", Position.TRANSFER,
        "overSub",  Position.OVER_SUBMERSIBLE ,
        "look",     Position.LOOKING,
        "grab",     Position.GRABBING,
            "off",  Position.OFF
    );

    private static final int      sTimeOut = 100; // Timeout in ms

    Telemetry               mLogger;      // Local logger

    boolean                 mReady;       // True if component is able to fulfil its mission
    SmartTimer              mTimer;       // Timer for timeout management

    Position                mPosition;    // Current elbow position
    ServoComponent          mServo;       // Servos (coupled if specified by the configuration) driving the elbow
    Map<Position, Double>   mPositions;   // Link between positions enumerated and servos positions

    // Return current reference position
    public Position getPosition() { return mPosition; }

    // Check if the component is currently moving on command
    public boolean isMoving() { return mTimer.isArmed();}

    // Initialize component from configuration
    public void setHW(Configuration config, HardwareMap hwm, Telemetry logger) {

        mLogger = logger;
        mReady = true;
        mPositions   = new LinkedHashMap<>();
        mTimer = new SmartTimer(logger);

        String status = "";

        // Get configuration
        ConfServo pitch  = config.getServo("intake-elbow-pitch");
        if(pitch == null)  { mReady = false; status += " CONF";}
        else {

            // Configure servo
            if (pitch.shallMock()) { mServo = new ServoMock("intake-elbow-pitch"); }
            else if (pitch.getHw().size() == 1) { mServo = new ServoSingle(pitch, hwm, "intake-elbow-pitch", logger); }
            else if (pitch.getHw().size() == 2) { mServo = new ServoCoupled(pitch, hwm, "intake-elbow-pitch", logger); }

            mPositions.clear();
            Map<String, Double> confPosition = pitch.getPositions();
            for (Map.Entry<String, Double> pos : confPosition.entrySet()) {
                if(sConfToPosition.containsKey(pos.getKey())) {
                    mPositions.put(sConfToPosition.get(pos.getKey()), pos.getValue());
                } else {
                    mLogger.addLine("Found unmanaged intake elbow position : " + pos.getKey());
                }
            }

            if (!mServo.isReady()) { mReady = false; status += " HW";}
        }

        // Log status
        if (mReady) { logger.addLine("==>  IN ELB : OK"); }
        else        { logger.addLine("==>  IN ELB : KO : " + status); }

        // Initialize position
        this.setPosition(Position.GRABBING);

    }

    // Make the servo reach current position. A timer is armed, and the servo won't respond until it is unarmed.
    // By the time, the servo should have reached its target position
    public void setPosition(Position position) {

        if( mPositions.containsKey(position) && mReady && !this.isMoving()) {
            mServo.setPosition(mPositions.get(position));
            mTimer.arm(sTimeOut);
            mPosition = position;
        }
    }
    public void setPosition(Position position, int timeout) {

        if( mPositions.containsKey(position) && mReady && !this.isMoving()) {
            mServo.setPosition(mPositions.get(position));
            mPosition = position;
            mTimer.arm(timeout);
        }

    }


    // Switch between the different positions of the arm, in the direction of the transfer position
    public void moveUp() {
        if(mPosition == Position.GRABBING)              { this.setPosition(Position.LOOKING);          }
        else if(mPosition == Position.LOOKING)          { this.setPosition(Position.OVER_SUBMERSIBLE); }
        else if(mPosition == Position.OVER_SUBMERSIBLE) { this.setPosition(Position.TRANSFER);         }
    }

    // Switch between the different positions of the arm, in the direction of the grabbing position
    public void moveDown() {
        if(mPosition == Position.LOOKING)               { this.setPosition(Position.GRABBING);         }
        else if(mPosition == Position.OVER_SUBMERSIBLE) { this.setPosition(Position.LOOKING);          }
        else if(mPosition == Position.TRANSFER)         { this.setPosition(Position.OVER_SUBMERSIBLE); }
    }

}


