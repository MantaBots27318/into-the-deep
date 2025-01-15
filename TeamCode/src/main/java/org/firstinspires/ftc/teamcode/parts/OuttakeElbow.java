package org.firstinspires.ftc.teamcode.parts;

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

public class OuttakeElbow {

    public enum Position {
        TRANSFER,
        DROP,
        OFF
    };

    private static final Map<String, Position> sConfToPosition = Map.of(
            "transfer", Position.TRANSFER,
            "drop",Position.DROP,
            "off",Position.OFF
    );

    private static int      sTimeOut = 100; // Timeout in ms

    Telemetry               mLogger;      // Local logger

    boolean                 mReady;       // True if component is able to fulfil its mission
    SmartTimer              mTimer;       // Timer for timeout management

    Position                mPosition;    // Current elbow position
    ServoComponent          mServo;       // Servos (coupled if specified by the configuration) driving the elbow
    Map<Position, Double>   mPositions;   // Link between positions enumerated and servos positions

    // Return current reference position
    public boolean isMoving() { return mTimer.isArmed();}

    // Check if the component is currently moving on command
    public Position getPosition() { return mPosition; }

    // Initialize component from configuration
    public void setHW(Configuration config, HardwareMap hwm, Telemetry logger) {

        mLogger = logger;
        mReady = true;

        mPositions   = new LinkedHashMap<>();
        mTimer = new SmartTimer(mLogger);

        String status = "";

        // Get configuration
        ConfServo pitch  = config.getServo("outtake-elbow-pitch");
        if(pitch == null)  { mReady = false; status += " CONF";}
        else {

            // Configure servo
            if (pitch.shallMock()) { mServo = new ServoMock("outtake-elbow-pitch"); }
            else if (pitch.getHw().size() == 1) { mServo = new ServoSingle(pitch, hwm, "outtake-elbow-pitch", logger); }
            else if (pitch.getHw().size() == 2) { mServo = new ServoCoupled(pitch, hwm, "outtake-elbow-pitch", logger); }

            mPositions.clear();
            Map<String, Double> confPosition = pitch.getPositions();
            for (Map.Entry<String, Double> pos : confPosition.entrySet()) {
                if(sConfToPosition.containsKey(pos.getKey())) {
                    mPositions.put(sConfToPosition.get(pos.getKey()), pos.getValue());
                }
            }

            if (!mServo.isReady()) { mReady = false; status += " HW";}
        }

        // Log status
        if (mReady) { logger.addLine("==>  OUT ELB : OK"); }
        else        { logger.addLine("==>  OUT ELB : KO : " + status); }

        // Initialize position
        this.setPosition(Position.OFF);

    }

    // Make the servo reach current position. A timer is armed, and the servo won't respond until it is unarmed.
    // By the time, the servo should have reached its target position
    public void setPosition(Position position) {

        if( mPositions.containsKey(position) && mReady && !this.isMoving()) {
            mServo.setPosition(mPositions.get(position));
            mPosition = position;
            mTimer.arm(sTimeOut);
        }
    }

    // Switch between the different positions of the arm, in the direction of the drop position
    public void moveUp() {
        if(mPosition == Position.OFF)              { this.setPosition(Position.TRANSFER);          }
        else if(mPosition == Position.TRANSFER)    { this.setPosition(Position.DROP); }
    }

    // Switch between the different positions of the arm, in the direction of the off position
    public void moveDown() {
        if(mPosition == Position.DROP)          { this.setPosition(Position.TRANSFER);         }
        else if(mPosition == Position.TRANSFER) { this.setPosition(Position.OFF);          }
    }
}


