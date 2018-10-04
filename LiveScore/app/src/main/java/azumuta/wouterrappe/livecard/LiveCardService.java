package azumuta.wouterrappe.livecard;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */


/**
 * See this link for further implementation:             https://developers.google.com/glass/develop/gdk/live-cards
 */

public class LiveCardService extends Service {

    private static final String LIVE_CARD_TAG = "LiveCardService";

    private LiveCard mLiveCard;
    private RemoteViews mLiveCardView;

    private int homeScore, awayScore;
    private Random mPointsGenerator;

    private final Handler mHandler = new Handler();
    private final UpdateLiveCardRunnable mUpdateLiveCardRunnable = new UpdateLiveCardRunnable();
    private static final long DELAY_MILLIS = 5000;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mPointsGenerator = new Random();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {

            // get instance of a live card
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            //inflate a layout into a remote view
            mLiveCardView = new RemoteViews(getPackageName(), R.layout.live_card);

            //Set up initial RemoteViews values
            this.homeScore = 0;
            this.awayScore = 0;
            mLiveCardView.setTextViewText(R.id.home_team_name_text_view, getString(R.string.home_team));
            mLiveCardView.setTextViewText(R.id.away_team_name_text_view, getString(R.string.away_team));
            mLiveCardView.setTextViewText(R.id.footer_text, getString(R.string.game_quarter));

            // Set up the live card's action with a pending intent
            // to show a menu when tapped
            Intent menuIntent = new Intent(this, MenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

            // Publish the live card
            mLiveCard.publish(PublishMode.REVEAL); // or SILENT

            // Queue the update text runnable
            mHandler.post(mUpdateLiveCardRunnable);
        } else {
            mLiveCard.navigate();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            //Stop the handler from queuing more Runnable jobs
            mUpdateLiveCardRunnable.setStop(true);
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }


    // for interprocess communication, use binder objects. Not needed here so null is returned
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class UpdateLiveCardRunnable implements Runnable {

        private boolean mIsStopped = false;

        @Override
        public void run() {
            if(!isStopped()) {
                // Generate fake points
                homeScore += mPointsGenerator.nextInt(5);
                awayScore += mPointsGenerator.nextInt(3);

                // Update remote view
                mLiveCardView.setTextViewText(R.id.home_score_text_view, String.valueOf(homeScore));
                mLiveCardView.setTextViewText(R.id.away_score_text_view, String.valueOf(awayScore));

                // Always call setViews() to update the live card's RemoteViews
                mLiveCard.setViews(mLiveCardView);

                // Next score update in ... milliseconds
                mHandler.postDelayed(mUpdateLiveCardRunnable, DELAY_MILLIS);
            }
        }

        public boolean isStopped() {
            return this.mIsStopped;
        }

        public void setStop(boolean isStopped) {
            this.mIsStopped = isStopped;
        }
    }
}
