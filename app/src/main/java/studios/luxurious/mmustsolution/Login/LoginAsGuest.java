package studios.luxurious.mmustsolution.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class LoginAsGuest extends AppCompatActivity {

    TextInputEditText username;
    Button buttonContinue;
    ProgressDialog progressDialog;
    SharedPref sharedPref;
    FirebaseAuth mAuth;

    String referrralCode = null;


    boolean awarded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_as_guest);

        username = findViewById(R.id.guest_username);
        buttonContinue = findViewById(R.id.buttonContinue);

        sharedPref = new SharedPref(LoginAsGuest.this);

        progressDialog = new ProgressDialog(LoginAsGuest.this);

        mAuth = FirebaseAuth.getInstance();

        final String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        final String email = "m" + android_id + Constants.EMAIL_EXTENSION;

        buttonContinue.setOnClickListener(v -> {
            final String name = username.getText().toString();

            if (TextUtils.isEmpty(name)) {
                username.setError("Please fill this field");
            } else {

              showInviteRequest(email,android_id,name);

            }
        });
        tryTosignIn(email, android_id);


    }

    private void showInviteRequest(final String email, final String android_id, final String name) {

        final MaterialDialog inviteFriendsDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_invited, false)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();


        View customView = inviteFriendsDialog.getCustomView();
        final EditText referralCodeEdittext = customView.findViewById(R.id.inviteCodeEdittext);
        RelativeLayout oklayout = customView.findViewById(R.id.ok);

        oklayout.setOnClickListener(view -> {

            String referralCode = referralCodeEdittext.getText().toString().trim();

            if (!TextUtils.isEmpty(referralCode)) {

                referrralCode = referralCode;

            }

            inviteFriendsDialog.dismiss();

            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Logging you in as a guest");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            createuser(email, android_id, name);
        });


        inviteFriendsDialog.show();
    }



    private void tryTosignIn(String useremail, String userpassword) {

        progressDialog = new ProgressDialog(LoginAsGuest.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        mAuth.signInWithEmailAndPassword(useremail, userpassword).addOnSuccessListener(authResult -> {

            sharedPref.setIsGuest(true);

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            DocumentReference documentReference = firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid());
            documentReference.get().addOnCompleteListener(LoginAsGuest.this, task1 -> {
                if (task1.isSuccessful()) {
                    if (task1.getResult().exists() && task1.getResult().exists()) {
                        String nameDB = task1.getResult().get("name").toString();
                        String referralCode = task1.getResult().get("referralCode").toString();

                        sharedPref.setIsGuest(true);
                        sharedPref.setGuestUsername(nameDB);
                        sharedPref.setReferralCode(referralCode);

                        FirebaseMessaging.getInstance().subscribeToTopic("AllNew");

                        goToLastStep();

                    }
                }
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
        });

    }


    private void createuser(final String email, final String password, final String name) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveUserDetails(name);
                    } else {
                        if (task.getException().toString().contains("already in use")) {
                            signin(email, password, name);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginAsGuest.this, "Check your internet connection", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }

    private void signin(String email, String password, final String name) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sharedPref.setGuestUsername(name);
                goToLastStep();
            } else {
                progressDialog.dismiss();
                Toast.makeText(LoginAsGuest.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
            }
        });
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

    private void saveUserDetails(final String name) {

        final CollectionReference referralCodeReferences = FirebaseFirestore.getInstance().collection("referralCodes");

        final String key = generateRandomString(8);

        referralCodeReferences.document(key).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    saveUserDetails(name);
                } else {



                    //Invited user reward

                    if (referrralCode != null) {

                        referralCodeReferences.document(referrralCode).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                if (documentSnapshot != null) {
                                    final String uid = documentSnapshot.getString("uid");

                                    if (uid != null){

                                    FirebaseFirestore.getInstance().collection("Users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                            if (documentSnapshot != null) {
                                                String coins_from_db = documentSnapshot.getString("coins");
                                                int coins = 0;
                                                try {

                                                    if (!awarded) {


                                                        awarded = true;

                                                        if (coins_from_db != null)
                                                            coins = Integer.parseInt(coins_from_db);

                                                        int new_coins = coins + 100;

                                                        String coinsString = String.valueOf(new_coins);
                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put("coins", coinsString);


                                                        FirebaseFirestore.getInstance().collection("Users").document(uid)
                                                                .update(user)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        awarded = true;

                                                                        Toast.makeText(LoginAsGuest.this, "Your friend has been awarded 100 coins", Toast.LENGTH_SHORT).show();


                                                                        completeRegistration(referralCodeReferences, key, name);
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getApplicationContext(), "Something went wrong. Your friend was not rewarded", Toast.LENGTH_LONG).show();

                                                                        completeRegistration(referralCodeReferences, key, name);

                                                                    }
                                                                });

                                                    }
                                                } catch (Exception ignored) {

                                                    Toast.makeText(LoginAsGuest.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                                    completeRegistration(referralCodeReferences, key, name);
                                                }
                                            }
                                        }
                                    });
                                }else {

                                        Toast.makeText(LoginAsGuest.this, "You provided a non existing referral code.", Toast.LENGTH_SHORT).show();

                                        completeRegistration(referralCodeReferences, key, name);

                                    }

                                } else {
                                    Toast.makeText(LoginAsGuest.this, "You provided a non existing referral code.", Toast.LENGTH_SHORT).show();


                                    completeRegistration(referralCodeReferences,key,name);
                                }
                            }
                        });


                    }else {



                        completeRegistration(referralCodeReferences, key, name);

                    }



                }

            } else {
                Toast.makeText(LoginAsGuest.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();

            }

        });


    }

    private  void completeRegistration(CollectionReference referralCodeReferences, final String key, final String name){


        Map<String, Object> details = new HashMap<>();
        details.put("uid", mAuth.getCurrentUser().getUid());

        referralCodeReferences.document(key).set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("referralCode", key);
                user.put("coins", "100");

                FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getUid())
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                sharedPref.setIsGuest(true);
                                sharedPref.setGuestUsername(name);
                                sharedPref.setReferralCode(key);


                                FirebaseMessaging.getInstance().subscribeToTopic("AllNew").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
goToLastStep();
                                    }
                                });


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                            }
                        });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginAsGuest.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void goToLastStep() {

        username.setFocusable(false);
        progressDialog.dismiss();
        startActivity(new Intent(LoginAsGuest.this, LastStep.class));
        finish();
    }


}
