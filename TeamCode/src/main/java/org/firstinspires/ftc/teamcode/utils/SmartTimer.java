package org.firstinspires.ftc.teamcode.utils;


/* FTC Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;


public class SmartTimer {

    Telemetry       mLogger;

    private long    mStartTime;
    private boolean mIsRunning;
    private boolean mHasAlreadyBeenCalled;
    private int     mTarget;

    public SmartTimer(Telemetry logger){
        mIsRunning = false;
        mHasAlreadyBeenCalled = false;
        mLogger = logger;
    }

    public void arm(int milliseconds)
    {
        mStartTime = System.nanoTime();
        mIsRunning = true;
        mTarget = milliseconds;
        mHasAlreadyBeenCalled = true;
    }

    public boolean isArmed()
    {
        if(mHasAlreadyBeenCalled) {
            double delta = (System.nanoTime() - mStartTime) / 1_000_000.0;
            if (delta >= mTarget) { mIsRunning = false; }
        }
        return mIsRunning;
    }





}