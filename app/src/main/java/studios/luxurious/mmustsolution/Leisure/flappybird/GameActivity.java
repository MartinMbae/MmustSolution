package studios.luxurious.mmustsolution.Leisure.flappybird;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private TextView textViewScore;

    AdView adView;

    DatabaseReference highscoreReference;
    FirebaseAuth mAuth;
    private boolean isGameOver;


    int highScore, topScoreValue;

    MaterialDialog dialogGameOver;

    private boolean allowBackpressecd = false;

    private boolean isSetNewTimerThreadEnabled;


    private Thread setNewTimerThread;

    private Toast mainToast;

    int myCurrentScore = 0;

    private MediaPlayer mediaPlayer;
    InterstitialAd interstitialAd;
    private Timer timer;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE: {
                    if (gameView.isAlive()) {
                        isGameOver = false;
                        gameView.update();
                    } else {
                        if (isGameOver) {
                            break;
                        } else {
                            isGameOver = true;
                        }

                        // Cancel the timer
                        timer.cancel();
                        timer.purge();

                        allowBackpressecd = true;
                        WatchAdAndContinue();
                    }

                    break;
                }

                case RESET_SCORE: {
                    textViewScore.setText("0");

                    break;
                }

                default: {
                    break;
                }
            }
        }
    };


    public void WatchAdAndContinue() {


        dialogGameOver = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_blueprint_message_title, false)
                .cancelable(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();

        View view = dialogGameOver.getCustomView();

        TextView messageText = view.findViewById(R.id.message);
        TextView title = view.findViewById(R.id.title);
        RelativeLayout cancelLayout = view.findViewById(R.id.cancel);
        RelativeLayout okLayout = view.findViewById(R.id.ok);
        TextView canceltext = view.findViewById(R.id.canceltext);
        TextView oktext = view.findViewById(R.id.oktext);

        messageText.setText("Would you like to restart?");
        title.setText("Game Over");
        oktext.setText("Restart");
        canceltext.setText("Exit");

        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);

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
                        dialogGameOver.dismiss();
                        allowBackpressecd = false;
                        GameActivity.this.restartGame();

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


                        if (myCurrentScore > topScoreValue){

                            Toast.makeText(GameActivity.this, "We have notified all players about your new high score. You're the king", Toast.LENGTH_SHORT).show();


                            String name;
                            if (sharedPref.getIsGuest()){

                                name = sharedPref.getGuestUsername();

                            }else {

                                name = sharedPref.getPortalFullName();
                            }

                            String message = name + " has just set a new record in \"Flappy Bird\" game by scoring "+ myCurrentScore + " points. \nYou could be the next top scorer. Open Mmust App and try your luck.";
                            String title = "New High Scores";

                            sendNotification(title, message , dialogGameOver);

                            dialogGameOver.dismiss();
                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            } else {
                                GameActivity.this.onBackPressed();
                            }


                        }else{
                            dialogGameOver.dismiss();
                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            } else {
                                GameActivity.this.onBackPressed();
                            }


                        }

                    }
                }, 200);
            }
        });

        try {
            dialogGameOver.show();
        } catch (Exception ignored) {
        }

    }


    private static final int UPDATE = 0x00;
    private static final int RESET_SCORE = 0x01;
    SharedPref sharedPref;


    public void InitializeAds() {
        MobileAds.initialize(this, getString(R.string.app_id));

        adView = findViewById(R.id.ad);
        AdRequest adRequest = new AdRequest.Builder().build();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_flappy_bird));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        adView.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                interstitialAd.loadAd(new AdRequest.Builder().build());

            }

            @Override
            public void onAdClosed() {
                Intent intent = new Intent(GameActivity.this, StartingActivity.class);
                intent.putExtra("refresh", false);
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



                Intent intent = new Intent(GameActivity.this, StartingActivity.class);
                intent.putExtra("refresh", false);
                intent.putExtra("highscore", highScore);
                startActivity(intent);
                finish();
            }

        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_flappy);

        // Initialize the private views
        sharedPref = new SharedPref(this);
        mAuth = FirebaseAuth.getInstance();
        highscoreReference = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("Flappy_bird").child(mAuth.getCurrentUser().getUid()).child("highScores");
        initViews();

        highscoreReference.keepSynced(true);

        InitializeAds();

        // Initialize the MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_score);
        mediaPlayer.setLooping(false);


        // Set the Timer
        isSetNewTimerThreadEnabled = true;
        setNewTimerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Sleep for 3 seconds for the Surface to initialize
                    Thread.sleep(3000);
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    if (isSetNewTimerThreadEnabled) {
                        setNewTimer();
                    }
                }
            }
        });
        setNewTimerThread.start();

        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gameView.jump();

                        break;

                    case MotionEvent.ACTION_UP:


                        break;

                    default:
                        break;
                }

                return true;
            }
        });


    }


    private void initViews() {
        gameView = findViewById(R.id.game_view);
        textViewScore = findViewById(R.id.text_view_score);
        highScore = getIntent().getIntExtra("highScores", 0);
        topScoreValue = getIntent().getIntExtra("topScoreValue", 0);

    }

    /**
     * Sets the Timer to update the UI of the GameView.
     */
    private void setNewTimer() {
        if (!isSetNewTimerThreadEnabled) {
            return;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // Send the message to the handler to update the UI of the GameView
                GameActivity.this.handler.sendEmptyMessage(UPDATE);

                // For garbage collection
                System.gc();
            }

        }, 0, 17);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        isSetNewTimerThreadEnabled = false;

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        isSetNewTimerThreadEnabled = false;

        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void updateScore(int score) {
        textViewScore.setText(String.valueOf(score));

        myCurrentScore = score;

        if (score > highScore) {
            highScore = score;
            highscoreReference.setValue(score);
        }
    }


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private void sendNotification(final String title, final String message, final MaterialDialog dialogGameOver) {
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


    public void playScoreMusic() {
        mediaPlayer.start();

    }

    /**
     * Restarts the game.
     */
    private void restartGame() {
        // Reset all the data of the over game in the GameView
        gameView.resetData();

        // Refresh the TextView for displaying the score
        new Thread(new Runnable() {

            @Override
            public void run() {
                handler.sendEmptyMessage(RESET_SCORE);
            }

        }).start();

        isSetNewTimerThreadEnabled = true;
        setNewTimerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // Sleep for 3 seconds
                    Thread.sleep(3000);
                } catch (Exception exception) {
                    exception.printStackTrace();
                } finally {
                    if (isSetNewTimerThreadEnabled) {
                        setNewTimer();
                    }
                }
            }

        });
        setNewTimerThread.start();

    }

    @Override
    public void onBackPressed() {

        if (allowBackpressecd) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }

            isSetNewTimerThreadEnabled = false;


            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                Intent intent = new Intent(GameActivity.this, StartingActivity.class);
                intent.putExtra("refresh", false);
                intent.putExtra("highscore", highScore);
                startActivity(intent);
                finish();
            }


        }
    }
}
