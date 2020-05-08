package studios.luxurious.mmustsolution.mmustChatroom;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;
import studios.luxurious.mmustsolution.BuildConfig;
import studios.luxurious.mmustsolution.Login.RegistrationNumber;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title;
    private ImageView back_btn;
    private CircleImageView profile_image;
    private CardView name, email, password;
    private TextView profile_name, profile_email, profile_password, version;
    private Button logout_btn;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String photo_url, username, useremail;

    SharedPref sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        //Finding view By ID
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        back_btn = findViewById(R.id.back_btn);
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        profile_password = findViewById(R.id.profile_password);
        version = findViewById(R.id.version);
        logout_btn = findViewById(R.id.logout_btn);

        sharedPref = new SharedPref(this);



        //Changing the Typefaces
        Typeface extravaganzza = Typeface.createFromAsset(getAssets(), "fonts/extravaganzza.ttf");
        title.setTypeface(extravaganzza);
        profile_name.setTypeface(extravaganzza);
        profile_email.setTypeface(extravaganzza);
        profile_password.setTypeface(extravaganzza);
        version.setTypeface(extravaganzza);
        logout_btn.setTypeface(extravaganzza);

        mAuth = FirebaseAuth.getInstance();
        logout_btn.setOnClickListener(v -> {
            mAuth.signOut();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent main =  new Intent(SettingsActivity.this, RegistrationNumber.class);
            startActivity(main);
            finish();
        });
        String version_name = "Version - " + BuildConfig.VERSION_NAME;
        version.setText(version_name);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        user = mAuth.getCurrentUser();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        back_btn.setOnClickListener(v -> SettingsActivity.super.onBackPressed());

        //Setting profile information
        getProfileDetails();


    }

    private void getProfileDetails(){
        if (user != null){
            if (user.getPhotoUrl() != null) {
                photo_url = user.getPhotoUrl().toString();
            }
            String email = sharedPref.getStudentEmail();
            username = user.getDisplayName();
            useremail = email;
            profile_name.setText(username);
            profile_email.setText(useremail);
            setProfileImage(photo_url);
            password.setVisibility(View.VISIBLE);
        }
    }

    private void setProfileImage(String url){
        if (url != null) {
            Glide.with(SettingsActivity.this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .load(url)
                    .into(profile_image);
        }
    }


}
