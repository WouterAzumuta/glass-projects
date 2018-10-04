package azumuta.wouterrappe.imagescroller;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    private final int CARD_COUNT = 16;

    private ArrayList<CardBuilder> mCards;
    private CardScrollView mCardScrollView;
    private MyCardScrollAdapter mCardScrollAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCards();

        mCardScrollView = new CardScrollView(this);
        mCardScrollAdapter = new MyCardScrollAdapter();

        mCardScrollView.setAdapter(mCardScrollAdapter);
        mCardScrollView.activate();

        setupClickListener();

        setContentView(mCardScrollView);
    }

    private void createCards() {
        mCards = new ArrayList<CardBuilder>();
        Random r = new Random();

        int[] backgrounds = {R.drawable.screenshot, R.drawable.screenshot2, R.drawable.blue, R.drawable.green, R.drawable.red, R.drawable.yellow};

        for(int i=0; i<CARD_COUNT; i++) {
            mCards.add(new CardBuilder(this, CardBuilder.Layout.TITLE)
                    //.setText("Image " + (i+1))
                    .addImage(backgrounds[i % backgrounds.length])
            );
        }
    }

    private class MyCardScrollAdapter extends CardScrollAdapter {
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
            return mCards.get(i).getView(view, viewGroup);
        }

        @Override
        public int getPosition(Object o) {
            return mCards.indexOf(o);
        }

        @Override
        public int getViewTypeCount() {
            return CardBuilder.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position){
            return mCards.get(position).getItemViewType();
        }
    }

    private void setupClickListener() {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.TAP);
            }
        });
    }
}
