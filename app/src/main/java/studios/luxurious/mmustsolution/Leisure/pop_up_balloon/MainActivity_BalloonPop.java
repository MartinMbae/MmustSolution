package studios.luxurious.mmustsolution.Leisure.pop_up_balloon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import studios.luxurious.mmustsolution.R;


public class MainActivity_BalloonPop extends AppCompatActivity {
    public static final String SOUND = "SOUND", MUSIC = "MUSIC";
    private boolean mMusic = true, mSound = true;
    private TextView highScore;
    private Animation animation;
    private ImageButton btnLeaderboard, btnAchievements;


    MaterialDialog loadingAdDialog;
    DatabaseReference highscoreReference;
    FirebaseAuth mAuth;
    int highScoreInt = 0;

    int coinBalance,topScoreValue = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pop);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        setToFullScreen();
        Button btnStart = findViewById(R.id.btn_start);
        Button btnInstructions = findViewById(R.id.btn_instructions);
        final ImageButton btnMusic = findViewById(R.id.btn_music);
        final ImageButton btnSound = findViewById(R.id.btn_sound);
        btnLeaderboard = findViewById(R.id.btn_leaderboard);
        btnAchievements = findViewById(R.id.btn_achievements);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        animation.setDuration(100);
        highScore = findViewById(R.id.high_score);


        mAuth = FirebaseAuth.getInstance();
        highscoreReference = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("balloon_pop").child(mAuth.getCurrentUser().getUid()).child("highScores");




        boolean refresh = getIntent().getBooleanExtra("refresh",true);

        if (refresh){
            loadHighscores("Please wait...", "Fetching your game progress. \nThis will take about half a minute.");


        }else {

            int coins = getIntent().getIntExtra("coins",0);
            int highscore = getIntent().getIntExtra("highscore",0);
            highScore.setText(String.valueOf(highscore));
            highScoreInt = highscore;

            coinBalance = coins;

        }


        highscoreReference.keepSynced(true);

        btnInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity_BalloonPop.this.startActivity(new Intent(MainActivity_BalloonPop.this.getApplicationContext(), InstructionsActivity.class));
            }
        });
        findViewById(R.id.activity_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity_BalloonPop.this.setToFullScreen();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity_BalloonPop.this.getApplicationContext(), GameplayActivity.class);
                intent.putExtra(SOUND, mSound);
                intent.putExtra(MUSIC, mMusic);
                intent.putExtra("highScore", highScoreInt);
                intent.putExtra("coinBalance", coinBalance);
                intent.putExtra("topScoreValue", topScoreValue);
                MainActivity_BalloonPop.this.startActivity(intent);
                finish();
            }
        });

        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMusic) {
                    mMusic = false;
                    btnMusic.setBackgroundResource(R.drawable.music_note_off);
                } else {
                    mMusic = true;
                    btnMusic.setBackgroundResource(R.drawable.music_note);
                }
            }
        });

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSound) {
                    mSound = false;
                    btnSound.setBackgroundResource(R.drawable.volume_off);
                } else {
                    mSound = true;
                    btnSound.setBackgroundResource(R.drawable.volume_up);
                }
            }
        });

        btnAchievements.setVisibility(View.GONE);

        btnLeaderboard.setVisibility(View.GONE);


    }

    /**
     * This method is responsible to transfer MainActivity_BalloonPop into fullscreen mode.
     */
    private void setToFullScreen() {
        findViewById(R.id.activity_start).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    public void loadHighscores(String titlee, String message) {

        loadingAdDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_blueprint_gif, false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();

        View view = loadingAdDialog.getCustomView();
        TextView messageText = view.findViewById(R.id.message);
        TextView title = view.findViewById(R.id.title);
        title.setText(titlee);
        messageText.setText(message);

        RelativeLayout cancelLayout = view.findViewById(R.id.cancel);
        cancelLayout.setVisibility(View.GONE);

        try {
            loadingAdDialog.show();
        } catch (Exception ignored) {
        }


        String uid = mAuth.getCurrentUser().getUid();
        
//        if (Utils.IsConnected(MainActivity_BalloonPop.this)) {
        if (true) {

            FirebaseFirestore.getInstance().collection("Users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    if (documentSnapshot != null) {
                        String coins_from_db = documentSnapshot.getString("coins");
                        try {
                            if (coins_from_db != null)
                                coinBalance = Integer.parseInt(coins_from_db);


                            highscoreReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    if (dataSnapshot.exists()) {
                                        highScoreInt = Integer.parseInt(dataSnapshot.getValue().toString());

                                    } else {
                                        highScoreInt = 0;
                                    }
                                    highScore.setText(String.valueOf(highScoreInt));

                                    final DatabaseReference topScore = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("balloon_pop");

                                    topScore.orderByChild("highScores").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()) {

                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {


                                                    if (childSnapshot.hasChild("highScores")) {
                                                        topScoreValue = Integer.parseInt(childSnapshot.child("highScores").getValue().toString());
                                                    } else {
                                                        topScoreValue = 0;

                                                    }

                                                    loadingAdDialog.dismiss();


                                                }
                                            }else {


                                                loadingAdDialog.dismiss();

                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            throw databaseError.toException(); // don't swallow errors
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    loadingAdDialog.dismiss();
                                    Toast.makeText(MainActivity_BalloonPop.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                        } catch (Exception ex) {
                            loadingAdDialog.dismiss();
                        }
                    }


                }
            });

        }else {

            Toast.makeText(this, "You are not connected to the internet. Your progress will not be updated", Toast.LENGTH_SHORT).show();
        }

    }



}