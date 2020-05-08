package studios.luxurious.mmustsolution.mmustChatroom;


import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import androidx.appcompat.app.AppCompatActivity;
import studios.luxurious.mmustsolution.R;

//postImage
public class FullImage extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);


        imageView  = findViewById(R.id.postImage);


        Bundle bundle = getIntent().getExtras();
        final String ImageUrl = bundle.getString("ImageUrl");


        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.color.grey_fb)
                        .error(R.color.grey_fb)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(ImageUrl)
                .into(imageView);

    }

}
