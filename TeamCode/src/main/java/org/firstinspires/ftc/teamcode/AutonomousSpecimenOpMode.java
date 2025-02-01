package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.configurations.Configuration;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.SparkFunOTOSDrive;
import org.firstinspires.ftc.teamcode.roadrunner.TankDrive;
import org.firstinspires.ftc.teamcode.tuning.TuningOpModes;


@Autonomous
public final class AutonomousSpecimenOpMode extends LinearOpMode {
     Collecting      mCollecting;

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(0, 0, 0);
        SparkFunOTOSDrive drive = new SparkFunOTOSDrive(hardwareMap, beginPose);
        try {

            mCollecting = new Collecting();
            mCollecting.setHW(Configuration.s_Current, hardwareMap, telemetry, gamepad2);
        } catch (Exception e) {
            telemetry.addLine("INIT error : " + e.getMessage());
        }

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        .lineToX(-31)
                        .build());

        mCollecting.clipSpecimen();

        drive.leftBack.setPower(1.0);
        drive.rightBack.setPower(1.0);
        drive.leftFront.setPower(1.0);
        drive.rightFront.setPower(1.0);

        sleep(100);
        drive.leftBack.setPower(0.0);
        drive.rightBack.setPower(0.0);
        drive.leftFront.setPower(0.0);
        drive.rightFront.setPower(0.0);

       Actions.runBlocking(
               drive.actionBuilder(beginPose)
                      .lineToX(-30.5)
                        .build());

       mCollecting.openClaw();
    }
}