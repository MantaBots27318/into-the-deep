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

       Actions.runBlocking(
               drive.actionBuilder(drive.pose)
                      .lineToX(-29)
                        .build());

       mCollecting.openClaw();

       // Read current heading and transform it into the FTC field coordinate system
       // Since the opmode roadrunner reference was backwards, with X along the field length and Y along the field width
       // We have to rotate the angle by 90 degrees
       telemetry.addData("Final Heading", "" + (drive.pose.heading.toDouble() + Math.PI / 2));
       Configuration.s_Current.persist("heading",drive.pose.heading.toDouble() + Math.PI / 2);
    }
}