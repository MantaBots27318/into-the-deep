package org.firstinspires.ftc.teamcode;

/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/* Robot include */
import org.firstinspires.ftc.teamcode.configurations.Configuration;

@TeleOp
public class ManualOpMode extends OpMode {

    Driving         mDriving;
    Collecting      mCollecting;

    @Override
    public void init(){

        try {
            mDriving = new Driving();
            mCollecting = new Collecting();

            mDriving.setHW(Configuration.s_Current, hardwareMap, telemetry, gamepad1);
            mCollecting.setHW(Configuration.s_Current, hardwareMap, telemetry, gamepad2);
        }
        catch(Exception e){
            telemetry.addLine("INIT error : " + e.getMessage()) ;
        }

    }
    @Override
    public void loop (){

        try {
            mDriving.control();
            mCollecting.control();

            // Update state machines
            mCollecting.loop();
        }
        catch(Exception e){
            telemetry.addLine("LOOP error : " + e.getMessage()) ;
        }
        
    }
}
