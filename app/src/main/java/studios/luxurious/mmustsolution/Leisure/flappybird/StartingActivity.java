package studios.luxurious.mmustsolution.Leisure.flappybird;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import studios.luxurious.mmustsolution.R;

public class StartingActivity extends AppCompatActivity {


    int highScore = 0;
    DatabaseReference highscoreReference;
    FirebaseAuth mAuth;
    TextView highScoreTxt;

    int topScoreValue = 0;

    Button startGameBtn;
    MaterialDialog loadingAdDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        highScoreTxt = findViewById(R.id.high_score);
        startGameBtn = findViewById(R.id.startGameBtn);
        startGameBtn.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        highscoreReference = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("Flappy_bird").child(mAuth.getCurrentUser().getUid()).child("highScores");


        boolean refresh = getIntent().getBooleanExtra("refresh",true);

        if (refresh){
            loadHighscores("Please wait...", "Fetching your game progress. \nThis will take about half a minute.");


        }else {
            int highscore = getIntent().getIntExtra("highscore",0);
            highScoreTxt.setText(String.valueOf(highscore));
            highScore = highscore;

            startGameBtn.setEnabled(true);


        }

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartingActivity.this, GameActivity.class);
                intent.putExtra("highScores", highScore);
                intent.putExtra("topScoreValue", topScoreValue);
                startActivity(intent);
                finish();
            }
        });
    }





    public void loadHighscores(String titlee, String message) {

        loadingAdDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_blueprint_gif, false)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .widgetColorRes(R.color.colorPrimary)
                .build();

        View view = loadingAdDialog.getCustomView();
        TextView messageText = view.findViewById(R.id.message);
        TextView title = view.findViewById(R.id.title);
        title.setText(titlee);
        messageText.setText(message);


        RelativeLayout cancelLayout = view.findViewById(R.id.cancel);
        cancelLayout.setVisibility(View.GONE);

        try {
            loadingAdDialog.show();
        } catch (Exception ignored) {}



        highscoreReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    highScore = Integer.parseInt(dataSnapshot.getValue().toString());
                } else {
                    highScore = 0;
                }
                highScoreTxt.setText(String.valueOf(highScore));


                final DatabaseReference topScore = FirebaseDatabase.getInstance().getReference().child("Leaders_board").child("Flappy_bird");

                topScore.orderByChild("highScores").limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot){

                        if (dataSnapshot.exists()) {

                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {


                                if (childSnapshot.hasChild("highScores")) {
                                    topScoreValue = Integer.parseInt(childSnapshot.child("highScores").getValue().toString());
                                } else {
                                    topScoreValue = 0;

                                }


                                startGameBtn.setEnabled(true);

                                loadingAdDialog.dismiss();
                            }
                        }else {
                            startGameBtn.setEnabled(true);

                            loadingAdDialog.dismiss();

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException(); // don't swallow errors
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                loadingAdDialog.dismiss();
                Toast.makeText(StartingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (loadingAdDialog != null) {
            if (loadingAdDialog.isShowing()) {
                loadingAdDialog.dismiss();
                finish();
            }
        }
    }
}
