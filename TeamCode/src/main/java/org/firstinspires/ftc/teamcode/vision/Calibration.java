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
            new Point(3, 30),
            new Point(21, 27),
            new Point(12, 27),
            new Point(3, 21),
            new Point(18, 21),
            new Point(18, 15),
            new Point(3, 15),
            new Point(12, 12),
            new Point(6, 6),
            new Point(21, 6),

    };
    static Point[] srcPoints = new Point[] {
            new Point(72.2, 9.4),
            new Point(245, 23.6),
            new Point(154.6, 22.5),
            new Point(48, 53),
            new Point(222, 55),
            new Point(232, 97.3),
            new Point(26.3, 97.8),
            new Point(147.6, 124.7),
            new Point(23.6, 204.2),
            new Point(319, 206.7),
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

    public void distance(Point pixelPoint) {
        MatOfPoint2f src = new MatOfPoint2f(pixelPoint);
        MatOfPoint2f dst = new MatOfPoint2f();

        Core.perspectiveTransform(src, dst, homography);

        Point[] dstPoints = dst.toArray();
        if (dstPoints.length > 0) {
            FtcDashboard.getInstance().getTelemetry().addData("Real World X", dstPoints[0].x);
            FtcDashboard.getInstance().getTelemetry().addData("Real World Y", dstPoints[0].y);
            FtcDashboard.getInstance().getTelemetry().update();
        }
    }


    }


