/* -------------------------------------------------------
   Copyright (c) [2025] FASNY
   All rights reserved
   -------------------------------------------------------
   CoupledMotor class overloads the FTC motor class to manage
   A couple of motors both turning the same hardware.

   Note that this is a dangerous situation which can result in
   motor destruction if not correctly tuned. The coupled motors
   shall be the same model
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.components;

/* System includes */
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.List;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/* FTC Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.configurations.ConfMotor;

public class MotorCoupled extends MotorComponent {

    Telemetry                       mLogger;

    int                             mFirstInvertPosition;
    int                             mSecondInvertPosition;
    DcMotorSimple.Direction         mDirection;

    DcMotorEx                       mFirst;
    DcMotorEx                       mSecond;

    /* -------------- Constructors --------------- */
    public MotorCoupled(ConfMotor conf, HardwareMap hwMap, String name, Telemetry logger)
    {
        mReady  = true;
        mLogger = logger;
        mName   = name;
        mDirection = DcMotor.Direction.FORWARD;
        mFirstInvertPosition = 1;
        mSecondInvertPosition = 1;

        Map<String, Boolean> hw = conf.getHw();
        Map<String, Boolean> encoders = conf.getEncoders();
        if((hw.size() == 2) && (encoders.size() == 2) && !conf.shallMock()) {

            List<Map.Entry<String, Boolean>> motors = new ArrayList<>(hw.entrySet());
            List<Map.Entry<String, Boolean>> inverts = new ArrayList<>(encoders.entrySet());
            ListIterator<Map.Entry<String, Boolean>> hwiterator = motors.listIterator();
            ListIterator<Map.Entry<String, Boolean>> inviterator = inverts.listIterator();

            Map.Entry<String,Boolean> motor = hwiterator.next();
            Map.Entry<String,Boolean> invert = inviterator.next();
            mFirst = hwMap.tryGet(DcMotorEx.class, motor.getKey());
            if(mFirst != null && motor.getValue()) { mFirst.setDirection(DcMotor.Direction.REVERSE);}
            else if(mFirst != null)                { mFirst.setDirection(DcMotor.Direction.FORWARD);}

            if(invert.getValue()) { mFirstInvertPosition = -1; }

            motor = hwiterator.next();
            invert = inviterator.next();
            mSecond = hwMap.tryGet(DcMotorEx.class, motor.getKey());
            if(mSecond != null && motor.getValue()) { mSecond.setDirection(DcMotor.Direction.REVERSE);}
            else if(mSecond != null)                { mSecond.setDirection(DcMotor.Direction.FORWARD);}

            if(invert.getValue()) { mSecondInvertPosition = -1; }

            if(mFirst != null) { mFirst.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
            if(mSecond != null) { mSecond.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }

        }

        if(mFirst  == null) { mReady = false; }
        if(mSecond == null) { mReady = false; }
    }

    @Override
    public int	                        getCurrentPosition()
    {
        int result = -1;
        if(mReady) {
            result = (int) (0.5 * mFirstInvertPosition * mFirst.getCurrentPosition() +
                    mSecondInvertPosition * 0.5 * mSecond.getCurrentPosition());
        }
        return result;
    }

    @Override
    public DcMotorSimple.Direction      getDirection()
    {
        return mDirection;
    }


    @Override
    public DcMotor.RunMode	            getMode()
    {
        DcMotor.RunMode result =  DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        if (mReady) { result = mFirst.getMode(); }
        return result;
    }

    @Override
    public int	                        getTargetPosition()
    {
        int result = -1;
        if(mReady) {
            result = (int) (0.5 * mFirstInvertPosition * mFirst.getTargetPosition() +
                    0.5 * mSecondInvertPosition * mSecond.getTargetPosition());
        }
        return result;
    }

    @Override
    public double	                    getPower()
    {
        double result = -1;
        if(mReady) {
            result = (int) (0.5 * mFirst.getPower() + 0.5 * mSecond.getPower());
        }
        return result;
    }

    @Override
    public DcMotor.ZeroPowerBehavior	getZeroPowerBehavior()
    {
        DcMotor.ZeroPowerBehavior result = DcMotor.ZeroPowerBehavior.UNKNOWN;
        if(mReady) { result = mFirst.getZeroPowerBehavior(); }
        return result;
    }

    @Override
    public boolean	                    isBusy()
    {
        boolean result = false;
        if(mReady) { result = (mFirst.isBusy() && mSecond.isBusy()); }
        return result;
    }

    @Override
    public void	                        setMode(DcMotor.RunMode mode)
    {
        if(mReady) {
            mFirst.setMode(mode);
            mSecond.setMode(mode);
        }
    }

    @Override
    public void	                        setDirection(DcMotorSimple.Direction direction)
    {
        if(direction != mDirection && mReady) {

            if(     mFirst.getDirection()  == DcMotor.Direction.FORWARD) { mFirst.setDirection(DcMotor.Direction.REVERSE);  }
            else if(mFirst.getDirection()  == DcMotor.Direction.REVERSE) { mFirst.setDirection(DcMotor.Direction.FORWARD);  }

            if(     mSecond.getDirection() == DcMotor.Direction.FORWARD) { mSecond.setDirection(DcMotor.Direction.REVERSE); }
            else if(mSecond.getDirection() == DcMotor.Direction.REVERSE) { mSecond.setDirection(DcMotor.Direction.FORWARD); }

            mDirection = direction;

        }
    }

    @Override
    public void	                        setTargetPosition(int position)
    {
        if(mReady) {
            mFirst.setTargetPosition(mFirstInvertPosition * position);
            mSecond.setTargetPosition(mSecondInvertPosition * position);
        }
    }

    @Override
    public void	                        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior)
    {
        if(mReady) {
            mFirst.setZeroPowerBehavior(zeroPowerBehavior);
            mSecond.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    @Override
    public void	                        setPower(double power)
    {
        if(mReady) {
            mFirst.setPower(power);
            mSecond.setPower(power);
        }
    }

    @Override
    public PIDFCoefficients             getPIDFCoefficients(DcMotor.RunMode mode){
        PIDFCoefficients result = null;
        if(mReady) {
            result = mSecond.getPIDFCoefficients(mode);
        }
        return result;
    }

    @Override
    public void                        setPIDFCoefficients(DcMotor.RunMode mode, PIDFCoefficients pidfCoefficients){
        if(mReady) {
            mFirst.setPIDFCoefficients(mode, pidfCoefficients);
            mSecond.setPIDFCoefficients(mode, pidfCoefficients);
        }
    }

    @Override
    public void                        setTargetPositionTolerance(int tolerance)
    {
        if(mReady) {
            mFirst.setTargetPositionTolerance(tolerance);
            mSecond.setTargetPositionTolerance(tolerance);
        }
    }

    @Override
    public int                         getTargetPositionTolerance()
    {
        int result = -1;
        if(mReady) {
            result = mSecond.getTargetPositionTolerance();
        }
        return result;

    }

    @Override
    public String                      logPositions()
    {
        String result = "";
        if(mReady) {
            result += "\n  First : P : " + mFirst.getCurrentPosition() + " V : " + mFirst.getVelocity() + " P : " + mFirst.getPower();
            result += "\n  Second : P : " + mSecond.getCurrentPosition() + " V : " + mSecond.getVelocity() + " P : " + mSecond.getPower();
        }
        return result;
    }

}
