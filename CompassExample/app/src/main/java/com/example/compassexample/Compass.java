package com.example.compassexample;


import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AlertDialog;

/**
 * Compass Class implementing implements SensorEventListener
 * Interface class
 */
public class Compass implements SensorEventListener {
    private static final String TAG = "Compass";                                                    // Log TAG
    /**
     * CompassListener interface setup
     */
    public interface CompassListener {
        void onNewAzimuth(int azimuth, int accuracy);
    }

    // COMPASS VARIABLES
    private CompassListener listener;                                                               // listener
    Context context;                                                                                // context from super
    int mAzimuth;                                                                                   // current azimuth value
    private SensorManager mSensorManager;                                                           // sensor manager
    private Sensor mRotationV, mAccelerometer, mMagnetometer;                                       // sensors
    float[] rMat   = new float[9];                                                                  // rotation matrix
    float[] orient = new float[9];                                                                  // orientation matrix
    private float[] mLastAccelerometer = new float[3];                                              // acc matrix
    private float[] mLastMagnetometer  = new float[3];                                              // mag matrix
    // SENSOR STATES
    private boolean haveSensor            = false;                                                  // if 1 senor is used
    private boolean haveSensor2           = false;                                                  // if 2 senors are used
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet  = false;

    /**
     * Compass initialization. Sets up the context and sensors
     * @param context: context from super
     */
    public Compass(Context context) {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    /**
     * Initialize and start sensors
     */
    public void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)==null){                    // check if rotation sensor present
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)==null
                    || mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)==null){           // check if acc & mag sensors are present
                noSensorAlert();                                                                    // alert if no sensors are present
            }
            else {                                                                                  // use acc & mag sensors
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this,mMagnetometer,SensorManager.SENSOR_DELAY_UI);
            }
        }
        else {                                                                                      // use rotation sensor
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this,mRotationV,SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Alert Message in case no sensors are present for the application to wrokd
     */
    private void noSensorAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("This device doesn;t have the necessary sensors for this application ")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { stop()/*finish()*/; }
                });
    }

    /**
     * Check what sensors are used and stop them
     */
    public void stop() {
        if (haveSensor && haveSensor2){                                                             // acc & mag sensors used
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        } else if (haveSensor){                                                                     // only rotation used
            mSensorManager.unregisterListener(this, mRotationV);
        }
    }

    /**
     * Set value to the Listener
     * @param l CompassListener
     */
    public void setListener(CompassListener l) {
        listener = l;
    }

    @Override
    /**
     * Check if used sensors changed and calculate new azimuth value
     */
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            // Check what sensor ios changing data
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rMat,event.values);
                mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orient)[0])+360)%360;    // calculate orientation in degrees & restrict to 360
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values,0,mLastAccelerometer,0,event.values.length);
                mLastAccelerometerSet = true;
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values,0,mLastMagnetometer,0,event.values.length);
                mLastMagnetometerSet = true;
            }
            if (mLastAccelerometerSet && mLastMagnetometerSet) {                                            // data received  from both acc & mag
                SensorManager.getRotationMatrix(rMat,null,mLastAccelerometer,mLastMagnetometer);
                SensorManager.getOrientation(rMat,orient);
                mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orient)[0])+360)%360;    // calculate orientation in degrees & restrict to 360
            }
            mAzimuth = Math.round(mAzimuth);
            if (listener != null) {
                listener.onNewAzimuth(mAzimuth,event.accuracy);
            }
        }
    }

    @Override
    /**
     * Don't do/add anything on accuracy change
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
