package org.firstinspires.ftc.teamcode.tuning;

/* System includes */
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/* Acmerobotics includes */
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.config.ValueProvider;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

/* FtcController includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Local includes */
import org.firstinspires.ftc.teamcode.configurations.Configuration;
import org.firstinspires.ftc.teamcode.configurations.ConfServo;


@Config
@TeleOp(name = "ServoTuning")
public class ServoTuning extends LinearOpMode {


    public static String                ALL_SERVOS      = "";
    public static String                CURRENT_SERVO   = "";
    public static double                INCREMENT_STEP  = 0.01;
    public static long                  SLEEP_MS        = 200;
    public static double                TARGET_POS      = 0.0;
    public static String                UPDATED_CONF    = "";
    public static boolean               HOLD_POSITION   = false;

    private final List<String>          mAllServos      = new ArrayList<>();
    private final  Map<String,Servo>    mServos         = new LinkedHashMap<>();
    private String                      mCurrentServo   = "";
    private final Map<String,ConfServo> mCurrentConf    = new LinkedHashMap<>();

    private ReverseProvider             mFirstReverse;
    private ReverseProvider             mSecondReverse;
    private ModeProvider                mMode;

    @Override
    public void runOpMode() {

        try {

            telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());

            /* Load all servos name into list */
            mAllServos.clear();
            mCurrentConf.clear();
            ALL_SERVOS = "";
            Configuration.s_Current.getForTuning().forEach((key, value) -> {
                mAllServos.add(key);
                ALL_SERVOS += key + "; ";
                mCurrentConf.put(key,new ConfServo(value));
            });
            if(CURRENT_SERVO.isEmpty() && !(mAllServos.isEmpty())) { CURRENT_SERVO = mAllServos.get(0); }
            FtcDashboard.getInstance().updateConfig();
            telemetry.update();
            mMode = new ModeProvider();

            waitForStart();

            while(opModeIsActive() && !mAllServos.isEmpty()) {

                telemetry.clear();

                /* -------------- Update Servo if needed --------------- */
                if(!(mCurrentServo.equals(CURRENT_SERVO)) || this.wasReverseChanged()) {

                    // Stop servos if they don't need to hold
                    if (!HOLD_POSITION) { this.stopServos(); }

                    mCurrentServo = CURRENT_SERVO;

                    // Load servos
                    this.updateCurrentServos(mCurrentServo);
                    mMode.set(Mode.FIRST);
                    TARGET_POS = this.getServosPosition();

                    // Adapt configuration
                    if(this.mServos.size() >= 1) {
                        mFirstReverse = new ReverseProvider(mCurrentConf.get(mCurrentServo).getHw(0));
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"REVERSE_CONF_1",mFirstReverse);
                    }
                    else {
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"REVERSE_CONF_1");
                    }
                    if(this.mServos.size() >= 2) {
                        mSecondReverse = new ReverseProvider(mCurrentConf.get(mCurrentServo).getHw(1));
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"REVERSE_CONF_2",mSecondReverse);
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"MODE",mMode);
                    }
                    else {
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"REVERSE_CONF_2");
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"MODE");
                    }

                    // Update configuration
                    UPDATED_CONF = this.updateConf();

                    // Start servos
                    this.startServos();

                    FtcDashboard.getInstance().updateConfig();
                }


                /* ------------------ Manage controls ------------------ */

                // Cycle through servos with DPAD buttons
                if (gamepad1.dpad_up) {

                    // Step to next servos name
                    int currentIndex = this.getIndexFromServoName(mCurrentServo);
                    if (currentIndex < (mAllServos.size() - 1)) { currentIndex++; }
                    else { currentIndex = 0; }
                    CURRENT_SERVO = mAllServos.get(currentIndex);
                    FtcDashboard.getInstance().updateConfig();

                }
                else if (gamepad1.dpad_down) {

                    // Step to previous servos name
                    int currentIndex = this.getIndexFromServoName(mCurrentServo);
                    if (currentIndex > 0) { currentIndex--; }
                    else { currentIndex = mAllServos.size() - 1; }
                    CURRENT_SERVO = mAllServos.get(currentIndex);
                    FtcDashboard.getInstance().updateConfig();

                }

                // Adjust servo position with Left/Right bumpers
                if (gamepad1.left_bumper) {
                    TARGET_POS = Math.max(0.00, TARGET_POS - INCREMENT_STEP); // Decrease position but don't go below 0
                } else if (gamepad1.right_bumper) {
                    TARGET_POS = Math.min(1.00, TARGET_POS + INCREMENT_STEP); // Increase position but don't exceed 1
                }

                this.setServosPosition(TARGET_POS);

                // Display telemetry
                this.logServosState(telemetry);
                telemetry.update();

                sleep(SLEEP_MS);

            }
        }
        catch(Exception e) {
            telemetry.addLine(e.getMessage());
            telemetry.update();
        }


    }

    private int getIndexFromServoName(String name) {
        int result = -1;
        for (int i_servo = 0; i_servo < mAllServos.size(); i_servo++) {
            if (mAllServos.get(i_servo).equals(name)) {
                result = i_servo;
            }
        }
        return result;
    }

    private void updateCurrentServos(String name) {
        mServos.clear();
        if(mCurrentConf.containsKey(name)) {
            mCurrentConf.get(name).getHw().forEach((key, value) -> {
                Servo servo = hardwareMap.tryGet(Servo.class,key);
                if (servo != null) {
                    if(value) { servo.setDirection(Servo.Direction.REVERSE); }
                    else { servo.setDirection(Servo.Direction.FORWARD); }
                    mServos.put(key,servo);
                }
            });
        }
    }

    private void stopServos()
    {
        mServos.forEach((key, value) -> value.getController().pwmDisable());
    }

    private void startServos()
    {
        Servo first = null;
        Servo second = null;

        ConfServo conf = mCurrentConf.get(mCurrentServo);
        if(conf != null && conf.getHw().size() >= 1) {
            if (mServos.containsKey(conf.getHw(0).getKey())) {
                first = mServos.get(conf.getHw(0).getKey());
            }
        }
        if(conf != null && conf.getHw().size() >= 2) {
            if (mServos.containsKey(conf.getHw(1).getKey())) {
                second = mServos.get(conf.getHw(1).getKey());
            }
        }

        if (first != null && (mMode.get() == Mode.FIRST || mMode.get() == Mode.BOTH)) {
            first.getController().pwmEnable();
        }
        if (second != null && (mMode.get() == Mode.SECOND || mMode.get() == Mode.BOTH)) {
            second.getController().pwmEnable();
        }
    }

    private double getServosPosition()
    {
        double result = -1;

        Servo first = null;
        Servo second = null;

        ConfServo conf = mCurrentConf.get(mCurrentServo);

        if(conf != null && conf.getHw().size() >= 1) {
            if (mServos.containsKey(conf.getHw(0).getKey())) {
                first = mServos.get(conf.getHw(0).getKey());
            }
        }
        if(conf != null && conf.getHw().size() >= 2) {
            if (mServos.containsKey(conf.getHw(1).getKey())) {
                second = mServos.get(conf.getHw(1).getKey());
            }
        }

        if ((first != null) && (mMode.get() == Mode.FIRST || mMode.get() == Mode.BOTH)) {
            result = first.getPosition();
        }
        if ((second != null) && (mMode.get() == Mode.SECOND || mMode.get() == Mode.BOTH)) {
            result = second.getPosition();
        }
        return result;
    }

    private void setServosPosition( double position)
    {
        Servo first = null;
        Servo second = null;

        ConfServo conf = mCurrentConf.get(mCurrentServo);
        if(conf != null && conf.getHw().size() >= 1) {
            if (mServos.containsKey(conf.getHw(0).getKey())) {
                first = mServos.get(conf.getHw(0).getKey());
            }
        }
        if(conf != null && conf.getHw().size() >= 2) {
            if (mServos.containsKey(conf.getHw(1).getKey())) {
                second = mServos.get(conf.getHw(1).getKey());
            }
        }

        if ((first != null) && (mMode.get() == Mode.FIRST || mMode.get() == Mode.BOTH)) {
            first.setPosition(position);
        }
        else if (first != null) {
            first.getController().pwmDisable();
        }
        if ((second != null) && (mMode.get() == Mode.SECOND || mMode.get() == Mode.BOTH)) {
            second.setPosition(position);
        }
        else if (second != null) {
            second.getController().pwmDisable();
        }
    }

    private void logServosState(Telemetry logger) {
        logger.addLine("CURRENT SERVOS");
        int index = 0;
        for (Map.Entry<String, Servo> servo : mServos.entrySet()) {
            logger.addLine("--> Servo " + index);
            logger.addLine("-----> HwMap : " + servo.getKey());
            logger.addLine("-----> Direction : " + servo.getValue().getDirection());
            logger.addLine("-----> Position : " + servo.getValue().getPosition());
            logger.addLine("-----> Power : " + servo.getValue().getController().getPwmStatus());
            index ++;
        }
    }

    private boolean   wasReverseChanged()
    {
        boolean result = false;

        ConfServo conf = mCurrentConf.get(mCurrentServo);

        if(conf != null && conf.getHw().size() >= 1) {
            if (mServos.containsKey(conf.getHw(0).getKey())) {
                Servo temp = mServos.get(conf.getHw(0).getKey());
                if ((temp.getDirection() == Servo.Direction.REVERSE) &&
                        !conf.getHw(0).getValue()) {
                    result = true;
                }
                if ((temp.getDirection() == Servo.Direction.FORWARD) &&
                        conf.getHw(0).getValue()) {
                    result = true;
                }
            }
        }

        if(conf != null && conf.getHw().size() >= 2) {
            if (mServos.containsKey(conf.getHw(1).getKey())) {
                Servo temp = mServos.get(conf.getHw(1).getKey());
                if ((temp.getDirection() == Servo.Direction.REVERSE) &&
                        !conf.getHw(1).getValue()) {
                    result = true;
                }
                if ((temp.getDirection() == Servo.Direction.FORWARD) &&
                        conf.getHw(1).getValue()) {
                    result = true;
                }
            }
        }


        return result;

    }


    private String updateConf() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, ConfServo> conf : mCurrentConf.entrySet()) {
            result.append("mServos.put(\"").append(conf.getKey()).append("\", new ConfServo(");
            for (int i_servo = 0; i_servo < conf.getValue().getHw().size(); i_servo ++)
            {
                result.append("\"").append(conf.getValue().getHw(i_servo).getKey()).append("\",");
                if(conf.getValue().getHw(i_servo).getValue()) { result.append("true"); }
                else { result.append("false"); }
                if(i_servo < (conf.getValue().getHw().size() - 1)) { result.append(","); }
            }
            result.append("));");
        }

        return result.toString();

    }

    static class ReverseProvider implements ValueProvider<Boolean> {
        Map.Entry<String, Boolean> mHw;
        public ReverseProvider( Map.Entry<String, Boolean> hw) {
            mHw = hw;
        }
        @Override
        public Boolean get()           { return mHw.getValue(); }
        @Override
        public void set(Boolean Value) { mHw.setValue(Value);   }
    }

    public enum Mode {
        FIRST,
        SECOND,
        BOTH
    }

    static class ModeProvider implements ValueProvider<Mode> {
        Mode mMode;
        @Override
        public Mode get()              { return mMode;  }
        @Override
        public void set(Mode Value)    { mMode = Value; }
    }




}