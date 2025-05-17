package org.firstinspires.ftc.teamcode.tuning;

import org.firstinspires.ftc.teamcode.configurations.Configuration;
import org.firstinspires.ftc.teamcode.intake.IntakeSlides;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.intake.IntakeSlides;
import org.firstinspires.ftc.teamcode.vision.Calibration;
import org.opencv.core.Point;

@Config
@TeleOp
public class TestSlides extends LinearOpMode {

    public static double position = 10;

    @Override
    public void runOpMode() throws InterruptedException {

        // Setup the dashboard
        FtcDashboard dashboard = FtcDashboard.getInstance();

        IntakeSlides intakeSlides = new IntakeSlides();
        intakeSlides.setHW(Configuration.s_Current, hardwareMap, dashboard.getTelemetry());
        dashboard.getTelemetry().update();

        waitForStart();

        int count = 0;
        while (opModeIsActive()) {
            count ++;
            try {
                dashboard.getTelemetry().addLine("" + count);
                intakeSlides.goToPosition(position);
            } catch (Exception e) {
                dashboard.getTelemetry().addLine(e.getMessage());
            }
            dashboard.getTelemetry().update();

        }
    }

    // Function to transform pixel point and display real-world point

}
