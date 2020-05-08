package studios.luxurious.mmustsolution.mmustChatroom;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.mmustChatroom.Helper.PostsViewHolder;
import studios.luxurious.mmustsolution.mmustChatroom.Modal.Blog;

public class ProfileActivity extends AppCompatActivity {

    private ImageView back_btn, header_image;
    private CircleImageView profile_image;
    private TextView  profile_name, error;
    private RecyclerView post_list;
    private FirebaseAuth mAuth;
    Button follow_btn;
    private String user_id, name, photo_url;
    private FirestoreRecyclerAdapter<Blog, PostsViewHolder> adapter;
    private FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back_btn = findViewById(R.id.back_btn);
        follow_btn = findViewById(R.id.follow_btn);
        header_image = findViewById(R.id.header_image);
        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);
        post_list = findViewById(R.id.post_list);
        error = findViewById(R.id.error);
        add = findViewById(R.id.add);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        header_image.setVisibility(View.INVISIBLE);
        header_image.setColorFilter(Color.rgb(220, 220, 220), android.graphics.PorterDuff.Mode.MULTIPLY);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });


        //Initializing add button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, PostActivity.class));
            }
        });

        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            }
        });

        //Initializing RecyclerView
        populateBlogList();
        post_list.setAdapter(adapter);
        post_list.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        post_list.setLayoutManager(gridLayoutManager);
        adapter.startListening();

        if (adapter.getItemCount() == 0){
            showError();
        }else {
            error.setVisibility(View.GONE);
        }


        //Changing Typefaces
        Typeface extravaganzza = Typeface.createFromAsset(getAssets(), "fonts/extravaganzza.ttf");

        profile_name.setTypeface(extravaganzza);

        //Setting user details
        setUserDetails();
    }

    private void populateBlogList(){

        Query query = FirebaseFirestore.getInstance()
                .collection("Posts")
                .whereEqualTo("User", user_id);

        FirestoreRecyclerOptions<Blog> options = new FirestoreRecyclerOptions.Builder<Blog>()
                .setQuery(query, Blog.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Blog, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, int position, @NonNull Blog model) {

                holder.showPostDetails(model.getImage(), model.getUser(), model.getTime(), model.getTitle(), model.getDetails(), model.getID(),
                        ProfileActivity.this);

            }

            @Override
            public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.post_grid, parent, false);
                return new PostsViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                if (adapter.getItemCount() == 0){
                    showError();
                }else {
                    error.setVisibility(View.GONE);
                }

            }
        };

    }

    private void setUserDetails(){


        if (itsMe()){

            final String user_id = mAuth.getCurrentUser().getUid();
            name = mAuth.getCurrentUser().getDisplayName();


            if (mAuth.getCurrentUser().getPhotoUrl() != null) {
                photo_url = mAuth.getCurrentUser().getPhotoUrl().toString();
            }
            DocumentReference user_doc = FirebaseFirestore.getInstance().collection("Users").document(user_id);

            profile_name.setText(name);
            Glide.with(ProfileActivity.this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .load(photo_url)
                    .into(profile_image);

            Glide.with(ProfileActivity.this)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .load(photo_url)
                    .into(header_image);

            blurImage();


        }else {


            final DocumentReference user_doc = FirebaseFirestore.getInstance().collection("Users").document(user_id);

            user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful() && task.getResult().exists()){

                        name = task.getResult().get("name").toString();
                        photo_url = task.getResult().get("url").toString();

                        profile_name.setText(name);
                        Glide.with(ProfileActivity.this)
                                .applyDefaultRequestOptions(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                .load(photo_url)
                                .into(profile_image);

                        Glide.with(ProfileActivity.this)
                                .applyDefaultRequestOptions(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                .load(photo_url)
                                .into(header_image);

                        blurImage();


                    }

                }
            });



        }
    }

    private boolean itsMe(){
        if (mAuth.getCurrentUser() != null) {
            String current_user = mAuth.getCurrentUser().getUid();

            if (current_user.equals(user_id)) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }

    }

    private void blurImage(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                header_image.setVisibility(View.VISIBLE);
                Blurry.with(ProfileActivity.this)
                        .radius(30)
                        .sampling(1)
                        .async()
                        .capture(findViewById(R.id.header_image))
                        .into((ImageView) findViewById(R.id.header_image));
            }
        }, 1000);
    }

    private void showError(){

        error.setVisibility(View.VISIBLE);
        if (itsMe()){
            error.setText("You haven't posted anything yet !");
        }else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (name!=null){
                        error.setText(name + " hasn't posted anything yet !");
                    }
                }
            }, 1200);
        }
    }
}
