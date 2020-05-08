package studios.luxurious.mmustsolution.mmustChatroom.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.mmustChatroom.BlogDetails;
import studios.luxurious.mmustsolution.mmustChatroom.ProfileActivity;

public class BlogViewHolder extends RecyclerView.ViewHolder {

    View view;
    FirebaseAuth mAuth;
    String user_id;
    Context context;

    public BlogViewHolder(View itemView, Context context) {
        super(itemView);
        view = itemView;
        mAuth = FirebaseAuth.getInstance();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.context = context;
    }

    public void setImage(String url, Context ctx) {
        final ImageView post_image = view.findViewById(R.id.post_image);
        Glide.with(ctx)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.color.grey_fb)
                        .error(R.color.grey_fb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url)
                .into(post_image);


    }

    public void setUser(Activity activity, final String user_id, final Context ctx) {
        final TextView username = view.findViewById(R.id.user_name);
        final CircleImageView userimage = view.findViewById(R.id.profile_image);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(ctx, ProfileActivity.class);
                profile.putExtra("UserId", user_id);
                ctx.startActivity(profile);
            }
        });

        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(ctx, ProfileActivity.class);
                profile.putExtra("UserId", user_id);
                ctx.startActivity(profile);
            }
        });

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(activity, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists() && task.getResult().exists()) {
                        String name = task.getResult().get("name").toString();
                        String url = task.getResult().get("url").toString();
                        username.setText(name);
                        Glide.with(ctx)
                                .applyDefaultRequestOptions(new RequestOptions()
                                        .placeholder(R.drawable.fb_holder)
                                        .error(R.drawable.fb_holder)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                                .load(url)
                                .into(userimage);
                    }
                } else {
                    username.setText("");
                    userimage.setImageResource(R.drawable.fb_holder);
                }
            }
        });

    }

    public void setDate(long time) {
        TextView post_date = view.findViewById(R.id.date);
        TimeFormatter timeFormatter = new TimeFormatter();
        post_date.setText(timeFormatter.getTime(time));
    }

    public void setTitle(String title) {
        TextView head = view.findViewById(R.id.post_title);
        head.setText(title);
    }

    public void setDesc(String desc) {
        TextView post_desc = view.findViewById(R.id.post_desc);
        post_desc.setText(desc);
    }

    public void setLikes(final Activity activity, final String postId) {
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        final Animation animation2 = AnimationUtils.loadAnimation(context, R.anim.zoom_out);


        final TextView like_count = view.findViewById(R.id.like_count);
        final ImageView like_btn = view.findViewById(R.id.like_btn);

        final CollectionReference collection = FirebaseFirestore.getInstance().collection("Posts").document(postId)
                .collection("Likes");


        final EventListener<DocumentSnapshot> checkifLiked = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {
                    like_btn.setImageResource(R.drawable.liked);
                } else {
                    like_btn.setImageResource(R.drawable.unliked);
                }

            }
        };

        final EventListener<QuerySnapshot> likeEvent = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                int likes_int = documentSnapshots.getDocuments().size();
                String likes = String.valueOf(likes_int);
                if (likes.equals("0")) {
                    like_count.setText("");
                } else {
                    like_count.setText(likes);
                }


                updateValueOfLikes(postId, likes_int);

            }
        };

        collection.addSnapshotListener(activity, likeEvent);

        collection.document(user_id).addSnapshotListener(activity, checkifLiked);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                like_btn.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                collection.document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                collection.document(user_id).delete();

                            } else {
                                like_btn.startAnimation(animation);
                                final MediaPlayer sound = MediaPlayer.create(context, R.raw.like_btn_click);
                                sound.start();
                                Map<String, Object> like = new HashMap<>();
                                like.put("UserId", user_id);
                                collection.document(user_id)
                                        .set(like)
                                        .addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        sound.stop();
                                                    }
                                                }
                                        );
                            }
                        } else {
                            Toast.makeText(context, "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void setComments(String Id) {
        //Displaying number of comments

        final TextView commentCount = view.findViewById(R.id.comment_count);


        CollectionReference collection = FirebaseFirestore.getInstance().collection("Posts").document(Id)
                .collection("Comments");
        EventListener<QuerySnapshot> comments = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                String comments = String.valueOf(documentSnapshots.getDocuments().size());

                if (comments.equals("0")) {
                    commentCount.setText("");
                } else {
                    commentCount.setText(comments);
                }
            }
        };
        collection.addSnapshotListener(comments);


    }

    public void showPostDetails(String imgurl, String user, long time, String title, String details, final Activity activity, String id) {

        TextView post_title = view.findViewById(R.id.post_title);
        LinearLayout linearCommentLayout = view.findViewById(R.id.linearCommentLayout);
        ImageView post_image = view.findViewById(R.id.post_image);
        ImageView commentBtn = view.findViewById(R.id.comment_btn);
        TextView commentTextview = view.findViewById(R.id.comment_count);
        TextView post_desc = view.findViewById(R.id.post_desc);

        final Intent post = new Intent(activity, BlogDetails.class);
        post.putExtra("ImageUrl", imgurl);
        post.putExtra("Time", time);
        post.putExtra("User", user);
        post.putExtra("Title", title);
        post.putExtra("Details", details);
        post.putExtra("Id", id);

        post_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post.putExtra("showComments", false);
                activity.startActivity(post);
            }
        });

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post.putExtra("showComments", false);
                activity.startActivity(post);
            }
        });

        post_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post.putExtra("showComments", false);
                activity.startActivity(post);
            }
        });

        linearCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post.putExtra("showComments", true);
                activity.startActivity(post);
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post.putExtra("showComments", true);
                activity.startActivity(post);
            }
        });

        commentTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post.putExtra("showComments", true);
                activity.startActivity(post);
            }
        });


    }

    private void updateValueOfLikes(String postId, int likes) {

        FirebaseFirestore.getInstance().collection("Posts").document(postId).update("Likes", likes);
    }
}