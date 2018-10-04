package azumuta.wouterrappe.workinstructions;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static final String IMAGE_IDS = "IMAGE_IDS";
    public static final String IMAGE_ID = "IMAGE_ID";

    private List<CardBuilder> firstLevelCards;
    private CardScrollView mCardScrollView;
    private MyCardScrollAdapter mAdapter;

    private List<WorkInstruction> instructions = new ArrayList<WorkInstruction>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupWorkInstructions();
        createFirstLevelCards();

        mCardScrollView = new CardScrollView(this);
        mAdapter = new MyCardScrollAdapter();

        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();

        setupClickListener();

        setContentView(mCardScrollView);
    }

    private void setupWorkInstructions() {
        instructions.add(new WorkInstruction("Take the back from from trolley and place the frame into the airbar fixture", R.drawable.image_1_1, R.drawable.image_1_2));
        instructions.add(new WorkInstruction("Scan selex", R.drawable.image_2_1));
        instructions.add(new WorkInstruction("Assemble map pocket retainer Y413/Y283", R.drawable.image_3_1));
        instructions.add(new WorkInstruction("Assemble extra map pocket retainer Y413/Y283", R.drawable.image_4_1));
        instructions.add(new WorkInstruction("Screw the grounding cable on torque", R.drawable.image_5_1, R.drawable.image_5_2));
        instructions.add(new WorkInstruction("Assemble the lumbar console ( adjuster lumbar mat )", R.drawable.image_6_1, R.drawable.image_6_2, R.drawable.image_6_3));
        instructions.add(new WorkInstruction("Connect motor lumbar", R.drawable.image_7_1, R.drawable.image_7_2));
        instructions.add(new WorkInstruction("Assemble 4 hooks of the lumbar mat onto the backrest frame (without lumbar)", R.drawable.image_8_1));
        instructions.add(new WorkInstruction("Assemble 4 hooks of the lumbar mat onto the backrest frame", R.drawable.image_9_1));
        instructions.add(new WorkInstruction("Take and assemble two bushings EUR into the backrest frame", R.drawable.image_10_1, R.drawable.image_10_2, R.drawable.image_10_3));
    }

    private void createFirstLevelCards() {
        firstLevelCards = new ArrayList<CardBuilder>();

        for(WorkInstruction instruction : instructions) {
            firstLevelCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)
                    .setText(instruction.getTitle())
                    .showStackIndicator(true)
                    .setFootnote(instruction.getImages().size() + " images")
            );
        }
    }

    private void setupClickListener() {
        final Context context = this;

        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.TAP);

                int imageCount = instructions.get(position).getImages().size();
                if(imageCount > 1) {
                    Intent intent = new Intent(context, WorkInstructionImagesActivity.class);
                    intent.putExtra(IMAGE_IDS, instructions.get(position).getImages());
                    startActivity(intent);
                }
                else if(imageCount == 1) {
                    Intent intent = new Intent(context, SingleImageActivity.class);
                    intent.putExtra(IMAGE_ID, instructions.get(position).getImages().get(0));
                    startActivity(intent);
                }
            }
        });
    }

    private class MyCardScrollAdapter extends CardScrollAdapter {
        @Override
        public int getCount() {
            return firstLevelCards.size();
        }

        @Override
        public Object getItem(int i) {
            return firstLevelCards.get(i);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return firstLevelCards.get(i).getView(view, viewGroup);
        }

        @Override
        public int getPosition(Object o) {
            return firstLevelCards.indexOf(o);
        }

        @Override
        public int getViewTypeCount() {
            return CardBuilder.getViewTypeCount();
        }

        @Override
        public int getItemViewType(int position) {
            return firstLevelCards.get(position).getItemViewType();
        }
    }
}
