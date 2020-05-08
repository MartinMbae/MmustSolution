package studios.luxurious.mmustsolution.Leisure.Game1;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class Game_Activity_1 extends AppCompatActivity implements RewardedVideoAdListener {

    // Frame
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;

    // Image
    private ImageView box, black, orange, pink;
    private Drawable imageBoxRight, imageBoxLeft;

    boolean canEndGame = false;

    // Size
    private int boxSize;

    private Toast mainToast;

    // Position
    private float boxX, boxY;
    private float blackX, blackY;
    private float orangeX, orangeY;
    private float pinkX, pinkY;

    // Score
    private TextView scoreLabel, highScoreLabel;
    private int score, highScore, timeCount;

    // Class
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;
    
    int topScoreValue = 0;

    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean pink_flg = false;

    private int rewarded_times = 0;
    private Button startGameBtn;
    SharedPref sharedPref;
    MaterialDialog loadingAdDialog, watchAdDialog;

    RewardedVideoAd mAd;
    Boolean load_ad_immediately = false;
    Boolean has_been_rewarded = false;

    AdView adView;

    DatabaseReference highscoreReference;
    FirebaseAuth mAuth;

    InterstitialAd interstitialAd;

    int coinBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        soundPlayer = new SoundPlayer(this);
        sharedPref = new SharedPref(Game_Activity_1.this);

        gameFrame = findViewById(R.id.gameFrame);
        startLayout = findViewById(R.id.startLayout);
        box = findViewById(R.id.box);
        black = findViewById(R.id.black);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);

        startGameBtn = findViewById(R.id.start_game_btn);


        startGameBtn.setEnabled(false);

        imageBoxLeft = getResources().getDrawable(R.drawable.box_left);
        imageBoxRight = getResources().getDrawable(R.drawable.box_right);


        mAuth = FirebaseAuth.getInstance();
        highscoreReference = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("Catch_the_ball").child(mAuth.getCurrentUser().getUid()).child("highScores");
        loadHighscoresandCoinBalance("Please wait...", "Fetching your game progress. \nThis will take about half a minute.");


        FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("Catch_the_ball").child(mAuth.getCurrentUser().getUid()).child("uid").setValue(mAuth.getCurrentUser().getUid());



        highscoreReference.keepSynced(true);


        MobileAds.initialize(Game_Activity_1.this,
                getResources().getString(R.string.app_id));


        mAd = MobileAds.getRewardedVideoAdInstance(Game_Activity_1.this);
        mAd.setRewardedVideoAdListener(Game_Activity_1.this);

        mAd.loadAd(getResources().getString(R.string.rewarded_video_game_1), new AdRequest.Builder().build());


        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_game1));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                interstitialAd.loadAd(new AdRequest.Builder().build());

            }
        });

        adView = findViewById(R.id.ad);
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }

        });

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(0, 0);
            }
        });
    }

    public void changePos() {

        timeCount += 20;
        orangeY += 12;

        float orangeCenterX = orangeX + orange.getWidth() / 2;
        float orangeCenterY = orangeY + orange.getHeight() / 2;

        if (hitCheck(orangeCenterX, orangeCenterY)) {
            orangeY = frameHeight + 100;
            score += 10;
            soundPlayer.playHitOrangeSound();
        }

        if (orangeY > frameHeight) {
            orangeY = -100;
            orangeX = (float) Math.floor(Math.random() * (frameWidth - orange.getWidth()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // Pink
        if (!pink_flg && timeCount % 10000 == 0) {
            pink_flg = true;
            pinkY = -20;
            pinkX = (float) Math.floor(Math.random() * (frameWidth - pink.getWidth()));
        }

        if (pink_flg) {
            pinkY += 20;

            float pinkCenterX = pinkX + pink.getWidth() / 2;
            float pinkCenterY = pinkY + pink.getWidth() / 2;

            if (hitCheck(pinkCenterX, pinkCenterY)) {
                pinkY = frameHeight + 30;
                score += 30;
                // Change FrameWidth
                if (initialFrameWidth > frameWidth * 110 / 100) {
                    frameWidth = frameWidth * 110 / 100;
                    changeFrameWidth(frameWidth);
                }
                soundPlayer.playHitPinkSound();
            }

            if (pinkY > frameHeight) pink_flg = false;
            pink.setX(pinkX);
            pink.setY(pinkY);
        }

        // Black
        blackY += 18;

        float blackCenterX = blackX + black.getWidth() / 2;
        float blackCenterY = blackY + black.getHeight() / 2;

        if (hitCheck(blackCenterX, blackCenterY)) {
            blackY = frameHeight + 100;

            // Change FrameWidth
            frameWidth = frameWidth * 80 / 100;
            changeFrameWidth(frameWidth);
            soundPlayer.playHitBlackSound();
            if (frameWidth <= boxSize) {
                gameOver();
            }

        }

        if (blackY > frameHeight) {
            blackY = -100;
            blackX = (float) Math.floor(Math.random() * (frameWidth - black.getWidth()));
        }

        black.setX(blackX);
        black.setY(blackY);

        // Move Box
        if (action_flg) {
            // Touching
            boxX += 14;
            box.setImageDrawable(imageBoxRight);
        } else {
            // Releasing
            boxX -= 14;
            box.setImageDrawable(imageBoxLeft);
        }

        // Check box position.
        if (boxX < 0) {
            boxX = 0;
            box.setImageDrawable(imageBoxRight);
        }
        if (frameWidth - boxSize < boxX) {
            boxX = frameWidth - boxSize;
            box.setImageDrawable(imageBoxLeft);
        }

        box.setX(boxX);

        scoreLabel.setText("Score : " + score);

    }

    public boolean hitCheck(float x, float y) {
        if (boxX <= x && x <= boxX + boxSize &&
                boxY <= y && y <= frameHeight) {
            return true;
        }
        return false;
    }

    public void changeFrameWidth(int frameWidth) {
        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);
    }


    public void gameOver() {

        timer.cancel();
        timer = null;
        start_flg = false;

        if (rewarded_times < 2) {
            WatchAdAndContinue();
        } else {
            endGame();
        }
    }


    private void endGame() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);
        startLayout.setVisibility(View.VISIBLE);
        box.setVisibility(View.INVISIBLE);
        black.setVisibility(View.INVISIBLE);
        orange.setVisibility(View.INVISIBLE);
        pink.setVisibility(View.INVISIBLE);


        canEndGame = true;

        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);
            highscoreReference.setValue(score);
            
            if (score > topScoreValue){

                Toast.makeText(this, "We have notified all players about your new high score. You're the king", Toast.LENGTH_SHORT).show();


                String name;
                if (sharedPref.getIsGuest()){

                    name = sharedPref.getGuestUsername();

                }else {

                    name = sharedPref.getPortalFullName();
                }

                String message = name + " has just set a new record in \"Catch The Ball\" game by scoring "+ score + " points. \nYou could be the next top scorer. Open Mmust App and try your luck.";
                String title = "New High Scores";

                sendNotification(title, message);
            }

        }
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private void sendNotification(final String title, final String message) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body",message);
                    dataJson.put("title",title);
                    json.put("notification",dataJson);
                    json.put("to","/topics/AllNew");
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+ Constants.LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    final String finalResponse = response.body().string();

                }catch (final Exception e){}
                return null;
            }
        }.execute();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;

            }
        }
        return true;
    }

    public void startGame(int current_score, int time_count) {

        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE);

        if (current_score == 0 && time_count == 0) {
            rewarded_times = 0;
        }

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            boxSize = box.getHeight();
            boxX = box.getX();
            boxY = box.getY();
        }

        frameWidth = initialFrameWidth;

        box.setX(0.0f);
        black.setY(3000.0f);
        orange.setY(3000.0f);
        pink.setY(3000.0f);

        blackY = black.getY();
        orangeY = orange.getY();
        pinkY = pink.getY();

        box.setVisibility(View.VISIBLE);
        black.setVisibility(View.VISIBLE);
        orange.setVisibility(View.VISIBLE);
        pink.setVisibility(View.VISIBLE);

        timeCount = time_count;
        score = current_score;
        scoreLabel.setText("Score : " + score);


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }

    public void quitGame(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }


    public void WatchAdAndContinue() {


        watchAdDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_blueprint_message_title_three_btn, false)
                .cancelable(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();

        View view = watchAdDialog.getCustomView();

        TextView messageText = view.findViewById(R.id.message);
        TextView title = view.findViewById(R.id.title);
        RelativeLayout cancelLayout = view.findViewById(R.id.cancel);
        RelativeLayout okLayout = view.findViewById(R.id.ok);
        RelativeLayout middleLayout = view.findViewById(R.id.middle_layout);
        TextView canceltext = view.findViewById(R.id.canceltext);
        TextView oktext = view.findViewById(R.id.oktext);
        TextView middleText = view.findViewById(R.id.middle_text);

        messageText.setText("Redeem 5 coins or watch an advert to continue. \nYour current coin balance is " + coinBalance);
        title.setText("Continue");
        oktext.setText("Watch video");
        middleText.setText("Redeem coins");
        canceltext.setText("I give up");

        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);

        middleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(40);
                }
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (coinBalance >= 5) {
                            updateCoins(coinBalance - 5);
                            continueGameAfterReward();

                        } else {
                            showToast("You do not have enough coins ro redeem");
                        }

                        watchAdDialog.dismiss();


                    }
                }, 200);
            }
        });

        okLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(40);
                }
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadAnddisplayAd("Please wait", "Loading rewarded video");
                        watchAdDialog.dismiss();
                    }
                }, 200);
            }
        });

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(40);
                }
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        endGame();
                        watchAdDialog.dismiss();
                    }
                }, 200);
            }
        });

        try {
            watchAdDialog.show();
        } catch (Exception ignored) {
        }

    }


    private void updateCoins(final int coins) {

        String coinsString = String.valueOf(coins);
        Map<String, Object> user = new HashMap<>();
        user.put("coins", coinsString);

        FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getUid())
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        coinBalance = coins;
                        Toast.makeText(Game_Activity_1.this, "You've redeemed 5 coins", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                    }
                });


    }


    public void loadAnddisplayAd(String titlee, String message) {

        loadingAdDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_blueprint_gif, false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();

        View view = loadingAdDialog.getCustomView();
        TextView messageText = view.findViewById(R.id.message);
        TextView title = view.findViewById(R.id.title);
        title.setText(titlee);
        messageText.setText(message);

        RelativeLayout cancelLayout = view.findViewById(R.id.cancel);
        TextView canceltext = view.findViewById(R.id.canceltext);

        canceltext.setText("Cancel");



        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);

        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(40);
                }
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        endGame();
                        loadingAdDialog.dismiss();
                    }
                }, 200);
            }
        });

        try {
            loadingAdDialog.show();
        } catch (Exception ignored) {
        }

        if (mAd.isLoaded()) {
            loadingAdDialog.dismiss();
            mAd.show();
        } else {
            load_ad_immediately = true;
        }

    }


    public void loadHighscoresandCoinBalance(String titlee, String message) {

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
                                    highScore = Integer.parseInt(dataSnapshot.getValue().toString());

                                } else {
                                    highScore = 0;
                                }


                                highScoreLabel.setText("High Score : " + highScore);


                                final DatabaseReference topScore = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("Catch_the_ball");

                                topScore.orderByChild("highScores").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot){
                                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {

                                            if (childSnapshot.hasChild("highScores")) {
                                                topScoreValue = Integer.parseInt(childSnapshot.child("highScores").getValue().toString());
                                            }else {
                                                topScoreValue = 0;

                                            }
                                            loadingAdDialog.dismiss();
                                            startGameBtn.setEnabled(true);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                       // throw databaseError.toException(); // don't swallow errors
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                loadingAdDialog.dismiss();
                                Toast.makeText(Game_Activity_1.this, "Something wrong happened.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });


                    } catch (Exception ignored) {
                    }
                }


            }
        });


    }


    @Override
    public void onRewardedVideoAdLoaded() {
        if (load_ad_immediately) {
            mAd.show();
            if (loadingAdDialog != null) {
                if (loadingAdDialog.isShowing()) {
                    loadingAdDialog.dismiss();
                }
            }
        }

    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
        has_been_rewarded = false;
    }

    @Override
    public void onRewardedVideoAdClosed() {

        if (has_been_rewarded) {
            continueGameAfterReward();
            load_ad_immediately = false;

        } else {
            endGame();
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        load_ad_immediately = false;
        has_been_rewarded = true;
        mAd.loadAd(getResources().getString(R.string.rewarded_video_game_1), new AdRequest.Builder().build());

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        mAd.loadAd(getResources().getString(R.string.rewarded_video_game_1), new AdRequest.Builder().build());

    }

    @Override
    public void onRewardedVideoCompleted() {
    }


    @Override
    protected void onDestroy() {
        try {

            mAd.destroy(this);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        try {

            mAd.resume(this);
        } catch (Exception ignored) {
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            mAd.pause(this);
        } catch (Exception ignored) {
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (canEndGame) {
            super.onBackPressed();

            if (loadingAdDialog != null) {
                if (loadingAdDialog.isShowing()) {
                    loadingAdDialog.dismiss();
                    endGame();
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            }
        }
    }

    private void continueGameAfterReward() {

        final FrameLayout frameLayout = findViewById(R.id.gameFrame);
        frameLayout.setAlpha(0.3f);

        final LinearLayout resumeLayout = findViewById(R.id.resumeLayout);
        resumeLayout.setVisibility(View.VISIBLE);


        final TextView resumeText = findViewById(R.id.resumeCountDown);
        resumeText.setText("3");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                resumeText.setText("2");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        resumeText.setText("1");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                frameLayout.setAlpha(1);
                                resumeLayout.setVisibility(View.GONE);
                                frameWidth = initialFrameWidth;
                                changeFrameWidth(frameWidth);
                                rewarded_times++;
                                startGame(score, timeCount);


                            }
                        }, 1000);


                    }
                }, 1000);


            }
        }, 1000);


    }

    private void showToast(String p_strMessage) {
        if (mainToast == null) {
            mainToast = Toast.makeText(Game_Activity_1.this, "", Toast.LENGTH_SHORT);
        }

        mainToast.setText(p_strMessage);
        mainToast.show();
    }

    public void Instructions(View view) {

        startActivity(new Intent(Game_Activity_1.this, InstructionsActivityGame1.class));
    }
}
