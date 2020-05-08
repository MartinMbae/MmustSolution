package studios.luxurious.mmustsolution.Leisure.LeaderBoard;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;

public class LeaderBoardFlappyBird extends Fragment {
    private DatabaseReference highscoreReference;
    RecyclerView recycler_view;
    ProgressDialog progressDialog;


    FirebaseAuth mAuth;
    String myUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_learder_board, container, false);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Updating Leader-board");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();


        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getCurrentUser().getUid();



        highscoreReference = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("Flappy_bird");
        highscoreReference.keepSynced(true);


        recycler_view = view.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setNestedScrollingEnabled(false);

        FirebaseRecyclerAdapter<Leader, LeaderViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Leader, LeaderViewHolder>(
                Leader.class,
                R.layout.item_player,
                LeaderViewHolder.class,
                highscoreReference.orderByChild("highScores").limitToLast(50)

        ) {
            @Override
            protected void populateViewHolder(final LeaderViewHolder viewHolderid, Leader model, final int positionid) {


                final String uid = getRef(positionid).getKey();

                String highscores = String.valueOf(model.getHighScores());

                int position = getItemCount() - (positionid);
                viewHolderid.positionTextview.setText(String.valueOf(position));

                viewHolderid.scoreTextview.setText(highscores);

                FirebaseFirestore.getInstance().collection("Users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        if (documentSnapshot != null) {
                            String name = documentSnapshot.getString("name");
                            viewHolderid.nameTextview.setText(name);
                        }
                    }
                });




                if (uid.equals(myUid)){

                    LinearLayout myLinearLayout = viewHolderid.view.findViewById(R.id.myNameHolder);
                    LinearLayout myLinearLayout2 = viewHolderid.view.findViewById(R.id.myPointsHolder);
                    myLinearLayout.setBackgroundColor(getResources().getColor(R.color.dk_green));
                    myLinearLayout2.setBackgroundColor(getResources().getColor(R.color.dk_green));

                }




            }


        };

        recycler_view.setAdapter(firebaseRecyclerAdapter);

        highscoreReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    TextView textView = view.findViewById(R.id.nodatafound);
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        highscoreReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

        return view;
    }


    public static class LeaderViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView scoreTextview, nameTextview,positionTextview;

        public LeaderViewHolder(View itemView) {
            super(itemView);

            view = itemView;

            scoreTextview = view.findViewById(R.id.tv_score);
            nameTextview = view.findViewById(R.id.tv_name);
            positionTextview = view.findViewById(R.id.tv_position);

        }


    }

    private boolean IsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;

        } else {
            return false;
        }


    }

}
