package azumuta.wouterrappe.workinstructions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class SingleImageActivity extends Activity implements SensorEventListener{
    private SensorManager mSensorManager;
    private Sensor mGyroscope;
    private GestureDetector mGestureDetector;

    private ZoomImageView mZoomImageView;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGestureDetector = createGestureDetector(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        Intent intent = getIntent();
        int id = intent.getIntExtra(MainActivity.IMAGE_ID, -1);

        mZoomImageView = new ZoomImageView(this, id);
        setContentView(mZoomImageView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(mZoomImageView != null) {
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;

                float vx = event.values[0];
                float vy = event.values[1];

                float deltaAngleX = vx * dT;
                float deltaAngleY = vy * dT;

                mZoomImageView.handleRotation(deltaAngleX, deltaAngleY);
            }
            timestamp = event.timestamp;
        }
    }

    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // nothing to do
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //TODO also stop listening to sensor events when the image is not zoomed in
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                mZoomImageView.handleScroll(displacement, delta, velocity);
                return false;
            }
        });

        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if(gesture == Gesture.TWO_TAP) {
                    mZoomImageView.resetValues();
                    return true;
                }
                return false;
            }
        });

        return gestureDetector;
    }
}
