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
                        .lineToX(-31.5)
                        .build());

        mCollecting.clipSpecimen();

        Actions.runBlocking(
                drive.actionBuilder(drive.pose)
                   .lineToX(-27)
                       .build());

        mCollecting.openClaw();


        Actions.runBlocking(
                drive.actionBuilder(drive.pose)
                        .lineToX(-24)
                        .splineTo(new Vector2d (-17, 38), Math.PI)
                        .lineToX(-5)
                        .build());

        mCollecting.closeClawWall();

        Actions.runBlocking(
                drive.actionBuilder(drive.pose)
                        .lineToX(-10)
                        .splineTo(new Vector2d (-12, -15),0)
                        .lineToX(-25)
                        .build());
        mCollecting.elbowClip();

        Actions.runBlocking(
                drive.actionBuilder(drive.pose)
                        .lineToX(-24)
                        .splineTo(new Vector2d (-17, 38), Math.PI)
                        .lineToX(-5)
                        .build());
    }

}



//40.5
