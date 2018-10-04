package azumuta.wouterrappe.workinstructions;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ZoomImageView extends View {
    private Drawable drawable;

    private int top, bottom, right, left;
    private int image_width, image_height;
    private int canvas_width, canvas_height;
    private int panX, panY;
    private float scale;

    private final float scaleFactor = 0.1f;
    private final float scrollDeltaTreshold = 10.0f;
    private final float rotationPanFactor = 300.0f;


    public ZoomImageView(Context context, int drawableID) {
        super(context);
        drawable = getResources().getDrawable(drawableID);

        init();
    }

    // Set image bounds to show full image as large as possible within the display metrics of the screen
    private void init() {
        this.scale = 1.0f;
        this.panX = 0;
        this.panY = 0;

        this.image_width = drawable.getIntrinsicWidth();
        this.image_height = drawable.getIntrinsicHeight();

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        this.canvas_width = metrics.widthPixels;
        this.canvas_height = metrics.heightPixels;

        double zoomX = (double) canvas_width / image_width;
        double zoomY = (double) canvas_height / image_height;

        if(zoomY > zoomX) {                                            // scaled to a width of MAX_WIDTH
            double zoomFactor = (double) canvas_width / image_width;
            double targetHeight = image_height * zoomFactor;
            this.top = (int) ((canvas_height / 2) - (targetHeight / 2));
            this.bottom = (int) ((canvas_height / 2) + (targetHeight / 2));
            this.left = 0;
            this.right = canvas_width;
        } else {                                                                    // scaled to a height of MAX_HEIGHT
            double zoomFactor = (double) canvas_height / image_height;
            double targetWidth = image_width * zoomFactor;
            this.left = (int) ((canvas_width / 2) - (targetWidth / 2));
            this.right = (int) ((canvas_width / 2) + (targetWidth / 2));
            this.top = 0;
            this.bottom = canvas_height;
        }

        Log.d("zoomview", "calculating initial drawing parameters...");
        Log.d("zoomview", String.format("image_width: %d, image_height: %d, max_width: %d, max_height, %d", this.image_width, this.image_height, this.canvas_width, this.canvas_height));
        Log.d("zoomview", String.format("top: %d, bottom: %d, left: %d, right: %d", this.top, this.bottom, this.left, this.right));

    }

    public void resetValues() {
        init();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("zoomview", String.format("drawing top: %d, bottom: %d, left: %d, right: %d", this.top, this.bottom, this.left, this.right));
        Log.d("zoomview", String.format("scale: %f, panX: %d", this.scale, this.panX));


        drawable.setBounds(this.left + this.panX, this.top + this.panY, this.right + this.panX, this.bottom + this.panY);
        canvas.scale(this.scale, this.scale, getWidth()/2, getHeight()/2);

        drawable.draw(canvas);

    }

    public void handleScroll(float displacement, float delta, float velocity) {
        Log.d("zoomview", String.format("SCROLL: %f %f %f", displacement, delta, velocity));

        if(Math.abs(delta) > this.scrollDeltaTreshold) {
            if (velocity > 0) {
                this.scale *= (1.0f + this.scaleFactor);
            } else {
                this.scale *= (1.0f - this.scaleFactor);
            }
        }

        invalidate();
    }

    public void handleRotation(float deltaAngleX, float deltaAngleY) {
        Log.d("zoomview", "DELTA_ANGLE: " + deltaAngleY);

        boolean invalidateFlag = false;
        if(isZoomedIn() && (this.right - this.left) * this.scale > this.canvas_width) {
            this.panX += (int) (deltaAngleY * this.scale * this.rotationPanFactor);
            invalidateFlag = true;
        }

        if(isZoomedIn() && (this.bottom - this.top) * this.scale > this.canvas_height) {
            this.panY += (int) (deltaAngleX * this.scale * this.rotationPanFactor);
            invalidateFlag = true;
        }

        if(invalidateFlag) invalidate();
    }

    public boolean isZoomedIn() {
        return this.scale > 1;
    }
}