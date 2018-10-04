package azumuta.wouterrappe.myapplication;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends Activity {

    private CardBuilder mCardBuilder;

    private int soundCounter = 0;

    private final int[] sounds = {
            Sounds.DISMISSED,
            Sounds.DISALLOWED,
            Sounds.ERROR,
            Sounds.SELECTED,
            Sounds.SUCCESS,
            Sounds.TAP
    };

    private final String[] soundNames = {
            "DISMISSED",
            "DISALLOWED",
            "ERROR",
            "SELECTED",
            "SUCCESS",
            "TAP"
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mCardBuilder = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText(getString(R.string.play_sounds))
                .setFootnote("next: " + soundNames[soundCounter]);

        setContentView(mCardBuilder.getView());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.playSoundEffect(sounds[soundCounter]);
            soundCounter = (soundCounter + 1) % sounds.length;
            mCardBuilder.setFootnote("next: " + soundNames[soundCounter]);
            setContentView(mCardBuilder.getView());
        }
        return super.onKeyDown(keyCode, event);
    }
}
