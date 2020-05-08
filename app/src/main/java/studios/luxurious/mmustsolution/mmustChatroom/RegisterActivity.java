package studios.luxurious.mmustsolution.mmustChatroom;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;


public class RegisterActivity extends AppCompatActivity {

    private TextView toptext;
    private Button signup;
    private ImageView avatar;
    private EditText fullname, email, password;
    private Uri mainImageUri;
    private FirebaseAuth mAuth;
    private ProgressDialog pdialog;
    private StorageReference storageReference;
    public static final int PICK_IMAGE = 1;
    SharedPref sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        avatar = findViewById(R.id.avatar);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        toptext = findViewById(R.id.toptext);
        toptext.setTypeface(pacifico);
        pdialog = new ProgressDialog(RegisterActivity.this);

        sharedPref = new SharedPref(this);


        String regno = sharedPref.getRegNumber();
        String passwordString = sharedPref.getPortalPassword();

        email.setText(regno);
        password.setText(passwordString);
        fullname.setText(sharedPref.getPortalFullName());


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        pickimage();
                    }
                } else {
                    pickimage();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = fullname.getText().toString();
                String mail = email.getText().toString();
                final String pwd = password.getText().toString();
                if (checkfields(name, mail, pwd)) {
                    pdialog.setMessage("Signing up...");
                    pdialog.setIndeterminate(true);
                    pdialog.setCanceledOnTouchOutside(false);
                    pdialog.setCancelable(false);
                    pdialog.show();


                    mail = mail + Constants.EMAIL_EXTENSION;
                    createuser(mail, pwd, name);
                }
            }
        });


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


    private void createuser(final String email, final String password, final String name) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signin(email, password, name);
                        } else {
                            if (task.getException().toString().contains("already in use")) {
                                pdialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "User already exists, please sign in !", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Something wrong happened :(", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });

    }

    private boolean checkfields(String name, String mail, String pwd) {

        if (TextUtils.isEmpty(name)) {
            fullname.setError("Name can't be empty");
        }
        if (TextUtils.isEmpty(mail)) {
            email.setError("Email can't be empty");
        }
        if (TextUtils.isEmpty(pwd)) {
            password.setError("Please enter a password");
        } else if (pwd.length() < 7) {
            password.setError("Password must be of at least 6 characters");
        } else if (mainImageUri == null) {
            Toast.makeText(RegisterActivity.this, "Please select a profile image !", Toast.LENGTH_LONG).show();
        } else {
            return true;
        }

        return false;
    }

    private void signin(String email, String password, final String name) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uploadimage(name);
                } else {
                    pdialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void uploadimage(final String name) {
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

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "Failed to upload your profile picture. Please try again", Toast.LENGTH_SHORT).show();

                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);

                            String regno = sharedPref.getRegNumber();

                            updateprofile(name, url, regno);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Failed to upload your profile picture. Please try again", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });

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
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            saveUserDetails(name, url, regno);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
                            pdialog.dismiss();
                        }
                    }
                }
        );

    }

    private void saveUserDetails(String name, String url, String reg_no) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("url", url);
        user.put("regno", reg_no);

        FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        pdialog.dismiss();
                        Intent intent = new Intent(RegisterActivity.this, ChatRoom.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void pickimage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }


}
