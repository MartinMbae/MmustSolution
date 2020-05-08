package studios.luxurious.mmustsolution.Leisure.Coins;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class Coins extends AppCompatActivity implements RewardedVideoAdListener {


    FirebaseAuth mAuth;
    TextView coinsTextview;
    MaterialDialog loadingAdDialog, inviteFriendsDialog;

    TextView watchAd;

    RewardedVideoAd mAd;

    int coins = 0;


    boolean load_ad_immediately = false;

    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        coinsTextview = findViewById(R.id.coinsBalance);
        watchAd = findViewById(R.id.btnWatchAds);

        watchAd.setEnabled(false);

        sharedPref = new SharedPref(this);


        loadingCoinBalance("Please wait...", "Fetching your coin balance", uid);


        MobileAds.initialize(Coins.this,
                getResources().getString(R.string.app_id));


        mAd = MobileAds.getRewardedVideoAdInstance(Coins.this);
        mAd.setRewardedVideoAdListener(Coins.this);

        mAd.loadAd(getResources().getString(R.string.rewarded_video_earn_coins), new AdRequest.Builder().build());


        watchAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadAnddisplayAd("Please wait", "Loading rewarded video");
            }
        });


    }


    public void loadingCoinBalance(String titlee, String message, String uid) {

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
        cancelLayout.setVisibility(View.GONE);

        try {
            loadingAdDialog.show();
        } catch (Exception ignored) {
        }


        FirebaseFirestore.getInstance().collection("Users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot != null) {
                    String coins_from_db = documentSnapshot.getString("coins");
                    coinsTextview.setText(coins_from_db);
                    loadingAdDialog.dismiss();
                    watchAd.setEnabled(true);

                    try {
                        if (coins_from_db != null)
                            coins = Integer.parseInt(coins_from_db);
                    } catch (Exception ignored) {
                    }
                }
            }
        });

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
        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingAdDialog.dismiss();

                load_ad_immediately = false;
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

    }

    @Override
    public void onRewardedVideoAdClosed() {
        load_ad_immediately = false;
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        load_ad_immediately = false;
        coins = coins + 10;

        updateCoins(coins);
        mAd.loadAd(getResources().getString(R.string.rewarded_video_earn_coins), new AdRequest.Builder().build());


    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        mAd.loadAd(getResources().getString(R.string.rewarded_video_earn_coins), new AdRequest.Builder().build());

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
        super.onBackPressed();

        if (loadingAdDialog != null) {
            if (loadingAdDialog.isShowing()) {
                loadingAdDialog.dismiss();

            }
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

                        coinsTextview.setText(String.valueOf(coins));
                        Toast.makeText(Coins.this, "You've earned 10 coins", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                    }
                });


    }



    public void showInviteFriendsDialog(View view) {

        inviteFriendsDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_invite_friends, false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();



        View customView = inviteFriendsDialog.getCustomView();
        TextView referralCodeTextview = customView.findViewById(R.id.inviteCode);

        referralCodeTextview.setText(sharedPref.getReferralCode());

            inviteFriendsDialog.show();

    }
}
