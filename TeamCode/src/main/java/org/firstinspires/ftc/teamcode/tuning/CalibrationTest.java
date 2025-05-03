package org.firstinspires.ftc.teamcode.tuning;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.vision.Calibration;
import org.opencv.core.Point;

@Config
@TeleOp
public class CalibrationTest extends LinearOpMode {

    // Inputs (editable from FTC Dashboard)
    public static double pixelX = 0;
    public static double pixelY = 0;
    private Calibration calibration;


    @Override
    public void runOpMode() throws InterruptedException {

        // Setup the dashboard
        FtcDashboard dashboard = FtcDashboard.getInstance();

        // Example source points (pixels in image)

        calibration = new Calibration();



        waitForStart();

        while (opModeIsActive()) {

            try {
                // Create a pixel Point from dashboard inputs
                Point inputPixelPoint = new Point(pixelX, pixelY);

                // Transform and send back through dashboard
                calibration.distance(inputPixelPoint);

                telemetry.addData("Input Pixel X", pixelX);
                dashboard.getTelemetry().addData("Input Pixel X", pixelX);

                telemetry.addData("Input Pixel Y", pixelY);
                telemetry.update();
                dashboard.getTelemetry().addData("Input Pixel Y", pixelY);
                dashboard.getTelemetry().update();

                sleep(100); // Refresh rate
            }
            catch( Exception e) { dashboard.getTelemetry().addLine(e.getMessage()); }
        }
    }

    // Function to transform pixel point and display real-world point

}
