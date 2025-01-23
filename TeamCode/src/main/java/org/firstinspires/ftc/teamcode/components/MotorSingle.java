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
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/* FTC Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.configurations.ConfMotor;

public class MotorSingle extends MotorComponent {

    Telemetry                       mLogger;

    DcMotorEx                         mMotor;

    int                             mInvertPosition;

    /* -------------- Constructors --------------- */
    public MotorSingle(ConfMotor conf, HardwareMap hwMap, String name, Telemetry logger)
    {
        mReady  = true;
        mLogger = logger;
        mName   = name;
        mInvertPosition = 1;

        Map<String, Boolean> hw = conf.getHw();
        Map<String, Boolean> encoders = conf.getEncoders();
        if((hw.size() == 1) && (encoders.size() == 1) && !conf.shallMock()) {

            List<Map.Entry<String, Boolean>> motors = new ArrayList<>(hw.entrySet());
            List<Map.Entry<String, Boolean>> inverts = new ArrayList<>(encoders.entrySet());
            ListIterator<Map.Entry<String, Boolean>> hwiterator = motors.listIterator();
            ListIterator<Map.Entry<String, Boolean>> inviterator = inverts.listIterator();

            Map.Entry<String,Boolean> motor = hwiterator.next();
            Map.Entry<String,Boolean> invert = inviterator.next();
            mMotor = hwMap.tryGet(DcMotorEx.class, motor.getKey());
            if(mMotor != null && motor.getValue()) { mMotor.setDirection(DcMotor.Direction.REVERSE);}
            else if(mMotor != null)                { mMotor.setDirection(DcMotor.Direction.FORWARD);}

            if(invert.getValue()) { mInvertPosition = -1; }

            if(mMotor != null) { mMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }

        }

        if(mMotor  == null) { mReady = false; }
    }

    @Override
    public int	                        getCurrentPosition()
    {
        int result = -1;
        if(mReady) {
            result = mInvertPosition * mMotor.getCurrentPosition();
        }
        return result;
    }

    @Override
    public DcMotorSimple.Direction      getDirection()
    {
        DcMotorSimple.Direction result = DcMotorSimple.Direction.FORWARD;
        if(mReady) { result = mMotor.getDirection(); }
        return result;
    }

    @Override
    public DcMotor.RunMode	            getMode()
    {
        DcMotor.RunMode result =  DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        if (mReady) { result = mMotor.getMode(); }
        return result;
    }

    @Override
    public int	                        getTargetPosition()
    {
        int result = -1;
        if(mReady) {
            result = mInvertPosition * mMotor.getTargetPosition();
        }
        return result;
    }

    @Override
    public DcMotor.ZeroPowerBehavior	getZeroPowerBehavior()
    {
        DcMotor.ZeroPowerBehavior result = DcMotor.ZeroPowerBehavior.UNKNOWN;
        if(mReady) { result = mMotor.getZeroPowerBehavior(); }
        return result;
    }

    @Override
    public double	                    getPower()
    {
        double result = -1;
        if(mReady) { result = mMotor.getPower(); }
        return result;
    }

    @Override
    public boolean	                    isBusy()
    {
        boolean result = false;
        if(mReady) { result = mMotor.isBusy(); }
        return result;
    }

    @Override
    public void	                        setMode(DcMotor.RunMode mode)
    {
        if(mReady) {
            mMotor.setMode(mode);
        }
    }

    @Override
    public void	                        setDirection(DcMotorSimple.Direction direction)
    {
        if(mReady) {
            mMotor.setDirection(direction);
        }
    }

    @Override
    public void	                        setTargetPosition(int position)
    {
        if(mReady) {
            mMotor.setTargetPosition(mInvertPosition * position);
        }
    }

    @Override
    public void	                        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior)
    {
        if(mReady) {
            mMotor.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    @Override
    public void	                        setPower(double power)
    {
        if(mReady) {
            mMotor.setPower(power);
        }
    }

    @Override
    public PIDFCoefficients            getPIDFCoefficients(DcMotor.RunMode mode){
        PIDFCoefficients result = null;
        if(mReady) {
            result = mMotor.getPIDFCoefficients(mode);
        }
        return result;
    }

    @Override
    public void                        setPIDFCoefficients(DcMotor.RunMode mode, PIDFCoefficients pidfCoefficients){
        if(mReady) {
            mMotor.setPIDFCoefficients(mode, pidfCoefficients);
        }
    }

    @Override
    public void                        setTargetPositionTolerance(int tolerance)
    {
        if(mReady) {
            mMotor.setTargetPositionTolerance(tolerance);
        }
    }

    @Override
    public int                         getTargetPositionTolerance()
    {
        int result = -1;
        if(mReady) {
            result = mMotor.getTargetPositionTolerance();
        }
        return result;
    }

    @Override
    public String                      logPositions()
    {
        String result = "";
        if(mReady) {
            result += "\n  First : P : " + mMotor.getCurrentPosition() + " V : " + mMotor.getVelocity() + " P : " + mMotor.getPower();
        }
        return result;
    }

}
