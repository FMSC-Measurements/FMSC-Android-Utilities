package com.usda.fmsc.android.utilities;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.renderscript.Float2;

/**
 * Based on DeviceOrientation by abdelhady (9/23/14).
 *
 * to use this class do the following 3 steps in your activity:
 *
 * define 3 sensors as member variables
 Sensor accelerometer;
 Sensor magnetometer;
 Sensor vectorSensor;
 DeviceOrientation deviceOrientation;
 *
 * add this to the activity's onCreate
 mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
 accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
 magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
 deviceOrientation = new DeviceOrientation();
 *
 * add this to onResume
 mSensorManager.registerListener(deviceOrientation.getEventListener(), accelerometer, SensorManager.SENSOR_DELAY_UI);
 mSensorManager.registerListener(deviceOrientation.getEventListener(), magnetometer, SensorManager.SENSOR_DELAY_UI);
 *
 * add this to onPause
 mSensorManager.unregisterListener(deviceOrientation.getEventListener());
 *
 *
 * then, you can simply call * deviceOrientation.getOrientation() * wherever you want
 *
 *
 * another alternative to this class's approach:
 * http://stackoverflow.com/questions/11175599/how-to-measure-the-tilt-of-the-phone-in-xy-plane-using-accelerometer-in-android/15149421#15149421
 *
 */
public class DeviceOrientationEx implements SensorEventListener {
    private static final int ORIENTATION_PORTRAIT = ExifInterface.ORIENTATION_ROTATE_90; // 6
    private static final int ORIENTATION_LANDSCAPE_REVERSE = ExifInterface.ORIENTATION_ROTATE_180; // 3
    private static final int ORIENTATION_LANDSCAPE = ExifInterface.ORIENTATION_NORMAL; // 1
    private static final int ORIENTATION_PORTRAIT_REVERSE = ExifInterface.ORIENTATION_ROTATE_270; // 8

    public enum ScreenOrientation {
        Portrait(ORIENTATION_PORTRAIT),
        Landscape(ORIENTATION_LANDSCAPE),
        PortraitReverse(ORIENTATION_PORTRAIT_REVERSE),
        LandscapeReverse(ORIENTATION_LANDSCAPE_REVERSE);

        private final int value;

        ScreenOrientation(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ScreenOrientation parse(int id) {
            for (ScreenOrientation so : values()) {
                if (so.getValue() == id)
                    return so;
            }
            throw new IllegalArgumentException("Invalid ScreenOrientation id: " + id);
        }

        @Override
        public String toString() {
            switch(this) {
                case Portrait: return "Portrait";
                case Landscape: return "Landscape";
                case PortraitReverse: return "Portrait Reverse";
                case LandscapeReverse: return "Landscape Reverse";
                default: throw new IllegalArgumentException();
            }
        }
    }


    private int smoothness;
    private float averagePitch = 0;
    private float averageAzimuth = 0;
    private float averageRoll = 0;
    private ScreenOrientation screen_orientation = ScreenOrientation.Portrait;
    private ScreenOrientation old_screen_orientation = screen_orientation;

    private float[] mGravity;
    private float[] mGeomagnetic;

    private float[] pitches;
    private float[] azimuthes;
    private float[] rolls;

    private SensorManager mSensorManager;
    private Sensor accelerometer, magnetometer;

    private PostDelayHandler postDelayHandler = new PostDelayHandler(250);

    private ScreenOrientationChangeListener listener;


    public DeviceOrientationEx(Context context) {
        this(context, 1);
    }

    public DeviceOrientationEx(Context context, int smoothness) {
        this.smoothness = smoothness;
        pitches = new float[smoothness];
        azimuthes = new float[smoothness];
        rolls = new float[smoothness];

        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void resume() {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void pause() {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientationData[] = new float[3];
                SensorManager.getOrientation(R, orientationData);
                averageAzimuth = addValue(orientationData[0], azimuthes);
                averagePitch = addValue(orientationData[1], pitches);
                averageRoll = addValue(orientationData[2], rolls);
                screen_orientation = calculateScreenOrientation();

                if (screen_orientation != old_screen_orientation) {
                    if (listener != null) {
                        postDelayHandler.post(orientationChanged);
                    } else {
                        old_screen_orientation = screen_orientation;
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }


    public Orientation getOrientation() {
        return new Orientation(averageAzimuth, averagePitch, averageRoll);
    }

    public ScreenOrientation getScreenOrientation() {
        return screen_orientation;
    }

    public void setScreenOrientationChangedListener(ScreenOrientationChangeListener listener) {
        this.listener = listener;
    }

    public void setSmoothness(int smoothness) {
        this.smoothness = smoothness;
        pitches = new float[smoothness];
        azimuthes = new float[smoothness];
        rolls = new float[smoothness];
    }

    private float addValue(float value, float[] values) {
        value = (float) Math.round((Math.toDegrees(value)));
        float average = 0;
        for (int i = 1; i < smoothness; i++) {
            values[i - 1] = values[i];
            average += values[i];
        }
        values[smoothness - 1] = value;
        average = (average + value) / smoothness;
        return average;
    }

    private ScreenOrientation calculateScreenOrientation() {
        // finding local screen_orientation dip
        if (((screen_orientation == ScreenOrientation.Portrait || screen_orientation == ScreenOrientation.PortraitReverse)
                && (averageRoll > -30 && averageRoll < 30))) {
            if (averagePitch > 0)
                return ScreenOrientation.PortraitReverse;
            else
                return ScreenOrientation.Portrait;
        } else {
            // divides between all orientations
            if (Math.abs(averagePitch) >= 30) {
                if (averagePitch > 0)
                    return ScreenOrientation.PortraitReverse;
                else
                    return ScreenOrientation.Portrait;
            } else {
                if (averageRoll > 0) {
                    return ScreenOrientation.LandscapeReverse;
                } else {
                    return ScreenOrientation.Landscape;
                }
            }
        }
    }


    private Runnable orientationChanged = new Runnable() {
        @Override
        public void run() {
            if (screen_orientation != old_screen_orientation) {
                old_screen_orientation = screen_orientation;

                if (listener != null) {
                    listener.onOrientationChange(getScreenOrientation());
                }
            }
        }
    };


    public static class Orientation implements Parcelable {
        public static final Parcelable.Creator<Orientation> CREATOR = new Parcelable.Creator<Orientation>() {
            @Override
            public Orientation createFromParcel(Parcel source) {
                return new Orientation(source);
            }

            @Override
            public Orientation[] newArray(int size) {
                return new Orientation[size];
            }
        };

        private Float azimuth;
        private Float pitch;
        private Float roll;

        public Orientation(Float azimuth, Float pitch, Float roll) {
            this.pitch = pitch;
            this.azimuth = azimuth;
            this.roll = roll;
        }

        public Orientation(Parcel parcel) {
            azimuth = ParcelTools.readNFloat(parcel);
            pitch = ParcelTools.readNFloat(parcel);
            roll = ParcelTools.readNFloat(parcel);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            ParcelTools.writeNFloat(dest, azimuth);
            ParcelTools.writeNFloat(dest, pitch);
            ParcelTools.writeNFloat(dest, roll);
        }

        public void setAzimuth(Float azimuth) {
            this.azimuth = azimuth;
        }

        public void setPitch(Float pitch) {
            this.pitch = pitch;
        }

        public void setRoll(Float roll) {
            this.roll = roll;
        }
        
        public Float getAzimuth() {
            return azimuth;
        }

        public Float getRationalAzimuth() {
            return azimuth % 360;
        }

        public Float getPitch() {
            return pitch;
        }

        public Float getRationalPitch() {
            return pitch % 360;
        }

        public Float getRoll() {
            return roll;
        }

        public Float getRationalRoll() {
            return roll % 360;
        }
    }


    public interface ScreenOrientationChangeListener {
        void onOrientationChange(ScreenOrientation orientation);
    }
}