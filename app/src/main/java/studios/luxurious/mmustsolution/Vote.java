package studios.luxurious.mmustsolution;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Vote extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        String str2 = "The selected option is not available.";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Vote.this);
        alertDialog.setTitle("Currently not Available")
                .setMessage(str2)
                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent d = new Intent(Vote.this,HomeActivity.class);
                        d.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(d);
                        finish();

                    }
                })
                .setCancelable(false)
                .show();


    }
}