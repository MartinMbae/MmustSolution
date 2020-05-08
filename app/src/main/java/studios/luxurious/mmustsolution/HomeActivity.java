package studios.luxurious.mmustsolution;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import studios.luxurious.mmustsolution.Leisure.Coins.Coins;
import studios.luxurious.mmustsolution.Leisure.Game1.Game_Activity_1;
import studios.luxurious.mmustsolution.Leisure.LeaderBoard.LeadersBoard;
import studios.luxurious.mmustsolution.Leisure.flappybird.StartingActivity;
import studios.luxurious.mmustsolution.Leisure.pop_up_balloon.MainActivity_BalloonPop;
import studios.luxurious.mmustsolution.Login.RegistrationNumber;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;
import studios.luxurious.mmustsolution.Voting.VotingActivity;
import studios.luxurious.mmustsolution.attendance.Login;
import studios.luxurious.mmustsolution.mmustChatroom.ChatRoom;
import studios.luxurious.mmustsolution.mmustChatroom.LoginActivity;
import studios.luxurious.mmustsolution.mmustChatroom.MyProfileActivity;


public class HomeActivity extends AppCompatActivity {

    LinearLayout profile, portal, chatroom, attendance, vote, lostAndFound, history, contact_us, fullsite, faq,downloads,programmes;
    TextView textView;
    Toolbar toolbar;
    LinearLayout blur, rel;
    FirebaseAuth mAuth;
    SharedPref sharedPref;
    String referrralCode = null;

    Boolean awarded = false;

    MaterialDialog loadingAdDialog, inviteFriendsDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textView = findViewById(R.id.te);
        toolbar = findViewById(R.id.toolbar);
        mAuth = FirebaseAuth.getInstance();


        blur = findViewById(R.id.blur);
        rel = findViewById(R.id.relative);


        sharedPref = new SharedPref(this);

        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("MMUST University");
        }


        profile = findViewById(R.id.profile);
        portal = findViewById(R.id.portal);
        chatroom = findViewById(R.id.chatroom);
        attendance = findViewById(R.id.attendance);
        vote = findViewById(R.id.vote);
        lostAndFound = findViewById(R.id.lost_and_found);
        history = findViewById(R.id.about_us);
        contact_us = findViewById(R.id.contact);
        fullsite = findViewById(R.id.fullsite);
        faq = findViewById(R.id.faq);
        downloads = findViewById(R.id.downloads);
        programmes = findViewById(R.id.programs);


        if (sharedPref.getIsFirstTime()) {
            showInviteFriendsDialog();

            sharedPref.setIsFirstTime(false);
        }


        profile.setOnClickListener(v -> {

            if (mAuth.getCurrentUser() == null) {
                dialogLogInToYourPortal("You must first login to MMUST Chatroom", "Access Denied");
            } else {
                if (sharedPref.getIsGuest()) {
                    dialogBoxGuestLogin();
                } else {
                    startActivity(new Intent(HomeActivity.this, MyProfileActivity.class));
                }
            }
        });

        if (sharedPref.getReferralCode() == null) {

            fetchReferalCodeAndName();

        } else {

            if (!sharedPref.getIsGuest()) {
                if (sharedPref.getPortalFullName() == null) {
                    fetchReferalCodeAndName();
                }
            }

        }

        portal.setOnClickListener(v -> {


            if (IsConnected()) {
                String regno = sharedPref.getRegNumber();
                String password = sharedPref.getPortalPassword();
                String fullname = sharedPref.getPortalFullName();

                if (regno != null && password != null && fullname != null) {
                    if (!regno.equalsIgnoreCase("null") && !password.equalsIgnoreCase("null") && !fullname.equalsIgnoreCase("null")) {

                        Intent f = new Intent(HomeActivity.this, Portal.class);
                        f.putExtra("regno", regno);
                        f.putExtra("password", password);
                        startActivity(f);
                        finish();

                    } else {
                        startActivity(new Intent(HomeActivity.this, LoginPortal.class));
                    }
                } else {
                    startActivity(new Intent(HomeActivity.this, LoginPortal.class));
                }

            } else {

                Toast.makeText(HomeActivity.this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
            }
        });

        chatroom.setOnClickListener(v -> {


            if (sharedPref.getIsGuest()) {
                dialogBoxGuestLogin();
                return;
            }


            String regno = sharedPref.getRegNumber();
            String password = sharedPref.getPortalPassword();
            String fullname = sharedPref.getPortalFullName();

            if (regno != null && password != null && fullname != null) {
                if (!regno.equalsIgnoreCase("null") && !password.equalsIgnoreCase("null") && !fullname.equalsIgnoreCase("null")) {


                    if (mAuth.getCurrentUser() == null) {
                        Intent f = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(f);

                    } else {
                        Intent f = new Intent(HomeActivity.this, ChatRoom.class);
                        startActivity(f);
                        finish();
                    }


                } else {
                    dialogLogInToYourPortal("You must first log in successfully to your portal before proceeding to MMUST Chatroom", "Access Denied");
                }
            } else {

                dialogBoxGuestLogin();
            }
        });


        attendance.setOnClickListener(v -> {


            if (sharedPref.getIsGuest()) {
                dialogBoxGuestLogin();
                return;
            }

            String str2 = "Class Attendance is not officially available.";

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
            alertDialog.setTitle("Currently not Available")
                    .setMessage(str2)
                    .setNegativeButton("CLOSE", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();
//            startActivity(new Intent(HomeActivity.this, Login.class));

        });

        vote.setOnClickListener(v -> {

            if (sharedPref.getIsGuest()) {
                dialogBoxGuestLogin();
                return;
            }


            String str2 = "Voting is only available on the Mmust Election day.";

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
            alertDialog.setTitle("Currently not Available")
                    .setMessage(str2)
                    .setNegativeButton("CLOSE", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .show();

//            startActivity(new Intent(HomeActivity.this, VotingActivity.class));

        });

        lostAndFound.setOnClickListener(v -> {

            if (mAuth.getCurrentUser() == null) {
                dialogLogInToYourPortal("You must first login to Mmust Chatroom", "Access Denied");
            } else {
                startActivity(new Intent(HomeActivity.this, LostAndFound.class));
            }


        });

        history.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, about_us.class)));

        contact_us.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, Contact_us.class)));

        faq.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FaqActivity.class)));

        downloads.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, Downloads.class)));

        programmes.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProgrammesActivity.class)));

        textView.setOnClickListener(v -> {

            ScrollView scrollView = findViewById(R.id.scroll);

            scrollView.smoothScrollTo(0, 0);
        });

        fullsite.setOnClickListener(v -> {

            if (IsConnected()) {
                Intent launchbrowser = new Intent("android.intent.action.VIEW", Uri.parse(Constants.WEBSITE_URL));
                startActivity(launchbrowser);

            } else {

                Toast.makeText(HomeActivity.this, "You are not connected to the internet", Toast.LENGTH_SHORT).show();
            }


        });



        FirebaseMessaging.getInstance().subscribeToTopic("AllNew");
        try {
            loadingAdDialog.dismiss();
        } catch (Exception ignored) {
        }

    }

    private void fetchReferalCodeAndName() {
        if (inviteFriendsDialog != null) {

            if (inviteFriendsDialog.isShowing()) {
                inviteFriendsDialog.dismiss();
            }
        }


        loadingAdDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_blueprint_gif, false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();

        View view = loadingAdDialog.getCustomView();
        TextView messageText = view.findViewById(R.id.message);
        TextView title = view.findViewById(R.id.title);
        title.setText("Completing Registration");
        messageText.setText("Please wait as we update your details");


        RelativeLayout cancelLayout = view.findViewById(R.id.cancel);
        cancelLayout.setVisibility(View.GONE);

        try {
            loadingAdDialog.show();
        } catch (Exception ignored) {
        }


        getNewReferralCode();
    }


    private String generateRandomString(int lenght) {
        if (lenght > 0) {
            StringBuilder stringBuilder = new StringBuilder(lenght);
            String CHAR_LOWER = "abcdefghijkmnpqrstuvwxyz";
            String CHAR_UPPER = CHAR_LOWER.toUpperCase();
            String NUMBER = "23456789";

            String all_characters = CHAR_LOWER + CHAR_UPPER + NUMBER;
            SecureRandom secureRandom = new SecureRandom();
            for (int i = 0; i < lenght; i++) {
                int randomCharAt = secureRandom.nextInt(all_characters.length());
                char randomChar = all_characters.charAt(randomCharAt);

                stringBuilder.append(randomChar);
            }

            return stringBuilder.toString();
        } else {
            return "0";
        }
    }

    private void completeRegistration(CollectionReference referralCodeReferences, final String key) {


        Map<String, Object> details = new HashMap<>();
        details.put("uid", mAuth.getCurrentUser().getUid());


        referralCodeReferences.document(key).set(details).addOnSuccessListener(aVoid -> {

            Map<String, Object> user = new HashMap<>();
            user.put("referralCode", key);
            user.put("coins", "100");

            FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getUid())
                    .update(user)
                    .addOnSuccessListener(aVoid1 -> {

                        sharedPref.setReferralCode(key);

                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        String uid = mAuth.getCurrentUser().getUid();

                        DocumentReference documentReference = firebaseFirestore.collection("Users").document(uid);
                        documentReference.get().addOnCompleteListener(HomeActivity.this, new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    if (task.getResult().exists() && task.getResult().exists()) {
                                        String nameDB = task.getResult().get("name").toString();

                                        sharedPref.setPortalFullName(nameDB);


                                    }
                                }
                            }
                        });


                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();

                        loadingAdDialog.dismiss();

                    });


        }).addOnFailureListener(e -> {

            loadingAdDialog.dismiss();
            Toast.makeText(HomeActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();

        });
    }


    private void getNewReferralCode() {


        final CollectionReference referralCodeReferences = FirebaseFirestore.getInstance().collection("referralCodes");

        final String key = generateRandomString(8);

        referralCodeReferences.document(key).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    getNewReferralCode();
                } else {


                    completeRegistration(referralCodeReferences, key);

                }

            } else {
                Toast.makeText(HomeActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();

            }

        });


    }


    public void dialogLogInToYourPortal(String message, String title) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle(title)
                .setMessage(message)

                .setNegativeButton("CLOSE", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();

    }

    public void dialogBoxGuestLogin() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Access Denied")
                .setMessage("You are logged in as a guest user. Access is only allowed to students")

                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss()).setNeutralButton("Log out", (dialog, which) -> {

            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this, RegistrationNumber.class));
            finish();
        })
                .setCancelable(false)
                .show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.background, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_share:

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Download Masinde Muliro University App from the playstore https://play.google.com/store/apps/details?id=" + getApplicationContext();
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Masinde Muliro University App");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;

            case R.id.action_star:

                rateUs();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean IsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    public void onBackPressed() {

        if (sharedPref.getRatings() == 0) {
            RateUsRequest("Do you like this app?", "Please take a minute to rate us in Google Playstore. We value your honest feedback");

        } else {
           finish();
        }
    }

    public void RateUsRequest(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Rate", (dialog, which) -> {
            rateUs();
            dialog.dismiss();
        });
        builder.setNegativeButton("Later", (dialog, which) -> {
            dialog.dismiss();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void rateUs() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://play.google.com/store/apps/details?id=" + getPackageName()));
        intent.setPackage("com.android.vending");

        sharedPref.setRatings(1);

        startActivity(intent);


    }


    public void toAboutMe(View view) {

        startActivity(new Intent(HomeActivity.this, Aboutme.class));
    }


    public void LeadersBoard(View view) {

        startActivity(new Intent(HomeActivity.this, LeadersBoard.class));
    }


    public void Game1(View view) {

        startActivity(new Intent(HomeActivity.this, Game_Activity_1.class));
    }

    public void Game2(View view) {

        Intent intent = new Intent(HomeActivity.this, StartingActivity.class);
        intent.putExtra("refresh", true);
        startActivity(intent);
        finish();
    }

    public void Game3(View view) {
        Intent intent = new Intent(HomeActivity.this, MainActivity_BalloonPop.class);
        intent.putExtra("refresh", true);
        startActivity(intent);
        finish();

    }

    public void toCoins(View view) {
        startActivity(new Intent(HomeActivity.this, Coins.class));
    }


    public void showInviteFriendsDialog() {

        inviteFriendsDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_welcome, false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();

        View customView = inviteFriendsDialog.getCustomView();
        TextView referralCodeTextview = customView.findViewById(R.id.inviteCode);

        referralCodeTextview.setText(sharedPref.getReferralCode());

        inviteFriendsDialog.show();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (loadingAdDialog != null) {
            if (loadingAdDialog.isShowing()) loadingAdDialog.dismiss();
        }


        if (inviteFriendsDialog != null) {
            if (inviteFriendsDialog.isShowing()) inviteFriendsDialog.dismiss();
        }
    }
}
