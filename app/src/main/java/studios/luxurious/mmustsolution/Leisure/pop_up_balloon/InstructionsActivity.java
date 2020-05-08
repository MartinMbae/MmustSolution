package studios.luxurious.mmustsolution.Leisure.pop_up_balloon;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.R;

/**
 * <h1>InstructionsActivity</h1>
 *
 * <p><b>InstructionsActivity</b> is responsible for displaying user game instructions.</p>
 */
public class InstructionsActivity extends AppCompatActivity {
    private Animation animation;

    /**
     * This method is responsible for configurations of game start screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see Activity#onCreate(Bundle)
     * @see Animation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        setToFullScreen();
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        animation.setDuration(100);
        findViewById(R.id.activity_instructions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstructionsActivity.this.setToFullScreen();
            }
        });

        findViewById(R.id.btn_back_instructions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                InstructionsActivity.this.finish();
            }
        });
    }

    /**
     * This method is responsible to transfer MainActivity_BalloonPop into fullscreen mode.
     */
    private void setToFullScreen() {
        findViewById(R.id.activity_instructions).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    /**
     * This method is responsible to call method setToFullScreen().
     *
     * @see InstructionsActivity#setToFullScreen()
     * @see Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }
}