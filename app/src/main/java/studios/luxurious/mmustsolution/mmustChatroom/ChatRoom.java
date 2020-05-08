package studios.luxurious.mmustsolution.mmustChatroom;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;
import studios.luxurious.mmustsolution.HomeActivity;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.SharedPref;
import studios.luxurious.mmustsolution.mmustChatroom.Fragments.HomeFragment;
import studios.luxurious.mmustsolution.mmustChatroom.Fragments.Trending;
import studios.luxurious.mmustsolution.mmustChatroom.Helper.BottomNavigationViewHelper;
import studios.luxurious.mmustsolution.mmustChatroom.Helper.CustomTypefaceSpan;

public class ChatRoom extends AppCompatActivity {

    TextView header;
    FirebaseAuth mAuth;
    BottomNavigationView bottom_nav;
    private CircleImageView profileimage;
    private Fragment homeFragment, trendingFragment;

    public static final String NOTIFICATION = "PushNotification";
    public static final String SHARED_PREFERENCES = "MmustApp";
    String token;

    SharedPref sharedPref;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        mAuth = FirebaseAuth.getInstance();


        //Customizing the Toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        header = findViewById(R.id.header);
        header.setTypeface(pacifico);
        profileimage = findViewById(R.id.profileimage);
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(ChatRoom.this, ProfileActivity.class);
                startActivity(profile);
            }
        });
        loadprofileImage(mAuth.getCurrentUser().getPhotoUrl());


        sharedPref = new SharedPref(this);
        //Customizing Bottom Navigation
        bottom_nav = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.disableShiftMode(bottom_nav);
        Menu m = bottom_nav.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            applyFontToMenuItem(mi);
        }
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Initializing Home Fragment
        homeFragment = new HomeFragment();
        trendingFragment = new Trending();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .add(R.id.fragment_container, trendingFragment)
                .hide(trendingFragment)
                .commit();


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(ChatRoom.this, "Get Instance Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    token = task.getResult().getToken();

                    sharedPref.setNotificationToken(token);
                    FirebaseMessaging.getInstance().subscribeToTopic("AllNew");


                });


        InitializeAds();
    }

    void InitializeAds() {

        //Initialize the ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_chatroom));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                startActivity(new Intent(ChatRoom.this, HomeActivity.class));
                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {

            startActivity(new Intent(ChatRoom.this, HomeActivity.class));
            finish();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(homeFragment, trendingFragment);
                    return true;
                case R.id.trending:
                    replaceFragment(trendingFragment, homeFragment);
                    return true;


            }
            return false;
        }
    };

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void loadprofileImage(Uri uri) {
        Glide.with(ChatRoom.this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.fb_holder)
                        .error(R.drawable.fb_holder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(uri)
                .into(profileimage);
    }

    private void replaceFragment(Fragment one, Fragment two) {
        if (!one.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .show(one)
                    .hide(two)
                    .commit();
        }

    }

    public void AddItem(View view) {
        startActivity(new Intent(ChatRoom.this, PostActivity.class));

    }
}