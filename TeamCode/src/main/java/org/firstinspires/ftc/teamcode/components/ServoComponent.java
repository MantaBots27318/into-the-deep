/* -------------------------------------------------------
   Copyright (c) [2025] FASNY
   All rights reserved
   -------------------------------------------------------
   CoupledServo class overloads the FTC servo class to manage
   A couple of servos both turning the same hardware.

   Note that this is a dangerous situation which can result in
   servo destruction if not correctly tuned. The coupled servos
   shall be tuned so that each orientation of the hardware they
   both support correspond to the same position on the 2 servos.
   If wrongly tuned, each of the 2 coupled servos may end up
   each forcing into a position they can not reach without the
   other failing.

   This means for example that the 2 servos are the same model
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.components;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.Servo;

public abstract class ServoComponent {

    protected boolean  mReady;
    protected String   mName;

    public boolean                      isReady() { return mReady;}
    public String                       getName() { return mName; }

    /* ---------- Servo methods override --------- */

    public abstract Servo.Direction     getDirection();
    public abstract double	            getPosition();
    public abstract void	            scaleRange(double min, double max);

    public abstract void	            setDirection(Servo.Direction direction);
    public abstract void	            setPosition(double position);

}
