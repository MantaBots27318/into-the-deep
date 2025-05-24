package org.firstinspires.ftc.teamcode.vision;

import com.acmerobotics.dashboard.FtcDashboard;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Imgproc;
import org.opencv.calib3d.Calib3d;

public class Calibration {


    static Point[] dstPoints = new Point[]{
            new Point(12, 12),
            new Point(6, 15),
            new Point(18, 15),
            new Point(3, 21),
            new Point(12, 21),
            new Point(18, 21),
            new Point(3, 24),
            new Point(15, 27),
            new Point(21, 27),
            new Point(6, 30),
            new Point(3, 30),
            new Point(18, 33),

    };
    static Point[] srcPoints = new Point[] {
            new Point(147, 190),
            new Point(37, 145),
            new Point(261, 45),
            new Point(20, 87),
            new Point(151, 87),
            new Point(238, 87),
            new Point(34, 64),
            new Point(186, 46),
            new Point(260, 46),
            new Point(86, 33),
            new Point(54, 33),
            new Point(214, 20),
    };

    private Telemetry mTelemetry;

    private Mat homography ;

    public Calibration(){
        this.warp();}

    public  void warp( ) {



        MatOfPoint2f srcMatOfPoint = new MatOfPoint2f(srcPoints);
        MatOfPoint2f dstMatOfPoint = new MatOfPoint2f(dstPoints);

        // Find homography
         homography = Calib3d.findHomography(srcMatOfPoint, dstMatOfPoint);

        // Warp the source image



    }

    public Point distance(Point pixelPoint) {
        MatOfPoint2f src = new MatOfPoint2f(pixelPoint);
        MatOfPoint2f dst = new MatOfPoint2f();

        Core.perspectiveTransform(src, dst, homography);

        Point[] dstPoints = dst.toArray();
        if (dstPoints.length > 0) {
            FtcDashboard.getInstance().getTelemetry().addData("Real World X", dstPoints[0].x);
            FtcDashboard.getInstance().getTelemetry().addData("Real World Y", dstPoints[0].y);
        }
        return dstPoints[0];
    }


    }


