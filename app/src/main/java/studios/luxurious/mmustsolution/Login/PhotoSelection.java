package studios.luxurious.mmustsolution.Login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import studios.luxurious.mmustsolution.BuildConfig;
import studios.luxurious.mmustsolution.HomeActivity;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class PhotoSelection extends AppCompatActivity {

    private Button signup;
    private ImageView avatar;
    private Uri mainImageUri;
    private FirebaseAuth mAuth;
    private ProgressDialog pdialog;
    private StorageReference storageReference;
    public static final int PICK_IMAGE = 1;

    SharedPref sharedPref;

    String referrralCode = null;

    boolean awarded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoselection);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        avatar = findViewById(R.id.avatar);
        signup = findViewById(R.id.registerButton);
        pdialog = new ProgressDialog(this);

        sharedPref = new SharedPref(PhotoSelection.this);

        TextView fullname = findViewById(R.id.full_username);
        TextView schoolEmail = findViewById(R.id.school_email);

        fullname.setText(sharedPref.getPortalFullName());
        schoolEmail.setText(sharedPref.getStudentEmail());

        if (ContextCompat.checkSelfPermission(PhotoSelection.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoSelection.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        avatar.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(PhotoSelection.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoSelection.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    pickimage();
                }
            } else {
                pickimage();
            }
        });

        String regno = sharedPref.getRegNumber();


        tryTosignIn(regno, regno);

        signup.setOnClickListener(view -> {


            if (mainImageUri == null) {
                Toast.makeText(PhotoSelection.this, "Please tap on the image icon and select your profile picture.", Toast.LENGTH_LONG).show();
            } else {

                showInviteRequest();


            }

        });


    }

    private void showInviteRequest() {

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

            pdialog.setMessage("Signing up...");
            pdialog.setIndeterminate(true);
            pdialog.setCanceledOnTouchOutside(false);
            pdialog.setCancelable(false);
            pdialog.show();


            String regno = sharedPref.getRegNumber();
            String studentEmail = sharedPref.getStudentEmail();

            createuser(studentEmail, regno);
        });


        inviteFriendsDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                mainImageUri = data.getData();
                CropImage.activity(mainImageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);

            } else {
                Toast.makeText(this, "Unable to load Image", Toast.LENGTH_LONG).show();
            }
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mainImageUri = result.getUri();
                avatar.setImageURI(mainImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception er = result.getError();
            }

            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void createuser(final String email, final String password) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        signin(email, password);
                    } else {
                        if (task.getException().toString().contains("already in use")) {
                            signin(email, password);
                            Toast.makeText(PhotoSelection.this, "You already have an account. Signing you in", Toast.LENGTH_LONG).show();
                        } else {
                            pdialog.dismiss();

                            Toast.makeText(PhotoSelection.this, "Check your internet connection", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }


    private void signin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploadimage();
            } else {
                pdialog.dismiss();
                Toast.makeText(PhotoSelection.this, "Something went wrong. Please try again", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void uploadimage() {
        final String userid = mAuth.getCurrentUser().getUid();


        if (mainImageUri != null) {

            final StorageReference filePath = storageReference.child("profile_images/" + userid + ".png");

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), mainImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(e -> Toast.makeText(PhotoSelection.this, "Failed to upload your profile picture. Please try again", Toast.LENGTH_SHORT).show());
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url = String.valueOf(uri);
                    String regno = sharedPref.getRegNumber();
                    String name = sharedPref.getPortalFullName();
                    updateprofile(name, url, regno);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PhotoSelection.this, "Failed to upload your profile picture. Please try again", Toast.LENGTH_SHORT).show();

                }
            }));

        } else {
            Toast.makeText(this, "no image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateprofile(final String name, final String url, final String regno) {
        UserProfileChangeRequest profileupdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(Uri.parse(url))
                .build();

        mAuth.getCurrentUser().updateProfile(profileupdates).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        saveUserDetails(name, url, regno);
                    } else {
                        Toast.makeText(PhotoSelection.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
                        pdialog.dismiss();
                    }
                }
        );

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

    private void saveUserDetails(final String name, final String url, final String reg_no) {

        final CollectionReference referralCodeReferences = FirebaseFirestore.getInstance().collection("referralCodes");

        final String key = generateRandomString(8);

        referralCodeReferences.document(key).get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    saveUserDetails(name, url, reg_no);
                } else {


                    //Award Inviter

                    if (referrralCode != null) {

                        referralCodeReferences.document(referrralCode).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                if (documentSnapshot != null) {
                                    final String uid = documentSnapshot.getString("uid");

                                    if (uid != null) {

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
                                                                            Toast.makeText(PhotoSelection.this, "Your friend has been awarded 100 coins", Toast.LENGTH_SHORT).show();

                                                                            completeRegistration(referralCodeReferences, key, name, url, reg_no);

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getApplicationContext(), "Something went wrong. Your friend was not rewarded", Toast.LENGTH_LONG).show();

                                                                            completeRegistration(referralCodeReferences, key, name, url, reg_no);

                                                                        }
                                                                    });

                                                        }
                                                    } catch (Exception ignored) {

                                                        Toast.makeText(PhotoSelection.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        completeRegistration(referralCodeReferences, key, name, url, reg_no);

                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        completeRegistration(referralCodeReferences, key, name, url, reg_no);

                                    }

                                } else {
                                    Toast.makeText(PhotoSelection.this, "You provided a non existing referral code.", Toast.LENGTH_SHORT).show();

                                    completeRegistration(referralCodeReferences, key, name, url, reg_no);

                                }
                            }
                        });


                    } else {

                        Toast.makeText(PhotoSelection.this, "You provided a non existing referral code.", Toast.LENGTH_SHORT).show();

                        completeRegistration(referralCodeReferences, key, name, url, reg_no);

                    }


                }

            } else {
                Toast.makeText(PhotoSelection.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();

            }

        });


    }

    private void completeRegistration(CollectionReference referralCodeReferences, final String key, final String name, final String url, final String reg_no) {

        Map<String, Object> details = new HashMap<>();
        details.put("uid", mAuth.getCurrentUser().getUid());

        referralCodeReferences.document(key).set(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("url", url);
                user.put("referralCode", key);
                user.put("regno", reg_no);
                user.put("coins", "100");


                FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getUid())
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                sharedPref.setReferralCode(key);
                                sharedPref.setIsGuest(false);

                                goToHome();

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
                Toast.makeText(PhotoSelection.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void pickimage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }


    private void tryTosignIn(String useremail, String userpassword) {

        pdialog = new ProgressDialog(PhotoSelection.this);
        pdialog.setMessage("Please wait...");
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.show();


        useremail = sharedPref.getStudentEmail();

        mAuth.signInWithEmailAndPassword(useremail, userpassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {


                        String uid = mAuth.getCurrentUser().getUid();
                        FirebaseFirestore.getInstance().collection("Users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                if (documentSnapshot != null) {
                                    String referralCode = documentSnapshot.getString("referralCode");


                                    sharedPref.setReferralCode(referralCode);
                                    sharedPref.setIsGuest(false);

                                    goToHome();


                                } else {
                                    goToHome();
                                }
                            }
                        });

                    } else {
                        String message = task.getException().toString();
                        if (message.contains("password is invalid")) {
                            pdialog.dismiss();
                            Toast.makeText(PhotoSelection.this, "Email or Password is Incorrect", Toast.LENGTH_LONG).show();
                        } else if (message.contains("There is no user")) {
                            pdialog.dismiss();
                            Toast.makeText(PhotoSelection.this, "Select a profile picture", Toast.LENGTH_LONG).show();


                        } else {
                            pdialog.dismiss();
                            Toast.makeText(PhotoSelection.this, "Please check your internet connection and try again", Toast.LENGTH_LONG).show();

                        }
                    }
                });


    }


    private void goToHome() {

        pdialog.dismiss();

        String versionName = BuildConfig.VERSION_NAME;
        sharedPref.setCurrentVersion(versionName);

        Intent intent = new Intent(PhotoSelection.this, LastStep.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
