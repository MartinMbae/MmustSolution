package studios.luxurious.mmustsolution.mmustChatroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import studios.luxurious.mmustsolution.R;

public class PostActivity extends AppCompatActivity {

    private Uri post_image_uri;
    private ImageView post_image;
    private EditText post_title, post_details;
    private ProgressDialog pdialog;
    private FirebaseAuth mAuth;
    public static final int PICK_IMAGE = 1;
    Button submit_post_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //Finding View By Id
        mAuth = FirebaseAuth.getInstance();
        TextView header = findViewById(R.id.header);
        CircleImageView profileimage = findViewById(R.id.profileimage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        post_title = findViewById(R.id.post_title);
        post_details = findViewById(R.id.post_details);
        post_image = findViewById(R.id.post_image);
        submit_post_btn = findViewById(R.id.submit_post_btn);
        submit_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit_post_btn.setEnabled(false);
                if (allClear()){
                    String uid = mAuth.getCurrentUser().getUid();
                    String postId = uid+ generateRandomNumber(6)+generateRandomNumber(5);
                    uploadPostImage(postId);
                }else{

                    submit_post_btn.setEnabled(true);
                }
            }
        });

        //Customizing toolbar
        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        header.setText("Post Blog");
        header.setTypeface(pacifico);
        profileimage.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //Setting post image to imageview
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(PostActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PostActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        pickimage();
                    }
                } else {
                    pickimage();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                post_image_uri = data.getData();
                CropImage.activity(post_image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);

            } else {
                Toast.makeText(this, "Unable to load Image", Toast.LENGTH_LONG).show();
            }


        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                post_image_uri = result.getUri();
                post_image.setImageURI(post_image_uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception er = result.getError();
            }

            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void pickimage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private boolean allClear() {
        if (post_image_uri == null) {
            toast("Please select an Image");
            return false;
        }else if (TextUtils.isEmpty(post_title.getText().toString())){
            post_title.setError("Title can't be Empty");
            return false;
        }else if (TextUtils.isEmpty(post_details.getText().toString())){
            post_details.setError("Detail can't be Empty");
            return false;
        }else {
            return true;
        }
    }

    private void uploadPostImage(final String randomId) {

        submit_post_btn.setEnabled(false);

        pdialog = new ProgressDialog(PostActivity.this, R.style.MyAlertDialogStyle);
        pdialog.setMessage("Please wait...");
        pdialog.setIndeterminate(true);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.setCancelable(false);
        pdialog.show();



        if (post_image_uri != null) {

            final StorageReference filePath =  FirebaseStorage.getInstance().getReference().child("post_images/" + randomId + ".png");

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), post_image_uri);
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
                    Toast.makeText(PostActivity.this, "Failed to upload your post. Please try again", Toast.LENGTH_SHORT).show();


                    submit_post_btn.setEnabled(true);
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            savePostDetails(uri.toString());

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, "Failed to upload your post. Please try again", Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            });

        }else{
            Toast.makeText(this, "no image", Toast.LENGTH_SHORT).show();

            submit_post_btn.setEnabled(true);
        }
    }

    private void savePostDetails(String imageUrl) {
        String title = post_title.getText().toString();
        String details = post_details.getText().toString();
        String desc = details.substring(0, Math.min(details.length(), 250));

        String user = mAuth.getCurrentUser().getUid();

        String postId = user + generateRandomNumber(6)+generateRandomNumber(5);
        Map<String, Object> post = new HashMap<>();
        post.put("User", user);
        post.put("Image", imageUrl);
        post.put("Time", System.currentTimeMillis());
        post.put("Title", title);
        post.put("Desc", desc);
        post.put("Details", details);
        post.put("Id", postId);
        post.put("Likes", 0);

        FirebaseFirestore.getInstance().collection("Posts").document(postId)
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pdialog.dismiss();
                        post_title.setText("");
                        post_details.setText("");
                        post_image.setImageResource(R.color.grey_fb);

                        startActivity(new Intent(PostActivity.this, ChatRoom.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();

                        submit_post_btn.setEnabled(true);
                    }
                });
    }

    private void toast(String message){
        Toast.makeText(PostActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public String generateRandomNumber(int lenght){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        while (stringBuilder.length() < lenght){
            int index = (int) (random.nextFloat() * characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();

    }
}
