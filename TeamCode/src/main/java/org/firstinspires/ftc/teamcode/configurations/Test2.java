/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Configuration for the robot second version (18th of january)
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.configurations;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class Test2 extends Configuration {

    protected void initialize(){

        /* Moving configuration */
        mMotors.put("front-left-wheel",new ConfMotor("frontLeft",false));   // CH Motor 0
        mMotors.put("back-left-wheel",new ConfMotor("backLeft",true));      // CH Motor 1
        mMotors.put("back-right-wheel",new ConfMotor("backRight",true));    // CH Motor 2
        mMotors.put("front-right-wheel",new ConfMotor("frontRight",false)); // CH Motor 3

        mImus.put("built-in", new ConfImu("imu", RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.LEFT));
        mImus.put("otos", new ConfImu("sensor_otos"));

        /* Intake configuration */
        mMotors.put("intake-slides",new ConfMotor(
                "intakeSlidesLeft",true,
                "intakeSlidesRight",true));
         mServos.put("intake-wrist-roll", new ConfServo("left", false));           // CH Servo 4     CSRoIn
        mServos.put("intake-claw", new ConfServo("right", false));                      // EH Servo 2     CSIn


        /* Outtake configuration */
        mMotors.put("outtake-slides",new ConfMotor(
                "outtakeSlidesLeft",false,
                "outtakeSlidesRight",false));        // EH Motor 1

        mMotors.get("intake-slides").addPosition("min",0 );
        mMotors.get("intake-slides").addPosition("transfer",177 );
        mMotors.get("intake-slides").addPosition("max",350 );

        mMotors.get("outtake-slides").addPosition("min",0 );
        mMotors.get("outtake-slides").addPosition("transfer",0);
        mMotors.get("outtake-slides").addPosition("max",4000 );


        mServos.get("intake-wrist-roll").addPosition("-2", 0.27);
        mServos.get("intake-wrist-roll").addPosition("-1", 0.335);
        mServos.get("intake-wrist-roll").addPosition("0", 0.405);
        mServos.get("intake-wrist-roll").addPosition("1", 0.47);
        mServos.get("intake-wrist-roll").addPosition("2", 0.54);
        mServos.get("intake-wrist-roll").addPosition("3", 0.605);
        mServos.get("intake-wrist-roll").addPosition("4", 0.675);
        mServos.get("intake-wrist-roll").addPosition("5", 0.74);
        mServos.get("intake-wrist-roll").addPosition("6", 0.82);

        mServos.get("intake-claw").addPosition("closed", 1.0);
        mServos.get("intake-claw").addPosition("microrelease", 0.98);
        mServos.get("intake-claw").addPosition("open", 0.62);

    }

    protected void initializeTuning() {


    }
}
