package studios.luxurious.mmustsolution;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import studios.luxurious.mmustsolution.Utils.Download;

public class Downloads extends AppCompatActivity {

    private RecyclerView recyclerview;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);


        recyclerview = findViewById(R.id.rv);
        progressBar = findViewById(R.id.progressBar);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Downloads");
        databaseReference.keepSynced(true);
        FirebaseRecyclerAdapter<Download, DownLoadViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Download, DownLoadViewHolder>(
                Download.class,
                R.layout.download_layout,
                DownLoadViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(final DownLoadViewHolder viewHolder, Download model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.viewBtn.setOnClickListener(v -> {
                    String url = model.getUrl();
                    Intent intent = new Intent(Downloads.this, ViewPdf.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                });
            }
        };

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerview.setAdapter(firebaseRecyclerAdapter);


    }


    public static class DownLoadViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView viewBtn;

        public DownLoadViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            viewBtn = view.findViewById(R.id.view);
        }

        void setName(String name) {
            TextView textView = view.findViewById(R.id.name);
            textView.setText(name);
        }
    }

}
