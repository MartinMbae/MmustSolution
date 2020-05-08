package studios.luxurious.mmustsolution.mmustChatroom.Helper;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.mmustChatroom.BlogDetails;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


public class PostsViewHolder extends RecyclerView.ViewHolder {

    private View view;
    private ImageView post_image;

    public PostsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        post_image = view.findViewById(R.id.post_image);
    }

    public void showPostDetails(String imgurl, String user, long time, String title, String details, String postId, final Activity activity){

        Glide.with(activity)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.color.grey_fb)
                        .error(R.color.grey_fb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(imgurl)
                .into(post_image);


        final Intent post = new Intent(activity, BlogDetails.class);
        post.putExtra("ImageUrl", imgurl);
        post.putExtra("Time", time);
        post.putExtra("User", user);
        post.putExtra("Title", title);
        post.putExtra("Details", details);
        post.putExtra("Id", postId);

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(post);
            }
        });
    }
}
