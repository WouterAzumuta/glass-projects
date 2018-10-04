package azumuta.wouterrappe.workinstructions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;

public class WorkInstructionImagesActivity extends Activity {

    private ArrayList<Integer> mImageIds;
    private ArrayList<View> mCards;

    private CardScrollView mCardScrollView;
    private CardScrollAdapter mCardScrollAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCards();

        final Context context = this;

        mCardScrollView = new CardScrollView(this);
        mCardScrollAdapter = new CardScrollAdapter() {
            @Override
            public int getCount() {
                return mCards.size();
            }

            @Override
            public Object getItem(int i) {
                return mCards.get(i);
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                return mCards.get(i);
            }

            @Override
            public int getPosition(Object o) {
                return mCards.indexOf(o);
            }
        };

        mCardScrollView.setAdapter(mCardScrollAdapter);
        mCardScrollView.activate();

        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.TAP);

                Intent intent = new Intent(context, SingleImageActivity.class);
                intent.putExtra(MainActivity.IMAGE_ID, mImageIds.get(position));
                startActivity(intent);
            }
        });

        setContentView(mCardScrollView);
    }

    private void createCards() {
        Intent intent = getIntent();
        mImageIds = intent.getIntegerArrayListExtra(MainActivity.IMAGE_IDS);

        mCards = new ArrayList<View>(mImageIds.size());

        for(Integer id : mImageIds) {
            mCards.add(getSingleImageView(id));
            //mCards.add(getZoomableImageView(id));
        }
    }

    private View getSingleImageView(int imageId) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_image_layout, null);

        ImageView imgView = (ImageView) view.findViewById(R.id.image);
        imgView.setImageResource(imageId);

        return view;
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(Sounds.DISMISSED);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }*/

}
