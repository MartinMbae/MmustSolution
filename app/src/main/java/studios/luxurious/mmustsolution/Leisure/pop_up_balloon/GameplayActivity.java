package studios.luxurious.mmustsolution.Leisure.pop_up_balloon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import studios.luxurious.mmustsolution.Leisure.pop_up_balloon.utils.SoundHelper;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class GameplayActivity extends AppCompatActivity implements Balloon.BalloonListener, RewardedVideoAdListener {
    private static final int MIN_ANIMATION_DELAY = 500, MAX_ANIMATION_DELAY = 1500, MIN_ANIMATION_DURATION = 1000, MAX_ANIMATION_DURATION = 6000, NUMBER_OF_HEARTS = 5;
    private final Random mRandom = new Random();
    private final int[] mBalloonColors = {Color.YELLOW, Color.RED, Color.WHITE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.BLUE};
    private int mBalloonsPerLevel = 10, mBalloonsPopped, mScreenWidth, mScreenHeight, mLevel, mScore, mHeartsUsed;
    private boolean mPlaying, mSound, mMusic, mGame;
    private TextView mScoreDisplay, mLevelDisplay;
    private ViewGroup mContentView;
    private SoundHelper mSoundHelper, mMusicHelper;
    private final List<ImageView> mHeartImages = new ArrayList<>();
    private final List<Balloon> mBalloons = new ArrayList<>();
    private Animation mAnimation;


    private boolean pauseGame = false;
    int coinBalance;

    SharedPref sharedPref;

    private int rewarded_times = 0;

    RewardedVideoAd mAd;
    Boolean load_ad_immediately = false;
    Boolean has_been_rewarded = false;

    MaterialDialog watchAdDialog, loadingAdDialog;


    DatabaseReference highscoreReference;
    FirebaseAuth mAuth;
    int highScore,topScoreValue;

    InterstitialAd interstitialAd;
    AdView adView;

    /**
     * This method is responsible for configurations of gameplay screen.
     *
     * @param savedInstanceState Define potentially saved parameters due to configurations changes.
     * @see Activity#onCreate(Bundle)
     */
    @SuppressLint("FindViewByIdCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        mContentView = findViewById(R.id.activity_main);
        setToFullScreen();
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        mMusicHelper = new SoundHelper(this);
        mMusicHelper.prepareMusicPlayer(this);
        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();
        highscoreReference = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("balloon_pop").child(mAuth.getCurrentUser().getUid()).child("highScores");


        sharedPref = new SharedPref(this);
        highscoreReference.keepSynced(true);
        highScore = getIntent().getIntExtra("highScore", 0);
        coinBalance = getIntent().getIntExtra("coinBalance", 0);
        topScoreValue = getIntent().getIntExtra("topScoreValue", 0);


        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenWidth = mContentView.getWidth();
                    mScreenHeight = mContentView.getHeight();
                }
            });
        }

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameplayActivity.this.setToFullScreen();
            }
        });

        mScoreDisplay = findViewById(R.id.score_display);
        mLevelDisplay = findViewById(R.id.level_display);
        ImageView imageview_heart1 = findViewById(R.id.heart1);
        ImageView imageview_heart2 = findViewById(R.id.heart2);
        ImageView imageview_heart3 = findViewById(R.id.heart3);
        ImageView imageview_heart4 = findViewById(R.id.heart4);
        ImageView imageview_heart5 = findViewById(R.id.heart5);
        mHeartImages.add(imageview_heart1);
        mHeartImages.add(imageview_heart2);
        mHeartImages.add(imageview_heart3);
        mHeartImages.add(imageview_heart4);
        mHeartImages.add(imageview_heart5);
        updateDisplay();
        mSoundHelper = new SoundHelper(this);
        mSoundHelper.prepareMusicPlayer(this);
        mAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        mAnimation.setDuration(100);

        if (intent.hasExtra(MainActivity_BalloonPop.SOUND))
            mSound = intent.getBooleanExtra(MainActivity_BalloonPop.SOUND, true);

        if (intent.hasExtra(MainActivity_BalloonPop.MUSIC))
            mMusic = intent.getBooleanExtra(MainActivity_BalloonPop.MUSIC, true);


        InitializeAds();

        startgameCountDown();
    }


    public void InitializeAds() {
        MobileAds.initialize(this, getString(R.string.app_id));


        mAd = MobileAds.getRewardedVideoAdInstance(GameplayActivity.this);
        mAd.setRewardedVideoAdListener(GameplayActivity.this);

        mAd.loadAd(getResources().getString(R.string.rewarded_video_popBaloons), new AdRequest.Builder().build());


        adView = findViewById(R.id.ad);
        AdRequest adRequest = new AdRequest.Builder().build();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_balloon));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        adView.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(GameplayActivity.this, MainActivity_BalloonPop.class);
                intent.putExtra("refresh", false);
                intent.putExtra("coins", coinBalance);
                intent.putExtra("highscore", highScore);
                startActivity(intent);
                finish();
            }
        });


        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }


            @Override
            public void onAdClosed() {
                Intent intent = new Intent(GameplayActivity.this, MainActivity_BalloonPop.class);
                intent.putExtra("refresh", false);
                intent.putExtra("coins", coinBalance);
                intent.putExtra("highscore", highScore);
                startActivity(intent);
                finish();
            }

        });
    }


    /**
     * This method is responsible to transfer MainActivity_BalloonPop into fullscreen mode.
     */
    private void setToFullScreen() {
        findViewById(R.id.activity_main).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    /**
     * This method is responsible to continue play music when user back to the game.
     *
     * @see Activity#onRestart()
     * @see SoundHelper#playMusic()
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (mGame) {
            if (mMusic) mMusicHelper.playMusic();
        }
    }

    /**
     * This method is responsible to start game and set beginning game parameters.
     *
     * @see GameplayActivity#setToFullScreen()
     * @see SoundHelper#playMusic()
     * @see GameplayActivity#startLevel()
     */
    private void startGame() {
        setToFullScreen();
        mScore = 0;
        mLevel = 0;
        mHeartsUsed = 0;
        mGame = true;
        if (mMusic) mMusicHelper.playMusic();
        for (ImageView pin : mHeartImages) pin.setImageResource(R.drawable.heart);

        for (Balloon balloon : mBalloons) {
            balloon.allowpop = true;
        }

        pauseGame = false;
        startLevel();
    }

    private void startLevel() {
        mLevel++;
        updateDisplay();
        new BalloonLauncher().execute(mLevel);
        mPlaying = true;
        mBalloonsPopped = 0;


        for (Balloon balloon : mBalloons) {
            balloon.allowpop = true;
        }
    }

    /**
     * This method is responsible to finish current level.
     *
     * @see Toast#makeText(Context, int, int)
     */


    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        mBalloonsPopped++;
        if (mSound) mSoundHelper.playSound();
        mContentView.removeView(balloon);
        mBalloons.remove(balloon);
        if (userTouch) mScore++;
        else {
            mHeartsUsed++;
            if (mHeartsUsed <= mHeartImages.size())
                mHeartImages.get(mHeartsUsed - 1).setImageResource(R.drawable.broken_heart);
            if (mHeartsUsed == NUMBER_OF_HEARTS) {
                gameOver();
                return;
            }
        }

        updateDisplay();

        if (mBalloonsPopped == mBalloonsPerLevel) {
            mBalloonsPerLevel += 10;
            Toast.makeText(this, getString(R.string.finish_level) + mLevel, Toast.LENGTH_SHORT).show();
            startLevel();

        }

    }

    public void gameOver() {

        if (mMusic) mMusicHelper.pauseMusic();
        mGame = false;


        for (Balloon balloon : mBalloons) {
            balloon.pauseGame();

            balloon.allowpop = false;
        }

        pauseGame = true;

        if (rewarded_times < 2) {
            WatchAdAndContinue();
        } else {
            endGame();
        }

        pauseGame = true;

    }


    public void WatchAdAndContinue() {


        watchAdDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_blueprint_message_title_three_btn, false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
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

                            endGame();
                            watchAdDialog.dismiss();

                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            } else {

                                gameOver();
                                finish();
                            }

                            Toast.makeText(GameplayActivity.this, "You do not have enough coins ro redeem", Toast.LENGTH_SHORT).show();
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

                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        } else {

                            gameOver();
                            finish();
                        }

                    }
                }, 200);
            }
        });

        try {
            watchAdDialog.show();
        } catch (Exception ignored) {
        }

    }


    private void endGame() {


        for (Balloon balloon : mBalloons) {
            mContentView.removeView(balloon);
            balloon.setPopped(true);
        }

        mBalloons.clear();
        mPlaying = false;

        if (mScore > highScore) {

            highScore = mScore;
            highscoreReference.setValue(mScore);

            if (mScore > topScoreValue){

                Toast.makeText(this, "We have notified all players about your new high score. You're the king", Toast.LENGTH_SHORT).show();


                String name;
                if (sharedPref.getIsGuest()){

                    name = sharedPref.getGuestUsername();

                }else {

                    name = sharedPref.getPortalFullName();
                }

                String message = name + " has just set a new record in \"Balloon Pop\" game by scoring "+ mScore + " points. \nYou could be the next top scorer. Open Mmust App and try your luck.";
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


    /**
     * This method update score after every popped balloon and level at the beginning of new level.
     */
    private void updateDisplay() {
        mScoreDisplay.setText(String.valueOf(mScore));
        mLevelDisplay.setText(String.valueOf(mLevel));
    }

    /**
     * This method add new balloon to the screen.
     *
     * @param x represent x axis of the balloon.
     * @see Balloon
     * @see ViewGroup#addView(View)
     */
    private void launchBalloon(int x) {
        Balloon balloon = new Balloon(this, mBalloonColors[mRandom.nextInt(mBalloonColors.length)], 150);
        mBalloons.add(balloon);
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mContentView.addView(balloon);
        balloon.releaseBalloon(mScreenHeight, Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000)));
    }

    /**
     * This class is responsible for calculating speed of balloons and x axis position of the balloon
     *
     * @see AsyncTask
     */
    @SuppressLint("StaticFieldLeak")
    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        /**
         * This method is executing in background and calculate speed and position of balloons depends on game level.
         * With increasing level, speed of balloons is increasing too.
         *
         * @param params represent current level
         * @return null
         * @see AsyncTask#doInBackground(Object[])
         * @see AsyncTask#publishProgress(Object[])
         * @see Thread#sleep(long)
         */
        @Nullable
        @Override
        protected Void doInBackground(@NonNull Integer... params) {

            if (params.length != 1)
                throw new AssertionError(getString(R.string.assertion_message));
            int minDelay = Math.max(MIN_ANIMATION_DELAY, (MAX_ANIMATION_DELAY - ((params[0] - 1) * 500))) / 2;
            int balloonsLaunched = 0;

            while (mPlaying && balloonsLaunched < mBalloonsPerLevel && !pauseGame) {
                Random random = new Random(new Date().getTime());
                publishProgress(random.nextInt(mScreenWidth - 200));
                balloonsLaunched++;

                try {
                    Thread.sleep(random.nextInt(minDelay) + minDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        /**
         * This method update UI, calling launchBalloon() method.
         *
         * @param values represent calculated x axis of balloon
         * @see GameplayActivity#launchBalloon(int)
         * @see AsyncTask#onProgressUpdate(Object[])
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            launchBalloon(values[0]);
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
                        Toast.makeText(GameplayActivity.this, "You've redeemed 5 coins", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                    }
                });


    }


    private void continueGameAfterReward() {

        final RelativeLayout gameLayout = findViewById(R.id.gameLayout);
        gameLayout.setAlpha(0.3f);

        final LinearLayout resumeLayout = findViewById(R.id.resumeLayout);
        resumeLayout.setVisibility(View.VISIBLE);


        final TextView resumeText = findViewById(R.id.resumeCountDown);
        resumeText.setText("3");


        final TextView resumeTitleText = findViewById(R.id.resumeLayoutTitle);
        resumeTitleText.setText("Game resumes in:");

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


                                gameLayout.setAlpha(1);
                                resumeLayout.setVisibility(View.GONE);

                                pauseGame = false;

                                mHeartsUsed = 0;
                                mGame = true;
                                if (mMusic) mMusicHelper.playMusic();
                                for (ImageView pin : mHeartImages)
                                    pin.setImageResource(R.drawable.heart);

                                updateDisplay();
                                new BalloonLauncher().execute(mLevel);
                                mPlaying = true;

                                for (Balloon balloon : mBalloons) {

                                    balloon.resumeGame();

                                    balloon.allowpop = true;
                                }


                            }
                        }, 1000);


                    }
                }, 1000);


            }
        }, 1000);


    }


    private void startgameCountDown() {

        final RelativeLayout gameLayout = findViewById(R.id.gameLayout);
        gameLayout.setAlpha(0.3f);

        final LinearLayout resumeLayout = findViewById(R.id.resumeLayout);
        resumeLayout.setVisibility(View.VISIBLE);


        final TextView resumeText = findViewById(R.id.resumeCountDown);
        resumeText.setText("3");


        final TextView resumeTitleText = findViewById(R.id.resumeLayoutTitle);
        resumeTitleText.setText("Game starts in:");

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


                                gameLayout.setAlpha(1);
                                resumeLayout.setVisibility(View.GONE);
                                startGame();


                            }
                        }, 1000);


                    }
                }, 1000);


            }
        }, 1000);


    }


    public void loadAnddisplayAd(String titlee, String message) {

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


                        load_ad_immediately = false;
                        endGame();
                        loadingAdDialog.dismiss();

                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        } else {

                            gameOver();
                            finish();
                        }

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

            setToFullScreen();
            mAd.resume(this);
        } catch (Exception ignored) {
        }
        super.onResume();
    }

    @Override
    protected void onPause() {


        try {
            if (mGame) {
                if (mMusic) mMusicHelper.pauseMusic();
            }
            mAd.pause(this);
        } catch (Exception ignored) {
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        if (!mGame) {
            super.onBackPressed();

            if (loadingAdDialog != null) {
                if (loadingAdDialog.isShowing()) {
                    loadingAdDialog.dismiss();
                    endGame();
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    } else {


                        Intent intent = new Intent(GameplayActivity.this, MainActivity_BalloonPop.class);
                        intent.putExtra("refresh", false);
                        intent.putExtra("coins", coinBalance);
                        intent.putExtra("highscore", highScore);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(GameplayActivity.this, MainActivity_BalloonPop.class);
                    intent.putExtra("refresh", false);
                    intent.putExtra("coins", coinBalance);
                    intent.putExtra("highscore", highScore);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }


}