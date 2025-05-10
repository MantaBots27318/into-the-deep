package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

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
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

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

    IntakeElbow mIntakeElbow;
    IntakeArm mIntakeArm;;


    @Override


    public void runOpMode()
    {
        Height = 13-1.5;
        FOV_X = 62.4 * (Math.PI/180);
        FOV_Y = 46.8 * (Math.PI/180);

        X_Resolution = 320;
        Y_Resolution = 240;
        Camera_Angle = 41.18 * (Math.PI/180) - FOV_Y / 2;


        ColorRange GOOD_RED = new ColorRange(
                ColorSpace.HSV,
                //bright red
                new Scalar(ColorDefinition.MIN_RED_HUE,  ColorDefinition.MIN_RED_SATURATION,  ColorDefinition.MIN_RED_VALUE),
                //dark red
                new Scalar(ColorDefinition.MAX_RED_HUE, ColorDefinition.MAX_RED_SATURATION, ColorDefinition.MAX_RED_VALUE)
        );


        ColorBlobLocatorProcessor colorLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(GOOD_RED)         // use a predefined color match
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

        // WARNING:  To be able to view the stream preview on the Driver Station, this code runs in INIT mode.
        while (opModeIsActive() || opModeInInit())
        {



            // Read the current list
            List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();
            FtcDashboard.getInstance().getTelemetry().addLine(""+blobs.size());
            FtcDashboard.getInstance().getTelemetry().update();

            /*
             * The list of Blobs can be filtered to remove unwanted Blobs.
             *   Note:  All contours will be still displayed on the Stream Preview, but only those that satisfy the filter
             *          conditions will remain in the current list of "blobs".  Multiple filters may be used.
             *
             * Use any of the following filters.
             *
             * ColorBlobLocatorProcessor.Util.filterByArea(minArea, maxArea, blobs);
             *   A Blob's area is the number of pixels contained within the Contour.  Filter out any that are too big or small.
             *   Start with a large range and then refine the range based on the likely size of the desired object in the viewfinder.
             *
             * ColorBlobLocatorProcessor.Util.filterByDensity(minDensity, maxDensity, blobs);
             *   A blob's density is an indication of how "full" the contour is.
             *   If you put a rubber band around the contour you would get the "Convex Hull" of the contour.
             *   The density is the ratio of Contour-area to Convex Hull-area.
             *
             * ColorBlobLocatorProcessor.Util.filterByAspectRatio(minAspect, maxAspect, blobs);
             *   A blob's Aspect ratio is the ratio of boxFit long side to short side.
             *   A perfect Square has an aspect ratio of 1.  All others are > 1
             */
             //           ColorBlobLocatorProcessor.Util.filterByArea(50, 20000, blobs);  // filter out very small blobs.
           // ColorBlobLocatorProcessor.Util.filterByAspectRatio(1.5, 3, blobs);

            /*
             * The list of Blobs can be sorted using the same Blob attributes as listed above.
             * No more than one sort call should be made.  Sorting can use ascending or descending order.
             *     ColorBlobLocatorProcessor.Util.sortByArea(SortOrder.DESCENDING, blobs);      // Default
             *     ColorBlobLocatorProcessor.Util.sortByDensity(SortOrder.DESCENDING, blobs);
             *     ColorBlobLocatorProcessor.Util.sortByAspectRatio(SortOrder.DESCENDING, blobs);
             */

//            telemetry.addLine(" Area Density Aspect  Center");


            counter = 0;
            hsvProcessor.DrawBlob(null);
            // Display the size (area) and center location for each Blob.
            for(ColorBlobLocatorProcessor.Blob b : blobs) {
                hsvProcessor.button(save);
                if (b.getAspectRatio() > 1.5) {
                    RotatedRect boxFit = b.getBoxFit();
                    hsvProcessor.DrawBlob(b) ;
//                telemetry.addLine(String.format("%5d  %4.2f   %5.2f  (%3d,%3d)",
//                        b.getContourArea(), b.getDensity(), b.getAspectRatio(), (int) boxFit.center.x, (int) boxFit.center.y));


                    telemetry.update();
                    sleep(50);

                    double x_center = boxFit.center.x - 160;
                    double y_center = -boxFit.center.y + 120;
                    double actual_AngleX = ((FOV_X / X_Resolution) * x_center);
                    double actual_AngleY = Camera_Angle + ((FOV_Y / Y_Resolution) * y_center + (FOV_Y / 2));

                    Actual_Y = Height * Math.tan(actual_AngleY);
                    Actual_X =  (x_center/160)*Math.tan(FOV_X/2)*Math.sqrt(Height*Height+Actual_Y*Actual_Y) ;

                    telemetry.addData("Actual X", Actual_X);
                    telemetry.addData("Actual Y", Actual_Y);
                    telemetry.addData("Center X", x_center);
                    telemetry.addData("Center Y", y_center);
                    telemetry.addData("Angle X", (actual_AngleX * 180 / Math.PI));
                    telemetry.addData("Angle Y", (actual_AngleY * 180 / Math.PI));


                    telemetry.update();

                }
            }
        }
    }
}



