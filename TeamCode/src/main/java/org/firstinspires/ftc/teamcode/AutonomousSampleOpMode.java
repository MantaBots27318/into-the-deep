package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.configurations.Configuration;
import org.firstinspires.ftc.teamcode.roadrunner.SparkFunOTOSDrive;
    @Autonomous
public final class AutonomousSampleOpMode extends LinearOpMode {
        Collecting      mCollecting;

        @Override
    public void runOpMode() throws InterruptedException {
        Pose2d beginPose = new Pose2d(0, 0, Math.PI/2);
        SparkFunOTOSDrive drive = new SparkFunOTOSDrive(hardwareMap, beginPose);
            try {

                mCollecting = new Collecting();
                mCollecting.setHW(Configuration.s_Current, hardwareMap, telemetry, gamepad2);
            }
            catch(Exception e){
                telemetry.addLine("INIT error : " + e.getMessage()) ;
            }
            waitForStart();

            Actions.runBlocking(drive.actionBuilder(beginPose)
                                .setTangent(Math.PI)
                                .splineTo(new Vector2d(-11,-11),5*Math.PI/4)
                                .build());
            mCollecting.dropHighBasket();
            Actions.runBlocking(drive
                    .actionBuilder(drive.pose)
                    .lineToXConstantHeading(-11.5)
                    .turn(Math.PI/4)
                    .lineToXConstantHeading(-14)
                    .build());
            mCollecting.catchFromGround();
            Actions.runBlocking(drive.actionBuilder(drive.pose).lineToXConstantHeading(-10).turn(-7 * Math.PI/24).build());
            mCollecting.dropHighBasketWithoutRetracting();
            Actions.runBlocking(drive.actionBuilder(drive.pose).splineTo(new Vector2d(-60,-10),-Math.PI/2).lineToYConstantHeading(14).build());

            // Read current heading and transform it into the FTC field coordinate system
            // Since the opmode roadrunner reference was backwards, with X along the field length and Y along the field width
            // We have to rotate the angle by 90 degrees
            telemetry.addData("Final Heading", "" + (drive.pose.heading.toDouble() - Math.PI / 2));
            Configuration.s_Current.persist("heading",drive.pose.heading.toDouble() - Math.PI / 2);
            mCollecting.persist(Configuration.s_Current);
            telemetry.update();
        }

}