//<uses-feature android:name="android.hardware.sensor.accelerometer"/>
package org.twinone.locker.lock;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_LONG;


/**
 * Created by abhishek on 7/26/2015.
 */
public class onbody implements SensorEventListener {
    private float mLastX, mLastY, mLastZ;
    boolean mInitialized;
   public static boolean onBody;
    long t1 = 0,t2=0;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Context con;
    private float gravityX,gravityY,gravityZ;
    //constructor
    onbody(Context c){
        this.con=c;
        mSensorManager = (SensorManager) con.getSystemService(con.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        Toast.makeText(con,"CONSTRUCTOR", Toast.LENGTH_SHORT).show();
      //
      //  Log.w("ONBODY","CONSTRUCTOR");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            gravityX= 0;
            gravityY= 0;
            gravityZ= 0;
            mInitialized = true;
        } else {
            final float alpha = (float) 0.8;
            gravityX = alpha * gravityX + (1 - alpha) * event.values[0];
            gravityY = alpha * gravityY + (1 - alpha) * event.values[1];
            gravityZ = alpha * gravityZ + (1 - alpha) * event.values[2];
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            String f="";

            if ((deltaX > 0.9 || deltaY > 0.9 || deltaZ > 0.9)){
                //turn it true
                if(!(gravityZ>8))
                {
                    if ((deltaX > 3 || deltaY > 3 || deltaZ > 3))
                    {
                      //  Log.w("GZ",String.valueOf(gravityZ));
                        onBody = true;
                        t1 = System.currentTimeMillis();
                   //     Log.w("ONBODY", "TRUE");
                    }
                }
                else{
                 //   Log.w("GZ",String.valueOf(gravityZ));
                    onBody = true;
                    t1 = System.currentTimeMillis();
                 //   Log.w("ONBODY", String.valueOf(onBody));
                }
                // Toast.makeText(con,"ONBODY TRUE", Toast.LENGTH_SHORT).show();
            }else{
                //check time before making it false
                if(System.currentTimeMillis()-t1>10000){
                    onBody=false;
           //         Log.w("ONBODY",String.valueOf(onBody));
                    //Toast.makeText(con,"ONBODY FALSE", Toast.LENGTH_SHORT).show();
                }else{
             //       Log.w("ONBODY", String.valueOf(onBody));
                    //Toast.makeText(con,"ONBODY WAITING", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
