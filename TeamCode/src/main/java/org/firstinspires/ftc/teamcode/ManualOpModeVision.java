package org.firstinspires.ftc.teamcode;/* Qualcomm includes */
import static java.lang.Thread.sleep;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/* Robot include */
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.configurations.Configuration;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.opencv.core.RotatedRect;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.List;


@TeleOp
public class ManualOpModeVision extends OpMode {

    Driving mDriving;
    Collecting mCollecting;

    static public double Height;
    static public double FOV_X;
    static public double FOV_Y;
    static public double X_Resolution;
    static public double Y_Resolution;
    private double Actual_X;
    private double Actual_Y;
    static public double Camera_Angle;
    private int counter = 0;
    private OpenCvWebcam webcam;
    ColorBlobLocatorProcessor colorLocator;
    HSVProcessor hsvProcessor;


    @Override
    public void init() {

        try {
            mDriving = new Driving();
            mCollecting = new Collecting();

            mDriving.setHW(Configuration.s_Current, hardwareMap, telemetry, gamepad1);
            mCollecting.setHW(Configuration.s_Current, hardwareMap, telemetry, gamepad2);
            Height = 13 - 1.5;
            FOV_X = 62.4 * (Math.PI / 180);
            FOV_Y = 46.8 * (Math.PI / 180);

            X_Resolution = 320;
            Y_Resolution = 240;
            Camera_Angle = 41.18 * (Math.PI / 180) - FOV_Y / 2;

            ColorBlobLocatorProcessor colorLocator = new ColorBlobLocatorProcessor.Builder()
                    .setTargetColorRange(ColorRange.RED)         // use a predefined color match
                    .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)    // exclude blobs inside blobs
                    .setRoi(ImageRegion.asUnityCenterCoordinates(-1, 1, 1, -1))  // search central 1/4 of camera view
                    .setDrawContours(true)                        // Show contours on the Stream Preview
                    .setBlurSize(5)                               // Smooth the transitions between different colors in image
                    .build();
            HSVProcessor hsvProcessor = new HSVProcessor(telemetry);
            VisionPortal portal = new VisionPortal.Builder()
                    .addProcessor(colorLocator)
                    .addProcessor(hsvProcessor)
                    .setCameraResolution(new Size(320, 240))
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .build();


            telemetry.setMsTransmissionInterval(50);   // Speed up telemetry updates, Just use for debugging.
            telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);




        } catch (Exception e) {
            telemetry.addLine("INIT error : " + e.getMessage());

        }
    }


    @Override
    public void loop() {


        try {
            mDriving.control();
            mCollecting.control();
            // Read the current list
            List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();
            counter = 0;
            hsvProcessor.DrawBlob(null);
            // Display the size (area) and center location for each Blob.
            for (ColorBlobLocatorProcessor.Blob b : blobs) {
                if (b.getAspectRatio() > 1.5) {
                    RotatedRect boxFit = b.getBoxFit();
                    hsvProcessor.DrawBlob(b);

                    telemetry.update();
                    sleep(50);

                    double x_center = boxFit.center.x - 160;
                    double y_center = -boxFit.center.y + 120;
                    double actual_AngleX = ((FOV_X / X_Resolution) * x_center);
                    double actual_AngleY = Camera_Angle + ((FOV_Y / Y_Resolution) * y_center + (FOV_Y / 2));

                    Actual_Y = Height * Math.tan(actual_AngleY);
                    Actual_X = (x_center / 160) * Math.tan(FOV_X / 2) * Math.sqrt(Height * Height + Actual_Y * Actual_Y);

                    telemetry.addData("Actual X", Actual_X);
                    telemetry.addData("Actual Y", Actual_Y);
                    telemetry.addData("Center X", x_center);
                    telemetry.addData("Center Y", y_center);
                    telemetry.addData("Angle X", (actual_AngleX * 180 / Math.PI));
                    telemetry.addData("Angle Y", (actual_AngleY * 180 / Math.PI));
                    telemetry.update();



                }



                // Update state machines
                mCollecting.loop();

            }
        }



        catch(Exception e){
                telemetry.addLine("LOOP error : " + e.getMessage());
            }


        }

       @Override
        public void stop () {
            // Make sure that once Teleop is over, we reset all the persisted data
            Configuration.s_Current.reinit();
        }


    }




