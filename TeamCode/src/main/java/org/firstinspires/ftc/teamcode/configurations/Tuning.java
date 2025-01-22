/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Configuration to use chassis as a model for V! slides
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.configurations;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class Tuning extends Configuration {

    protected void initialize(){

        /* Moving configuration */
        mMotors.put("front-left-wheel",new ConfMotor("frontLeft",true));    // CH Motor 0
        mMotors.put("back-left-wheel",new ConfMotor("backLeft",true));      // CH Motor 1
        mMotors.put("front-right-wheel",new ConfMotor("frontRight",false)); // CH Motor 2
        mMotors.put("back-right-wheel",new ConfMotor("backRight",true));    // CH Motor 3

        mImus.put("built-in", new ConfImu("imu", RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));
        mImus.put("otos", new ConfImu("sensor_otos"));                                     // EH I2C 3

        /* Intake configuration */
        mMotors.put("intake-slides-left",new ConfMotor("intakeSlidesLeft",true, false));         // EH Motor
        mMotors.put("intake-slides-right",new ConfMotor("intakeSlidesRight",true, true));        // EH Motor 1         // EH Motor 2
        mMotors.get("intake-slides-left").addPosition("min",-1000 );
        mMotors.get("intake-slides-right").addPosition("min",-1000 );
        mMotors.get("intake-slides-left").addPosition("transfer",5 );
        mMotors.get("intake-slides-right").addPosition("transfer",5 );
        mMotors.get("intake-slides-left").addPosition("max",200 );
        mMotors.get("intake-slides-right").addPosition("max",200 );
        mServos.put("intake-arm-pitch", new ConfServo(
                "intakeArmPitchLeft", false,                                                  // CH Servo 5
                "intakeArmPitchRight", true                                                               // EH Servo 1
        ));

        mServos.put("intake-elbow-pitch", new ConfServo("intakeElbowPitch", false));         // EH Servo 0
        mServos.put("intake-wrist-roll", new ConfServo("intakeWristRoll", false));           // CH Servo 4
        mServos.put("intake-claw", new ConfServo("intakeClaw", false));                      // EH Servo 2

        /* Outtake configuration */
        mMotors.put("outtake-slides-left",new ConfMotor("outtakeSlidesLeft",true, false));         // EH Motor
        mMotors.put("outtake-slides-right",new ConfMotor("outtakeSlidesRight",false,false));        // EH Motor 1

        mServos.put("outtake-wrist-roll", new ConfServo("outtakeWristRoll", false));         // CH Servo 0
        mServos.put("outtake-claw", new ConfServo("outtakeClaw", false));                    // CH Servo 1
        mServos.put("outtake-elbow-pitch", new ConfServo(
                "outtakeElbowPitchLeft", false,                                                // CH Servo 2
                "outtakeElbowPitchRight", false)                                                      // CH Servo 3
        );

        /* Motors reference positions */
        mMotors.get("outtake-slides-left").addPosition("min",-1000 );
        mMotors.get("outtake-slides-right").addPosition("min",-1000);
        mMotors.get("outtake-slides-left").addPosition("transfer",5);
        mMotors.get("outtake-slides-right").addPosition("transfer",5 );
        mMotors.get("outtake-slides-left").addPosition("max",200 );
        mMotors.get("outtake-slides-right").addPosition("max",200 );


        /* Intake servos reference positions */
        mServos.get("intake-arm-pitch").addPosition("transfer", 0.97);
        mServos.get("intake-arm-pitch").addPosition("overSub", 0.55);
        mServos.get("intake-arm-pitch").addPosition("look", 0.44);
        mServos.get("intake-arm-pitch").addPosition("grab", 0.39);

        mServos.get("intake-elbow-pitch").addPosition("transfer", 0.15);
        mServos.get("intake-elbow-pitch").addPosition("grab", 0.68);
        mServos.get("intake-elbow-pitch").addPosition("look", 0.70);
        mServos.get("intake-elbow-pitch").addPosition("overSub", 0.73);

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

        /* Outtake servos reference positions */
        mServos.get("outtake-wrist-roll").addPosition("-2", 0.84);
        mServos.get("outtake-wrist-roll").addPosition("-1", 0.775);
        mServos.get("outtake-wrist-roll").addPosition("0", 0.7);
        mServos.get("outtake-wrist-roll").addPosition("1", 0.635);
        mServos.get("outtake-wrist-roll").addPosition("2", 0.56);
        mServos.get("outtake-wrist-roll").addPosition("3", 0.495);
        mServos.get("outtake-wrist-roll").addPosition("4", 0.42);
        mServos.get("outtake-wrist-roll").addPosition("5", 0.355);
        mServos.get("outtake-wrist-roll").addPosition("6", 0.28);

        mServos.get("outtake-claw").addPosition("closed", 0.64);
        mServos.get("outtake-claw").addPosition("microrelease", 0.57);
        mServos.get("outtake-claw").addPosition("open", 0.26);

        mServos.get("outtake-elbow-pitch").addPosition("vertical", 0.015);
        mServos.get("outtake-elbow-pitch").addPosition("outside", 0.06);

    }

}