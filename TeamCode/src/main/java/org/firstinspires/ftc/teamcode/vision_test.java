package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.SortOrder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.configurations.Configuration;
import org.firstinspires.ftc.teamcode.intake.IntakeArm;
import org.firstinspires.ftc.teamcode.intake.IntakeElbow;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.teamcode.ColorDefinition;
import org.firstinspires.ftc.vision.opencv.ColorSpace;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

import org.firstinspires.ftc.teamcode.vision.Calibration;

import java.util.List;

@Config
@TeleOp
public class vision_test extends LinearOpMode  {
   static public double  Height;
   static public boolean save;
    static public double FOV_X ;
    static public double FOV_Y ;
    static public double X_Resolution ;
    static public double Y_Resolution;
    public double Actual_X;
    private double Actual_Y;
    static public double Camera_Angle;
    private int counter = 0;
    private OpenCvWebcam webcam;

    private Calibration mCalibration;
    IntakeElbow mIntakeElbow;
    IntakeArm mIntakeArm;;


    @Override


    public void runOpMode()
    {
        mCalibration = new Calibration();

        ColorRange GOOD_RED = new ColorRange(
                ColorSpace.HSV,
                //bright red
                new Scalar(ColorDefinition.MIN_RED_HUE,  ColorDefinition.MIN_RED_SATURATION,  ColorDefinition.MIN_RED_VALUE),
                //dark red
                new Scalar(ColorDefinition.MAX_RED_HUE, ColorDefinition.MAX_RED_SATURATION, ColorDefinition.MAX_RED_VALUE)
        );


        ColorBlobLocatorProcessor colorLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.BLUE)         // use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)    // exclude blobs inside blobs
                .setRoi(ImageRegion.asUnityCenterCoordinates(-1, 1, 1, -1))  // search central 1/4 of camera view
                .setDrawContours(true)                        // Show contours on the Stream Preview
                .setBlurSize(5)                               // Smooth the transitions between different colors in image
                .build();

        /*
         * Build a vision portal to run the Color Locator process.
         *
         *  - Add the colorLocator process created above.
         *  - Set the desired video resolution.
         *      Since a high resolution will not improve this process, choose a lower resolution that is
         *      supported by your camera.  This will improve overall performance and reduce latency.
         *  - Choose your video source.  This may be
         *      .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))  .....   for a webcam
         *  or
         *      .setCamera(BuiltinCameraDirection.BACK)    ... for a Phone Camera
         */

        mIntakeElbow = new IntakeElbow();
        mIntakeArm = new IntakeArm();

        mIntakeElbow.setHW(Configuration.s_Current, hardwareMap, telemetry);
        mIntakeArm.setHW(Configuration.s_Current, hardwareMap, telemetry);

        while (mIntakeElbow.isMoving() || mIntakeArm.isMoving()) {
            sleep(100);
        }

        mIntakeElbow.setPosition(IntakeElbow.Position.VISION_START);
        mIntakeArm.setPosition(IntakeArm.Position.TRANSFER);

        telemetry.update();


        HSVProcessor hsvProcessor = new HSVProcessor(telemetry);
        VisionPortal portal = new VisionPortal.Builder()
                .addProcessor(colorLocator)
                .addProcessor(hsvProcessor)
                .setCameraResolution(new Size(320, 240))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .build();


        telemetry.setMsTransmissionInterval(50);   // Speed up telemetry updates, Just use for debugging.
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);


        waitForStart();

        // WARNING:  To be able to view the stream preview on the Driver Station, this code runs in INIT mode.
        while (opModeIsActive())
        {
            counter ++;
            // Read the current list
            List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();
            FtcDashboard.getInstance().getTelemetry().addLine(""+blobs.size());

            hsvProcessor.DrawBlob(null);
            hsvProcessor.button(save);

            // Display the size (area) and center location for each Blob.
            ColorBlobLocatorProcessor.Util.sortByArea(SortOrder.ASCENDING, blobs);
            ColorBlobLocatorProcessor.Util.filterByArea(150,70000, blobs);
            for(ColorBlobLocatorProcessor.Blob b : blobs) {
                FtcDashboard.getInstance().getTelemetry().addLine("" + b.getContourArea());
                if (b.getAspectRatio() > 1.5) {
                    FtcDashboard.getInstance().getTelemetry().addLine("selected");
                    RotatedRect boxFit = b.getBoxFit();
                    hsvProcessor.DrawBlob(b) ;

                    Point inputPixelPoint = new Point(boxFit.center.x, boxFit.center.y);
                    mCalibration.distance(inputPixelPoint);

                    telemetry.addData("Pixel X", boxFit.center.x);
                    telemetry.addData("Pixel Y", boxFit.center.y);

                }
            }

            telemetry.update();

            FtcDashboard.getInstance().getTelemetry().addLine("" + counter);
            FtcDashboard.getInstance().getTelemetry().update();
            sleep(50);
        }
    }
}



