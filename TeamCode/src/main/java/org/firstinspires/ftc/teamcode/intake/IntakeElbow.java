package org.firstinspires.ftc.teamcode.intake;

/* System includes */
import java.util.LinkedHashMap;
import java.util.Map;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

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

public class IntakeElbow {

    public enum Position {
        TRANSFER,
        OVER_SUBMERSIBLE,
        LOOKING,
        GRABBING,
        OFF
    };

    private static final Map<String, Position> sConfToPosition = Map.of(
        "transfer", Position.TRANSFER,
        "overSub",  Position.OVER_SUBMERSIBLE ,
        "look",     Position.LOOKING,
        "grab",     Position.GRABBING,
            "off",  Position.OFF
    );

    private static int WAITING_TIME = 100;

    Telemetry               mLogger;

    boolean                 mReady;
    boolean                 mIsFirstCall;
    ElapsedTime             mTimer;

    Position                mPosition;
    ServoComponent          mServo;
    Map<Position, Double>   mPositions   = new LinkedHashMap<>();

    public Position getPosition() { return mPosition; }

    public boolean isMoving() { return (mTimer.milliseconds() < WAITING_TIME);}

    public void setHW(Configuration config, HardwareMap hwm, Telemetry logger) {

        mLogger = logger;
        mReady = true;
        mIsFirstCall = true;
        mTimer = new ElapsedTime(ElapsedTime.MILLIS_IN_NANO);

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

    public void setPosition(Position position) {

        if( mPositions.containsKey(position) && mReady && !this.isMoving()) {
            mServo.setPosition(mPositions.get(position));
            mTimer.reset();
            mIsFirstCall = false;
            mPosition = position;
        }
    }

    public void moveUp() {
        if(mPosition == Position.GRABBING)              { this.setPosition(Position.LOOKING);          }
        else if(mPosition == Position.LOOKING)          { this.setPosition(Position.OVER_SUBMERSIBLE); }
        else if(mPosition == Position.OVER_SUBMERSIBLE) { this.setPosition(Position.TRANSFER);         }
    }

    public void moveDown() {
        if(mPosition == Position.LOOKING)               { this.setPosition(Position.GRABBING);         }
        else if(mPosition == Position.OVER_SUBMERSIBLE) { this.setPosition(Position.LOOKING);          }
        else if(mPosition == Position.TRANSFER)         { this.setPosition(Position.OVER_SUBMERSIBLE); }
    }

}


