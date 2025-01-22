/* -------------------------------------------------------
   Copyright (c) [2025] FASNY
   All rights reserved
   -------------------------------------------------------
   Servos configuration data
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.configurations;

/* System includes */
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfServo {

    // To select if the servo shall be mocked --- not yet activated
    private       boolean                mShallMock   = false;

    // Mapping between servo name on the hub and the motor direction
    private final Map<String, Boolean>   mHw          = new LinkedHashMap<>();
    // Reference servo positions for the given servo (or servo couple)
    private final Map<String, Double>    mPositions   = new LinkedHashMap<>();

    public ConfServo(String Name, boolean ShallReverse)
    {
        mHw.clear();
        mHw.put(Name,ShallReverse);
        mShallMock    = false;
    }

    public ConfServo(String Name1, boolean ShallReverse1, String Name2, boolean ShallReverse2)
    {
        mHw.clear();
        mHw.put(Name1,ShallReverse1);
        mHw.put(Name2,ShallReverse2);
        mShallMock    = false;
    }

    public ConfServo(ConfServo Configuration)
    {
        mShallMock = Configuration.mShallMock;
        for (Map.Entry<String, Boolean> hw : Configuration.mHw.entrySet()) {
            mHw.put(hw.getKey(),hw.getValue());
        }
        for (Map.Entry<String, Double> pos : Configuration.mPositions.entrySet()) {
            mPositions.put(pos.getKey(),pos.getValue());
        }
    }

    public void addHw(String Name, boolean ShallReverse) { mHw.put(Name,ShallReverse);  }
    public void addPosition(String Name, Double Value)   { mPositions.put(Name, Value); }

    public Map<String, Boolean>       getHw()                  { return mHw;}
    public boolean                    shallMock()              { return mShallMock; }
    public Map<String, Double>        getPositions()           { return mPositions; }
    public Map.Entry<String, Boolean> getHw(int index)         {
        Map.Entry<String, Boolean> result = null;
        int iHw = 0;
        for (Map.Entry<String, Boolean> pos : mHw.entrySet()) {
            if(iHw == index) { result = pos; }
            iHw ++;
        }
        return result;
    }
    public Double               getPosition(String Name) {
        if(mPositions.containsKey(Name)) {
            return mPositions.get(Name);
        }
        return -1.0;
    }

}