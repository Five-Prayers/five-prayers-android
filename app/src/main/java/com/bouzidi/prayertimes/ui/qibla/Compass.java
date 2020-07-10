package com.bouzidi.prayertimes.ui.qibla;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * @author Gokul Swaminathan
 * @link https://github.com/JavaCafe01/MaterialCompass
 * Updated By Hicham Bouzidi Idrissi (Adding rotation vector sensor && Calibrating Dialog)
 */
public class Compass implements SensorEventListener {
    private static final String TAG = Compass.class.getSimpleName();

    private final AlertDialog calibratingDialog;
    private boolean dialogDismissed = false;

    public interface CompassListener {
        void onNewAzimuth(float azimuth);
    }

    private CompassListener listener;

    private static final float ROTATION_VECTOR_SMOOTHING_FACTOR = 0.5f;
    private static final int SENSOR_DELAY_MICROS = 16 * 1000;

    private SensorManager sensorManager;
    private Sensor rsensor;
    private Sensor asensor;
    private Sensor msensor;

    private boolean useRotationVectorSensor = false;

    private float[] aData = new float[3];
    private float[] mData = new float[3];
    private float[] R = new float[9];
    private float[] I = new float[9];

    private float azimuthFix;

    public Compass(Activity activity) {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);

        asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        calibratingDialog = createCalibratingDialog(activity);
    }

    public void start(Context context) {
        sensorManager.registerListener(this, asensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, rsensor,
                SENSOR_DELAY_MICROS);

        PackageManager manager = context.getPackageManager();
        boolean haveAS = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        boolean haveCS = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        boolean haveCR = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);

        if ((!haveAS || !haveCS) && !haveCR) {
            sensorManager.unregisterListener(this, asensor);
            sensorManager.unregisterListener(this, msensor);
            sensorManager.unregisterListener(this, rsensor);

            Log.e(TAG, "Device don't have enough sensors");
            dialogError(context);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void setAzimuthFix(float fix) {
        azimuthFix = fix;
    }

    public void resetAzimuthFix() {
        setAzimuthFix(0);
    }

    public void setListener(CompassListener l) {
        listener = l;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                useRotationVectorSensor = true;
                getAzimuthFromRotationSensor(event);
            } else if (!useRotationVectorSensor) {
                getAzimuthFromOtherSensors(event);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && !dialogDismissed) {
            calibratingDialog.show();
        }
        calibratingDialog.hide();
    }

    private void getAzimuthFromRotationSensor(SensorEvent event) {
        float[] mRotationMatrixFromVector = new float[16];
        float[] adjustedRotationMatrix = new float[16];
        float[] orientationVals = new float[3];
        float[] mRotationVector = new float[5];

        mRotationVector = exponentialSmoothing(event.values, mRotationVector, ROTATION_VECTOR_SMOOTHING_FACTOR);
        SensorManager.getRotationMatrixFromVector(mRotationMatrixFromVector, mRotationVector);
        SensorManager.remapCoordinateSystem(mRotationMatrixFromVector,
                SensorManager.AXIS_X, SensorManager.AXIS_Z,
                adjustedRotationMatrix);

        SensorManager.getOrientation(adjustedRotationMatrix, orientationVals);

        float azimuth = (float) Math.toDegrees(orientationVals[0]);
        azimuth = (azimuth + azimuthFix + 360) % 360;

        if (listener != null) {
            listener.onNewAzimuth(azimuth);
        }
    }

    private void getAzimuthFromOtherSensors(SensorEvent event) {
        final float alpha = 0.97f;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            aData[0] = alpha * aData[0] + (1 - alpha)
                    * event.values[0];
            aData[1] = alpha * aData[1] + (1 - alpha)
                    * event.values[1];
            aData[2] = alpha * aData[2] + (1 - alpha)
                    * event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mData[0] = alpha * mData[0] + (1 - alpha)
                    * event.values[0];
            mData[1] = alpha * mData[1] + (1 - alpha)
                    * event.values[1];
            mData[2] = alpha * mData[2] + (1 - alpha)
                    * event.values[2];
        }

        boolean success = SensorManager.getRotationMatrix(R, I, aData, mData);

        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            float azimuth = (float) Math.toDegrees(orientation[0]);
            azimuth = (azimuth + azimuthFix + 360) % 360;

            if (listener != null) {
                listener.onNewAzimuth(azimuth);
            }
        }
    }

    private float[] exponentialSmoothing(float[] newValue, float[] lastValue, float alpha) {
        float[] output = new float[newValue.length];
        if (lastValue == null) {
            return newValue;
        }
        for (int i = 0; i < newValue.length; i++) {
            output[i] = lastValue[i] + alpha * (newValue[i] - lastValue[i]);
        }
        return output;
    }

    private void dialogError(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(com.bouzidi.prayertimes.R.string.alert_dialog_title));
        builder.setCancelable(false);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage(context.getString(com.bouzidi.prayertimes.R.string.dialog_message_sensor_not_exist));
        builder.setNegativeButton(com.bouzidi.prayertimes.R.string.common_ok, (dialog, which) -> {
            dialog.dismiss();
            if (context instanceof Activity)
                ((Activity) context).finish();
        });
        builder.create().show();
    }

    private AlertDialog createCalibratingDialog(final Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogLayout = inflater.inflate(com.bouzidi.prayertimes.R.layout.calibrate_compass_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(dialogLayout);
        builder.setMessage(activity.getString(com.bouzidi.prayertimes.R.string.dialog_message_sensor_not_calibrate));
        builder.setNegativeButton(com.bouzidi.prayertimes.R.string.common_ok, (dialog, which) -> {
            dialogDismissed = true;
            dialog.dismiss();
        });
        return builder.create();
    }
}
