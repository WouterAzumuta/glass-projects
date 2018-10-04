package azumuta.wouterrappe.sensors;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;

public class MainActivity extends Activity implements SensorEventListener{

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

        mZoomImageView = new ZoomImageView(this, R.drawable.image_4_1);
        setContentView(mZoomImageView);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
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

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;

            float vx = event.values[0];
            float vy = event.values[1];

            float deltaAngleX = vx * dT;
            float deltaAngleY = vy * dT;

            mZoomImageView.handleRotation(deltaAngleX, deltaAngleY);

        }
        timestamp = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // nothing to do
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector != null && mGestureDetector.onMotionEvent(event);
    }

    /*
    private void listAvailableSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);


        StringBuilder sb = new StringBuilder();
        for(Sensor s : sensors) {
            sb.append(s.getName());
            sb.append("\n");
        }

        View view = new CardBuilder(this, CardBuilder.Layout.TEXT).setText(sb.toString()).getView();

        setContentView(view);
    }*/
}
