package org.firstinspires.ftc.teamcode;

import static android.graphics.ColorSpace.Model.RGB;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.util.SortOrder;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ColorSpace;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs ;
import org.opencv.imgproc.Imgproc;


import java.util.ArrayList;
import java.util.List;

public   class HSVProcessor implements VisionProcessor {
    private Mat hsvMat = new Mat();
    private Bitmap bitmap;
    private final FtcDashboard dashboard = FtcDashboard.getInstance();
    private ColorBlobLocatorProcessor.Blob blob ;
    private Telemetry mTelemetry;
    private boolean save ;
    private Mat RGBframe = new Mat ();



    public HSVProcessor(Telemetry telemetry) {
        mTelemetry = telemetry; save = false;
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888  );

    }

    @Override
    public Mat processFrame(Mat frame, long timestamp) {
//        Imgproc.cvtColor(frame, hsvMat, Imgproc.COLOR_RGB2HSV);
        Mat temp = new Mat();
        frame.copyTo(temp);
        if(save) {
            Imgproc.cvtColor(temp, RGBframe , Imgproc.COLOR_BGR2RGB);
            Imgcodecs.imwrite("/sdcard/FIRST/Image.png", RGBframe) ;
            FtcDashboard.getInstance().getTelemetry().addLine("Photo");
            save = false;
        }
        List<MatOfPoint> contours = new ArrayList<>();
        if(blob !=null) { contours.add(blob.getContour());}

        Imgproc.drawContours(temp,contours,0,new Scalar(255,165,0,1),3);
        Utils.matToBitmap(temp, bitmap);

        dashboard.sendImage(bitmap);

        return frame;
    }

    public void button ( boolean replace){
        save = replace;
    }
    public  void onDrawFrame(Canvas canva, int x,int y, float syrf,float iren,Object object ){

    }

    public void DrawBlob (ColorBlobLocatorProcessor.Blob blobprocessor){
        blob =blobprocessor;
    }

    public void onClose() {
        hsvMat.release();
    }
}
